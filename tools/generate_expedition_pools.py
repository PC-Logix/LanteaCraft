#!/usr/bin/env python3
"""Generate LanteaCraft expedition jigsaw template pools."""

from __future__ import annotations

import argparse
import json
import shutil
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
STRUCTURE_ROOT = ROOT / "src/main/resources/data/lanteacraft/structure"
EXPEDITION_ROOT = STRUCTURE_ROOT / "expedition"
POOL_ROOT = ROOT / "src/main/resources/data/lanteacraft/worldgen/template_pool/expedition"

POOLS = {
    "start": {
        "fallback": "lanteacraft:expedition/terminators",
        "explicit": ["gate_room.nbt"],
        "prefixes": [],
        "dirs": ["start"],
    },
    "halls": {
        "fallback": "lanteacraft:expedition/terminators",
        "explicit": [],
        "prefixes": ["hall_", "hall_left_", "hall_right_"],
        "dirs": ["halls"],
    },
    "rooms": {
        "fallback": "lanteacraft:expedition/terminators",
        "explicit": [],
        "prefixes": ["room_", "intersection_"],
        "dirs": ["rooms"],
    },
    "combat_rooms": {
        "fallback": "lanteacraft:expedition/terminators",
        "explicit": [],
        "prefixes": ["combat_room_"],
        "dirs": ["combat_rooms", "combat"],
    },
    "reward": {
        "fallback": "minecraft:empty",
        "explicit": ["reward_room.nbt"],
        "prefixes": ["reward_"],
        "dirs": ["reward"],
    },
    "terminators": {
        "fallback": "minecraft:empty",
        "explicit": [],
        "prefixes": ["terminator_", "end_cap_", "dead_end_"],
        "dirs": ["terminators"],
    },
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--import-generated",
        type=Path,
        help="Optional generated/lanteacraft/structures/expedition folder to copy into packaged resources.",
    )
    return parser.parse_args()


def copy_generated(import_root: Path) -> int:
    if not import_root.exists():
        raise SystemExit(f"Generated structure folder does not exist: {import_root}")
    copied = 0
    for source in sorted(import_root.rglob("*.nbt")):
        relative = source.relative_to(import_root)
        target = EXPEDITION_ROOT / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)
        copied += 1
    return copied


def expedition_structures() -> list[Path]:
    if not EXPEDITION_ROOT.exists():
        return []
    return sorted(EXPEDITION_ROOT.rglob("*.nbt"))


def matches_pool(path: Path, pool: dict[str, list[str] | str]) -> bool:
    relative = path.relative_to(EXPEDITION_ROOT).as_posix()
    name = path.name
    if relative in pool["explicit"] or name in pool["explicit"]:
        return True
    if any(relative.startswith(f"{directory}/") for directory in pool["dirs"]):
        return True
    return any(name.startswith(prefix) for prefix in pool["prefixes"])


def structure_location(path: Path) -> str:
    relative = path.relative_to(STRUCTURE_ROOT).with_suffix("").as_posix()
    if "structures" in relative.split("/"):
        raise SystemExit(f"Invalid packaged structure path contains plural 'structures': {path}")
    return f"lanteacraft:{relative}"


def entry_weight(pool_name: str, path: Path) -> int:
    name = path.stem
    if pool_name == "halls":
        if name.startswith(("hall_left_", "hall_right_")):
            return 5
        return 2
    if pool_name == "rooms":
        if name.startswith("intersection_"):
            return 8
        if name.startswith("room_combat_"):
            return 5
        if name.startswith(("room_reward_", "room_final_")):
            return 1
        return 3
    return 1


def pool_json(pool_name: str, paths: list[Path], fallback: str) -> dict:
    return {
        "fallback": fallback,
        "elements": [
            {
                "weight": entry_weight(pool_name, path),
                "element": {
                    "element_type": "minecraft:single_pool_element",
                    "location": structure_location(path),
                    "projection": "rigid",
                    "processors": "minecraft:empty",
                },
            }
            for path in paths
        ],
    }


def write_pool(name: str, data: dict) -> None:
    POOL_ROOT.mkdir(parents=True, exist_ok=True)
    path = POOL_ROOT / f"{name}.json"
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def main() -> int:
    args = parse_args()
    if args.import_generated:
        copied = copy_generated(args.import_generated)
        print(f"Imported {copied} generated expedition structure(s).")

    structures = expedition_structures()
    print(f"Found {len(structures)} packaged expedition structure(s).")
    for name, pool in POOLS.items():
        paths = [path for path in structures if matches_pool(path, pool)]
        write_pool(name, pool_json(name, paths, str(pool["fallback"])))
        print(f"Generated {name}.json with {len(paths)} element(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
