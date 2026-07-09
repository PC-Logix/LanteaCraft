#!/usr/bin/env python3
"""Validate LanteaCraft expedition jigsaw structures and template pools."""

from __future__ import annotations

import argparse
import gzip
import json
import struct
from dataclasses import dataclass
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
PACKAGED_STRUCTURE_ROOT = ROOT / "src/main/resources/data/lanteacraft/structure"
PACKAGED_EXPEDITION_ROOT = PACKAGED_STRUCTURE_ROOT / "expedition"
BAD_PLURAL_STRUCTURE_ROOT = ROOT / "src/main/resources/data/lanteacraft/structures"
POOL_ROOT = ROOT / "src/main/resources/data/lanteacraft/worldgen/template_pool/expedition"
DOOR = "lanteacraft:expedition/door"
EMPTY = "minecraft:empty"

TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12


@dataclass(frozen=True)
class Jigsaw:
    structure_id: str
    pos: tuple[int, int, int]
    state: dict[str, Any]
    name: str
    target: str
    pool: str
    final_state: str
    joint: str


class NbtReader:
    def __init__(self, data: bytes):
        self.data = data
        self.offset = 0

    def read(self, length: int) -> bytes:
        chunk = self.data[self.offset : self.offset + length]
        if len(chunk) != length:
            raise ValueError("Unexpected end of NBT data")
        self.offset += length
        return chunk

    def u8(self) -> int:
        return self.read(1)[0]

    def i8(self) -> int:
        return struct.unpack(">b", self.read(1))[0]

    def i16(self) -> int:
        return struct.unpack(">h", self.read(2))[0]

    def i32(self) -> int:
        return struct.unpack(">i", self.read(4))[0]

    def i64(self) -> int:
        return struct.unpack(">q", self.read(8))[0]

    def f32(self) -> float:
        return struct.unpack(">f", self.read(4))[0]

    def f64(self) -> float:
        return struct.unpack(">d", self.read(8))[0]

    def string(self) -> str:
        length = struct.unpack(">H", self.read(2))[0]
        return self.read(length).decode("utf-8")

    def root(self) -> dict[str, Any]:
        tag_type = self.u8()
        if tag_type != TAG_COMPOUND:
            raise ValueError("Root NBT tag is not a compound")
        _name = self.string()
        return self.payload(TAG_COMPOUND)

    def payload(self, tag_type: int) -> Any:
        if tag_type == TAG_BYTE:
            return self.i8()
        if tag_type == TAG_SHORT:
            return self.i16()
        if tag_type == TAG_INT:
            return self.i32()
        if tag_type == TAG_LONG:
            return self.i64()
        if tag_type == TAG_FLOAT:
            return self.f32()
        if tag_type == TAG_DOUBLE:
            return self.f64()
        if tag_type == TAG_BYTE_ARRAY:
            return list(self.read(self.i32()))
        if tag_type == TAG_STRING:
            return self.string()
        if tag_type == TAG_LIST:
            element_type = self.u8()
            length = self.i32()
            return [self.payload(element_type) for _ in range(length)]
        if tag_type == TAG_COMPOUND:
            value = {}
            while True:
                child_type = self.u8()
                if child_type == TAG_END:
                    return value
                child_name = self.string()
                value[child_name] = self.payload(child_type)
        if tag_type == TAG_INT_ARRAY:
            return [self.i32() for _ in range(self.i32())]
        if tag_type == TAG_LONG_ARRAY:
            return [self.i64() for _ in range(self.i32())]
        raise ValueError(f"Unsupported NBT tag type {tag_type}")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--generated",
        type=Path,
        help="Optional generated/lanteacraft/structures/expedition folder to validate.",
    )
    return parser.parse_args()


def read_nbt(path: Path) -> dict[str, Any]:
    data = path.read_bytes()
    try:
        data = gzip.decompress(data)
    except OSError:
        pass
    return NbtReader(data).root()


def structure_id(path: Path, root: Path) -> str:
    relative = path.relative_to(root).with_suffix("").as_posix()
    return f"lanteacraft:{relative}"


def load_structures(root: Path, structure_root: Path) -> dict[str, Path]:
    if not root.exists():
        return {}
    return {structure_id(path, structure_root): path for path in sorted(root.rglob("*.nbt"))}


def load_pools() -> dict[str, dict[str, Any]]:
    pools = {}
    if not POOL_ROOT.exists():
        return pools
    for path in sorted(POOL_ROOT.glob("*.json")):
        pools[f"lanteacraft:expedition/{path.stem}"] = json.loads(path.read_text(encoding="utf-8"))
    return pools


def pool_locations(pool: dict[str, Any]) -> list[str]:
    locations = []
    for entry in pool.get("elements", []):
        element = entry.get("element", {})
        if element.get("element_type") == "minecraft:empty_pool_element":
            continue
        location = element.get("location")
        if isinstance(location, str):
            locations.append(location)
    return locations


def state_name(state: dict[str, Any]) -> str:
    return str(state.get("Name", ""))


def state_properties(state: dict[str, Any]) -> dict[str, Any]:
    props = state.get("Properties", {})
    return props if isinstance(props, dict) else {}


def block_pos(block: dict[str, Any]) -> tuple[int, int, int]:
    pos = block.get("pos", [0, 0, 0])
    return int(pos[0]), int(pos[1]), int(pos[2])


def find_jigsaws(structure_id_: str, path: Path) -> list[Jigsaw]:
    tag = read_nbt(path)
    palette = tag.get("palette", [])
    blocks = tag.get("blocks", [])
    jigsaws = []
    for block in blocks:
        state_index = int(block.get("state", -1))
        if state_index < 0 or state_index >= len(palette):
            continue
        state = palette[state_index]
        if state_name(state) != "minecraft:jigsaw":
            continue
        nbt = block.get("nbt", {})
        jigsaws.append(
            Jigsaw(
                structure_id=structure_id_,
                pos=block_pos(block),
                state=state,
                name=str(nbt.get("name", EMPTY)),
                target=str(nbt.get("target", EMPTY)),
                pool=str(nbt.get("pool", EMPTY)),
                final_state=str(nbt.get("final_state", "")),
                joint=str(nbt.get("joint", "")),
            )
        )
    return jigsaws


def is_terminator_structure(structure_id_: str) -> bool:
    path = structure_id_.split(":", 1)[1]
    return any(part in path for part in ("terminator", "end_cap", "dead_end"))


def validate_resource_location(location: str, errors: list[str]) -> None:
    if "structures" in location.split(":")[-1].split("/"):
        errors.append(f"Pool location must not contain plural 'structures': {location}")
    if not location.startswith("lanteacraft:expedition/"):
        errors.append(f"Invalid expedition structure location: {location}")


def main() -> int:
    args = parse_args()
    errors: list[str] = []
    if BAD_PLURAL_STRUCTURE_ROOT.exists():
        errors.append(f"Packaged structures must use singular 'structure', found: {BAD_PLURAL_STRUCTURE_ROOT}")

    structures = load_structures(PACKAGED_EXPEDITION_ROOT, PACKAGED_STRUCTURE_ROOT)
    if args.generated:
        structures.update(load_structures(args.generated, args.generated.parents[0]))

    pools = load_pools()
    jigsaws_by_structure = {sid: find_jigsaws(sid, path) for sid, path in structures.items()}

    print("Expedition jigsaw validation")
    print("Rule: previous_jigsaw.target == candidate_piece_jigsaw.name")
    print(f"Java start rule: at least one start-pool structure needs name = {DOOR}")
    print()
    for sid in sorted(structures):
        jigsaws = jigsaws_by_structure[sid]
        print(f"{sid}: {len(jigsaws)} jigsaw(s)")
        for jigsaw in jigsaws:
            print(
                "  pos={pos} orientation={orientation} name={name} target={target} pool={pool} final_state={final_state} joint={joint}".format(
                    pos=jigsaw.pos,
                    orientation=state_properties(jigsaw.state).get("orientation", ""),
                    name=jigsaw.name,
                    target=jigsaw.target,
                    pool=jigsaw.pool,
                    final_state=jigsaw.final_state,
                    joint=jigsaw.joint,
                )
            )

    start_pool = pools.get("lanteacraft:expedition/start")
    start_locations = pool_locations(start_pool) if start_pool else []
    if not any(j.name == DOOR for loc in start_locations for j in jigsaws_by_structure.get(loc, [])):
        errors.append(f"No start-pool structure contains a jigsaw with name = {DOOR}")

    for pool_id, pool in pools.items():
        for location in pool_locations(pool):
            validate_resource_location(location, errors)
            if location not in structures:
                errors.append(f"Pool {pool_id} references missing structure NBT: {location}")

    all_names_by_structure = {sid: {j.name for j in jigsaws} for sid, jigsaws in jigsaws_by_structure.items()}
    for sid, jigsaws in jigsaws_by_structure.items():
        for jigsaw in jigsaws:
            if jigsaw.name == EMPTY and not is_terminator_structure(sid):
                errors.append(f"{sid} jigsaw at {jigsaw.pos} has name=minecraft:empty")
            if jigsaw.target != EMPTY and jigsaw.pool == EMPTY:
                errors.append(f"{sid} jigsaw at {jigsaw.pos} has target={jigsaw.target} but pool=minecraft:empty")
            if jigsaw.pool != EMPTY and jigsaw.target == EMPTY:
                errors.append(f"{sid} jigsaw at {jigsaw.pos} has pool={jigsaw.pool} but target=minecraft:empty")
            if jigsaw.pool != EMPTY and jigsaw.pool not in pools:
                errors.append(f"{sid} jigsaw at {jigsaw.pos} references missing pool JSON: {jigsaw.pool}")
            if jigsaw.pool in pools and jigsaw.target != EMPTY:
                candidate_locations = pool_locations(pools[jigsaw.pool])
                candidate_names = set()
                for location in candidate_locations:
                    candidate_names.update(all_names_by_structure.get(location, set()))
                if jigsaw.target not in candidate_names:
                    errors.append(
                        f"{sid} jigsaw at {jigsaw.pos} target {jigsaw.target} has no matching jigsaw name in pool {jigsaw.pool}"
                    )

    print()
    if errors:
        print("Validation failed:")
        for error in errors:
            print(f"- {error}")
        return 1
    print("Validation passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
