#!/usr/bin/env python3
"""Generate BlueMap-only blockstate overrides for LanteaCraft compatibility."""

from __future__ import annotations

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SOURCE_BLOCKSTATES = ROOT / "src" / "main" / "resources" / "assets" / "lanteacraft" / "blockstates"
PACK = ROOT / "compat" / "bluemap" / "lanteacraft-stargates"
BLOCKSTATES = PACK / "assets" / "lanteacraft" / "blockstates"
MINECRAFT_BLOCKSTATES = PACK / "assets" / "minecraft" / "blockstates"
MODELS = PACK / "assets" / "lanteacraft" / "models" / "block"

OBJ_BLOCKSTATES = (
    "dhd",
    "nox_dhd",
    "pegasus_dhd",
    "wraith_dhd",
    "naquadah_generator",
    "zpm_hub",
)

BANNER_COLORS = (
    "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
    "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black",
)

STANDING_ROTATIONS = (180, 202, 225, 247, 270, 292, 315, 337, 0, 22, 45, 67, 90, 112, 135, 157)
WALL_ROTATIONS = {"east": 90, "south": 180, "west": 270, "north": 0}


def write_json(path: Path, value: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2) + "\n", encoding="utf-8", newline="\n")


def add_renderer(value: object, renderer: str) -> None:
    if isinstance(value, dict):
        if "model" in value:
            value["renderer"] = renderer
        for child in value.values():
            add_renderer(child, renderer)
    elif isinstance(value, list):
        for child in value:
            add_renderer(child, renderer)


def generate_obj_blockstates() -> None:
    for name in OBJ_BLOCKSTATES:
        value = json.loads((SOURCE_BLOCKSTATES / f"{name}.json").read_text(encoding="utf-8"))
        add_renderer(value, "lanteacraft:obj")
        write_json(BLOCKSTATES / f"{name}.json", value)

    write_json(BLOCKSTATES / "obelisk.json", {
        "variants": {
            "": {
                "model": "lanteacraft:block/obelisk_bluemap",
                "renderer": "lanteacraft:obj",
            }
        }
    })


def variant(model: str, rotation: int) -> dict[str, object]:
    result: dict[str, object] = {"model": model, "renderer": "lanteacraft:banner"}
    if rotation:
        result["y"] = rotation
    return result


def generate_banner_blockstates() -> None:
    for color in BANNER_COLORS:
        standing = {
            "variants": {
                f"rotation={index}": variant("minecraft:entity/banner", rotation)
                for index, rotation in enumerate(STANDING_ROTATIONS)
            }
        }
        wall = {
            "variants": {
                f"facing={facing}": variant("minecraft:entity/wall_banner", rotation)
                for facing, rotation in WALL_ROTATIONS.items()
            }
        }
        # These override BlueMap's simplified vanilla banner blockstates.
        write_json(MINECRAFT_BLOCKSTATES / f"{color}_banner.json", standing)
        write_json(MINECRAFT_BLOCKSTATES / f"{color}_wall_banner.json", wall)


def generate_texture_models() -> None:
    write_json(MODELS / "obelisk_bluemap.json", {
        "textures": {
            "map_Obelisk_01": "lanteacraft:block/obelisk",
            "particle": "lanteacraft:block/obelisk",
        }
    })
    write_json(MODELS / "bluemap_extra_textures.json", {
        "textures": {
            "zpm": "lanteacraft:item/zpm_glb",
            "stargate_banner": "lanteacraft:entity/banner/stargate",
            "lantean_banner": "lanteacraft:entity/banner/lantean",
            "goauld_banner": "lanteacraft:entity/banner/goauld",
        }
    })


def main() -> None:
    generate_obj_blockstates()
    generate_banner_blockstates()
    generate_texture_models()
    print(f"Generated BlueMap OBJ and banner compatibility assets in {PACK}")


if __name__ == "__main__":
    main()
