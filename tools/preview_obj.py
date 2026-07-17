#!/usr/bin/env python3
"""Create a quick flat-shaded PNG preview of a Wavefront OBJ."""

from __future__ import annotations

import argparse
import math
from pathlib import Path

from PIL import Image, ImageDraw


COLORS = {
    "frame": (155, 158, 162),
    "frame_85": (132, 135, 139),
    "frame_75": (116, 119, 123),
    "frame_55": (86, 88, 92),
    "glyphs": (196, 157, 54),
    "chevron_lit": (223, 119, 34),
}


def rotate(point: tuple[float, float, float], yaw: float, pitch: float) -> tuple[float, float, float]:
    x, y, z = point
    cy, sy = math.cos(yaw), math.sin(yaw)
    x, z = x * cy - z * sy, x * sy + z * cy
    cp, sp = math.cos(pitch), math.sin(pitch)
    return x, y * cp - z * sp, y * sp + z * cp


def load_obj(path: Path) -> tuple[list[tuple[float, float, float]], list[tuple[str, list[int]]]]:
    vertices: list[tuple[float, float, float]] = []
    faces: list[tuple[str, list[int]]] = []
    material = "frame"
    for line in path.read_text(encoding="utf-8").splitlines():
        if line.startswith("v "):
            vertices.append(tuple(map(float, line.split()[1:4])))
        elif line.startswith("usemtl "):
            material = line.split(maxsplit=1)[1]
        elif line.startswith("f "):
            faces.append((material, [int(token.split("/")[0]) - 1 for token in line.split()[1:]]))
    return vertices, faces


def render_view(
    image: Image.Image,
    vertices: list[tuple[float, float, float]],
    faces: list[tuple[str, list[int]]],
    box: tuple[int, int, int, int],
    yaw_degrees: float,
    pitch_degrees: float,
) -> None:
    left, top, right, bottom = box
    center = (0.5, 3.5, 0.5)
    transformed = [rotate((x-center[0], y-center[1], z-center[2]), math.radians(yaw_degrees), math.radians(pitch_degrees)) for x, y, z in vertices]
    min_x, max_x = min(v[0] for v in transformed), max(v[0] for v in transformed)
    min_y, max_y = min(v[1] for v in transformed), max(v[1] for v in transformed)
    scale = min((right-left-40)/(max_x-min_x), (bottom-top-40)/(max_y-min_y))

    def project(index: int) -> tuple[float, float]:
        x, y, _z = transformed[index]
        return left + 20 + (x-min_x)*scale, bottom - 20 - (y-min_y)*scale

    draw = ImageDraw.Draw(image, "RGBA")
    ordered = sorted(faces, key=lambda face: sum(transformed[i][2] for i in face[1]) / len(face[1]), reverse=True)
    for material, indices in ordered:
        color = COLORS.get(material, (180, 180, 180))
        draw.polygon([project(i) for i in indices], fill=(*color, 255), outline=(28, 31, 36, 100))


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("obj", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    vertices, faces = load_obj(args.obj)
    image = Image.new("RGB", (1400, 760), (22, 25, 30))
    render_view(image, vertices, faces, (0, 0, 700, 760), 0, 0)
    render_view(image, vertices, faces, (700, 0, 1400, 760), -32, 8)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    image.save(args.output)
    print(f"Wrote {args.output}")


if __name__ == "__main__":
    main()
