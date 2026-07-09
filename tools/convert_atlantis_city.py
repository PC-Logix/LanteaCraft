#!/usr/bin/env python3
"""Convert the raw Atlantis block JSON into compact chunked resources.

Input format:
    [[[x, y, z], "minecraft:block"], ...]

Output:
    src/main/resources/data/lanteacraft/atlantis_city/city.json
    src/main/resources/data/lanteacraft/atlantis_city/city.lacb
"""

from __future__ import annotations

import argparse
import json
import struct
import zlib
from collections import Counter, defaultdict
from pathlib import Path


DEFAULT_REMAP = {
    "minecraft:cobblestone": "lanteacraft:lantean_wall",
    "minecraft:polished_blackstone_bricks": "lanteacraft:lantean_panel",
    "minecraft:black_concrete_powder": "lanteacraft:lantean_dark_trim",
    "minecraft:mud": "lanteacraft:lantean_carved_wall",
    "minecraft:acacia_log": "lanteacraft:lantean_light_panel",
    "minecraft:black_wool": "lanteacraft:lantean_glass",
    "minecraft:light_gray_concrete_powder": "lanteacraft:lantean_wall",
    "minecraft:red_wool": "lanteacraft:lantean_light_panel",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--input",
        default="Atlantis_City.json",
        help="raw city JSON exported as [[[x,y,z], block], ...]",
    )
    parser.add_argument(
        "--output",
        default="src/main/resources/data/lanteacraft/atlantis_city",
        help="output resource directory",
    )
    parser.add_argument(
        "--no-remap",
        action="store_true",
        help="keep source block ids instead of remapping to LanteaCraft blocks",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    source = Path(args.input)
    output = Path(args.output)
    output.mkdir(parents=True, exist_ok=True)

    with source.open("r", encoding="utf-8") as handle:
        blocks = json.load(handle)

    remap = {} if args.no_remap else DEFAULT_REMAP
    palette = []
    palette_index = {}
    source_counts = Counter()
    output_counts = Counter()
    chunks = defaultdict(list)

    min_x = min_y = min_z = 1 << 30
    max_x = max_y = max_z = -(1 << 30)

    for entry in blocks:
        (x, y, z), source_block = entry
        block = remap.get(source_block, source_block)
        source_counts[source_block] += 1
        output_counts[block] += 1

        if block not in palette_index:
            palette_index[block] = len(palette)
            palette.append(block)

        min_x = min(min_x, x)
        min_y = min(min_y, y)
        min_z = min(min_z, z)
        max_x = max(max_x, x)
        max_y = max(max_y, y)
        max_z = max(max_z, z)

        cx = x >> 4
        cz = z >> 4
        chunks[(cx, cz)].append((x & 15, y, z & 15, palette_index[block]))

    binary_path = output / "city.lacb"
    index_chunks = []
    offset = 0

    with binary_path.open("wb") as binary:
        binary.write(b"LCAC")
        binary.write(struct.pack(">HH", 1, len(palette)))
        offset = binary.tell()

        for (cx, cz), entries in sorted(chunks.items()):
            entries.sort(key=lambda row: (row[1], row[2], row[0], row[3]))
            payload = bytearray()
            payload.extend(struct.pack(">I", len(entries)))
            for local_x, y, local_z, palette_id in entries:
                payload.extend(struct.pack(">BhBH", local_x, y, local_z, palette_id))

            compressed = zlib.compress(bytes(payload), level=9)
            binary.write(compressed)
            index_chunks.append(
                {
                    "chunk_x": cx,
                    "chunk_z": cz,
                    "offset": offset,
                    "length": len(compressed),
                    "blocks": len(entries),
                }
            )
            offset += len(compressed)

    metadata = {
        "format": "lanteacraft_atlantis_city",
        "version": 1,
        "source": source.name,
        "binary": binary_path.name,
        "block_count": len(blocks),
        "bounds": {
            "min": [min_x, min_y, min_z],
            "max": [max_x, max_y, max_z],
            "size": [max_x - min_x + 1, max_y - min_y + 1, max_z - min_z + 1],
        },
        "palette": palette,
        "source_counts": dict(sorted(source_counts.items())),
        "output_counts": dict(sorted(output_counts.items())),
        "remap": remap,
        "chunks": index_chunks,
    }

    with (output / "city.json").open("w", encoding="utf-8") as handle:
        json.dump(metadata, handle, indent=2)
        handle.write("\n")

    print(f"Converted {len(blocks):,} blocks")
    print(f"Bounds: x={min_x}..{max_x}, y={min_y}..{max_y}, z={min_z}..{max_z}")
    print(f"Palette: {len(palette)} blocks")
    print(f"Chunks: {len(index_chunks):,}")
    print(f"Wrote {output / 'city.json'}")
    print(f"Wrote {binary_path} ({binary_path.stat().st_size:,} bytes)")


if __name__ == "__main__":
    main()
