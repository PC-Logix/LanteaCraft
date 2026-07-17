#!/usr/bin/env python3
"""Add bounded explicit UVs to block-model elements outside the 0..16 cube."""

from __future__ import annotations

import argparse
import difflib
import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MODEL_DIR = ROOT / "src/main/resources/assets/lanteacraft/models/block"


def default_uv(direction: str, start: list[float], end: list[float]) -> list[float]:
    x1, y1, z1 = start
    x2, y2, z2 = end
    return {
        "down": [x1, 16 - z2, x2, 16 - z1],
        "up": [x1, z1, x2, z2],
        "north": [16 - x2, 16 - y2, 16 - x1, 16 - y1],
        "south": [x1, 16 - y2, x2, 16 - y1],
        "west": [z1, 16 - y2, z2, 16 - y1],
        "east": [16 - z2, 16 - y2, 16 - z1, 16 - y1],
    }[direction]


def shift_inside(a: float, b: float) -> tuple[float, float]:
    low, high = min(a, b), max(a, b)
    if high - low > 16:
        return (0, 16) if a <= b else (16, 0)
    shift = -low if low < 0 else 16 - high if high > 16 else 0
    return a + shift, b + shift


def bounded_uv(direction: str, start: list[float], end: list[float]) -> list[float]:
    u1, v1, u2, v2 = default_uv(direction, start, end)
    u1, u2 = shift_inside(u1, u2)
    v1, v2 = shift_inside(v1, v2)
    return [u1, v1, u2, v2]


def clean_numbers(values: list[float]) -> list[int | float]:
    return [int(value) if float(value).is_integer() else value for value in values]


def add_uvs(model: dict) -> int:
    added = 0
    for element in model.get("elements", []):
        start, end = element["from"], element["to"]
        if all(0 <= coordinate <= 16 for coordinate in (*start, *end)):
            continue
        for direction, face in element.get("faces", {}).items():
            if "uv" not in face:
                face["uv"] = clean_numbers(bounded_uv(direction, start, end))
                added += 1
    return added


def process(path: Path, write: bool) -> tuple[int, bool]:
    model = json.loads(path.read_text(encoding="utf-8"))
    added = add_uvs(model)
    if added and write:
        path.write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8")
    return added, bool(added)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write", action="store_true", help="update affected models")
    parser.add_argument("--patch", action="store_true", help="emit an apply_patch patch")
    args = parser.parse_args()

    if args.patch:
        print("*** Begin Patch")
        for path in sorted(MODEL_DIR.glob("*.json")):
            original = path.read_text(encoding="utf-8")
            model = json.loads(original)
            if not add_uvs(model):
                continue
            updated = json.dumps(model, indent=2) + "\n"
            diff = list(difflib.unified_diff(
                original.splitlines(), updated.splitlines(), lineterm=""
            ))
            print(f"*** Update File: {path}")
            print("\n".join(diff[2:]))
        print("*** End Patch")
        return

    total = 0
    affected = 0
    for path in sorted(MODEL_DIR.glob("*.json")):
        added, changed = process(path, args.write)
        if changed:
            print(f"{path.relative_to(ROOT)}: {added} UV rectangles")
            total += added
            affected += 1
    action = "added" if args.write else "needed"
    print(f"{total} UV rectangles {action} across {affected} models")


if __name__ == "__main__":
    main()
