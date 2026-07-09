#!/usr/bin/env python3
"""Export LanteaCraft project docs and reference data as DokuWiki pages."""

from __future__ import annotations

import json
import re
from collections import defaultdict
from datetime import datetime
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs" / "dokuwiki"
MEDIA = OUT / "media"

EXCLUDED_INDEX_PARTS = {
    ".git",
    ".gradle",
    ".idea",
    "build",
    "run",
    "__pycache__",
}


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8", errors="replace")


def write_page(rel: str, title: str, body: str) -> None:
    path = OUT / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    content = body.rstrip() + "\n"
    path.write_text(content, encoding="utf-8")


def page_link(rel: str, label: str | None = None) -> str:
    page = rel.replace("\\", "/").removesuffix(".txt").replace("/", ":")
    return f"[[{page}|{label or page}]]"


def title_from_slug(slug: str) -> str:
    return slug.replace("_", " ").replace("-", " ").title()


def escape_table_cell(text: object) -> str:
    return str(text).replace("|", "\\|").replace("\n", " ").strip()


def table(headers: list[str], rows: list[list[object]]) -> str:
    if not rows:
        return ""
    lines = ["^ " + " ^ ".join(headers) + " ^"]
    for row in rows:
        lines.append("| " + " | ".join(escape_table_cell(cell) for cell in row) + " |")
    return "\n".join(lines)


def md_inline_to_doku(text: str) -> str:
    text = re.sub(r"`([^`]+)`", r"''\1''", text)
    text = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r"[[\2|\1]]", text)
    return text


def md_to_dokuwiki(markdown: str) -> str:
    lines = markdown.splitlines()
    output: list[str] = []
    in_code = False
    in_table = False

    for raw in lines:
        line = raw.rstrip()
        fence = re.match(r"^```([A-Za-z0-9_+-]*)\s*$", line)
        if fence:
            if in_code:
                output.append("</code>")
                in_code = False
            else:
                lang = fence.group(1)
                output.append(f"<code {lang}>" if lang else "<code>")
                in_code = True
            continue

        if in_code:
            output.append(line)
            continue

        heading = re.match(r"^(#{1,6})\s+(.+?)\s*$", line)
        if heading:
            level = len(heading.group(1))
            marks = "=" * max(2, 7 - level)
            output.append(f"{marks} {md_inline_to_doku(heading.group(2))} {marks}")
            in_table = False
            continue

        if re.match(r"^\s*\|.*\|\s*$", line):
            cells = [md_inline_to_doku(c.strip()) for c in line.strip().strip("|").split("|")]
            if all(re.match(r"^:?-{3,}:?$", c) for c in cells):
                continue
            prefix = "^" if not in_table else "|"
            suffix = "^" if not in_table else "|"
            sep = " ^ " if not in_table else " | "
            output.append(prefix + " " + sep.join(cells) + f" {suffix}")
            in_table = True
            continue
        in_table = False

        bullet = re.match(r"^(\s*)[-*]\s+(.+)$", line)
        if bullet:
            depth = max(1, len(bullet.group(1)) // 2 + 1)
            output.append("  " * depth + "* " + md_inline_to_doku(bullet.group(2)))
            continue

        numbered = re.match(r"^(\s*)\d+[.)]\s+(.+)$", line)
        if numbered:
            depth = max(1, len(numbered.group(1)) // 2 + 1)
            output.append("  " * depth + "- " + md_inline_to_doku(numbered.group(2)))
            continue

        output.append(md_inline_to_doku(line))

    if in_code:
        output.append("</code>")
    return "\n".join(output)


def load_json(path: Path) -> object | None:
    try:
        return json.loads(read_text(path))
    except Exception:
        return None


def lang_entries() -> dict[str, str]:
    path = ROOT / "src/main/resources/assets/lanteacraft/lang/en_us.json"
    data = load_json(path)
    return data if isinstance(data, dict) else {}


def id_from_lang_key(key: str) -> str:
    return key.split(".")[-1]


def recipe_summary(path: Path) -> tuple[str, str, str]:
    data = load_json(path)
    rid = path.stem
    if not isinstance(data, dict):
        return rid, "unreadable", path.as_posix()
    rtype = str(data.get("type", "minecraft:crafting_shaped")).replace("minecraft:", "")
    result = data.get("result", {})
    if isinstance(result, dict):
        item = result.get("id") or result.get("item") or result.get("count") or ""
    else:
        item = result
    if not item:
        item = rid
    return rid, rtype, str(item)


def inventory_page(kind: str, labels: dict[str, str]) -> str:
    resources = ROOT / "src/main/resources"
    rows: list[list[object]] = []
    prefix = f"{kind}.lanteacraft."
    for key, label in sorted(labels.items()):
        if not key.startswith(prefix) or key.endswith(".tooltip") or "." in key[len(prefix):]:
            continue
        item_id = id_from_lang_key(key)
        model = resources / f"assets/lanteacraft/models/{kind}/{item_id}.json"
        texture = resources / f"assets/lanteacraft/textures/{kind}/{item_id}.png"
        recipe = resources / f"data/lanteacraft/recipe/{item_id}.json"
        loot = resources / f"data/lanteacraft/loot_table/blocks/{item_id}.json"
        rows.append([
            f"lanteacraft:{item_id}",
            label,
            "yes" if model.exists() else "",
            "yes" if texture.exists() else "",
            "yes" if recipe.exists() else "",
            "yes" if loot.exists() else "",
        ])
    title = "Blocks" if kind == "block" else "Items"
    return "\n".join([
        f"====== {title} ======",
        "",
        f"Generated from ''src/main/resources/assets/lanteacraft/lang/en_us.json'' and matching resource files.",
        "",
        table(["ID", "Display name", "Model", "Texture", "Recipe", "Loot table"], rows),
    ])


def recipes_page() -> str:
    recipe_dir = ROOT / "src/main/resources/data/lanteacraft/recipe"
    rows = []
    for path in sorted(recipe_dir.glob("*.json")):
        rid, rtype, result = recipe_summary(path)
        rows.append([f"lanteacraft:{rid}", rtype, result, path.relative_to(ROOT).as_posix()])
    return "\n".join([
        "====== Recipes ======",
        "",
        "Generated from packaged recipe JSON.",
        "",
        table(["Recipe", "Type", "Result", "Source"], rows),
    ])


def config_page() -> str:
    config = ROOT / "src/main/java/com/pclogix/lanteacraft/Config.java"
    text = read_text(config) if config.exists() else ""
    rows = []
    assignment = re.compile(
        r"(?:public\s+static\s+final\s+[^=;]+?\s+)?([A-Z0-9_]+)\s*=\s*BUILDER\s*(.*?)\s*;",
        re.S,
    )
    for match in assignment.finditer(text):
        name = match.group(1)
        block = " ".join(match.group(2).split())
        default = ""
        default_match = re.search(r"\.define(?:InRange|Enum)?\(\s*\"([^\"]+)\"\s*,\s*([^,\)]+)", block)
        if not default_match:
            continue
        key = default_match.group(1) if default_match else name.lower()
        default = default_match.group(2).strip()
        comments = []
        for comment_match in re.finditer(r"\.comment\((.*?)\)\s*\.", block):
            comments.extend(re.findall(r"\"([^\"]+)\"", comment_match.group(1)))
        rows.append([key, default, " ".join(comments)])
    if not rows:
        rows.append(["See source", "", "Could not extract config entries automatically."])
    return "\n".join([
        "====== Configuration Reference ======",
        "",
        "Generated from ''src/main/java/com/pclogix/lanteacraft/Config.java''.",
        "",
        table(["Option", "Default", "Comment"], rows),
    ])


def worldgen_page() -> str:
    base = ROOT / "src/main/resources/data/lanteacraft"
    sections: list[str] = ["====== Worldgen And Structures ======", ""]
    groups = [
        ("Dimensions", "dimension/*.json"),
        ("Dimension Types", "dimension_type/*.json"),
        ("Configured Features", "worldgen/configured_feature/*.json"),
        ("Placed Features", "worldgen/placed_feature/*.json"),
        ("Template Pools", "worldgen/template_pool/**/*.json"),
        ("Structures", "structure/**/*.nbt"),
        ("Atlantis City Data", "atlantis_city/*"),
    ]
    for heading, pattern in groups:
        paths = sorted(base.glob(pattern))
        sections.extend([f"===== {heading} =====", ""])
        if paths:
            for path in paths:
                sections.append(f"  * ''{path.relative_to(ROOT).as_posix()}''")
        else:
            sections.append("  * None found.")
        sections.append("")
    return "\n".join(sections)


def assets_page() -> str:
    base = ROOT / "src/main/resources/assets/lanteacraft"
    groups: dict[str, list[Path]] = defaultdict(list)
    for path in sorted(base.rglob("*")):
        if path.is_file():
            rel = path.relative_to(base)
            groups[rel.parts[0] if rel.parts else "assets"].append(path)
    lines = ["====== Asset Manifest ======", "", "Generated file listing for packaged LanteaCraft assets.", ""]
    for group, paths in sorted(groups.items()):
        total = sum(p.stat().st_size for p in paths)
        lines.extend([f"===== {title_from_slug(group)} =====", "", f"Files: {len(paths)}; bytes: {total}", ""])
        for path in paths:
            lines.append(f"  * ''{path.relative_to(ROOT).as_posix()}''")
        lines.append("")
    return "\n".join(lines)


def code_index_page() -> str:
    java_base = ROOT / "src/main/java/com/pclogix/lanteacraft"
    rows = []
    for path in sorted(java_base.rglob("*.java")):
        rel = path.relative_to(java_base)
        package = ".".join(rel.parts[:-1]) or "(root)"
        rows.append([package, path.stem, path.relative_to(ROOT).as_posix()])
    return "\n".join([
        "====== Code Index ======",
        "",
        "Generated Java source index. This is a navigation aid rather than API documentation.",
        "",
        table(["Package", "Class", "Source"], rows),
    ])


def project_manifest_page() -> str:
    rows = []
    for path in sorted(ROOT.rglob("*")):
        if not path.is_file():
            continue
        rel = path.relative_to(ROOT)
        if any(part in EXCLUDED_INDEX_PARTS for part in rel.parts):
            continue
        if rel.parts[:2] == ("docs", "dokuwiki"):
            continue
        rows.append([rel.as_posix(), path.stat().st_size])
    return "\n".join([
        "====== Project File Manifest ======",
        "",
        "Generated file manifest excluding build, IDE, runtime, and VCS internals.",
        "",
        table(["Path", "Bytes"], rows),
    ])


def export_existing_markdown() -> list[tuple[str, str]]:
    pages = []
    sources = [ROOT / "README.md", *sorted((ROOT / "docs").glob("*.md"))]
    for source in sources:
        slug = "readme" if source.name == "README.md" else source.stem.lower()
        rel = f"docs/{slug}.txt"
        body = md_to_dokuwiki(read_text(source))
        write_page(rel, source.stem, body)
        pages.append((rel, source.name))
    return pages


def start_page(converted_pages: list[tuple[str, str]]) -> str:
    generated_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    lines = [
        "====== LanteaCraft DokuWiki Export ======",
        "",
        f"Generated from the local workspace at ''{generated_at}''.",
        "",
        "===== Human-Written Docs =====",
        "",
    ]
    for rel, label in converted_pages:
        lines.append(f"  * {page_link(rel, label)}")
    lines.extend([
        "",
        "===== Generated Reference =====",
        "",
        f"  * {page_link('reference/blocks.txt', 'Blocks')}",
        f"  * {page_link('reference/items.txt', 'Items')}",
        f"  * {page_link('reference/recipes.txt', 'Recipes')}",
        f"  * {page_link('reference/configuration.txt', 'Configuration')}",
        f"  * {page_link('reference/worldgen.txt', 'Worldgen and structures')}",
        f"  * {page_link('reference/assets.txt', 'Asset manifest')}",
        f"  * {page_link('reference/code_index.txt', 'Code index')}",
        f"  * {page_link('reference/project_manifest.txt', 'Project file manifest')}",
        "",
        "To install these pages in DokuWiki, copy the ''.txt'' files under this directory into your wiki ''data/pages'' namespace. Media references are listed as project paths; binary media is not duplicated by this exporter.",
    ])
    return "\n".join(lines)


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    MEDIA.mkdir(parents=True, exist_ok=True)

    converted = export_existing_markdown()
    labels = lang_entries()
    write_page("reference/blocks.txt", "Blocks", inventory_page("block", labels))
    write_page("reference/items.txt", "Items", inventory_page("item", labels))
    write_page("reference/recipes.txt", "Recipes", recipes_page())
    write_page("reference/configuration.txt", "Configuration", config_page())
    write_page("reference/worldgen.txt", "Worldgen And Structures", worldgen_page())
    write_page("reference/assets.txt", "Asset Manifest", assets_page())
    write_page("reference/code_index.txt", "Code Index", code_index_page())
    write_page("reference/project_manifest.txt", "Project File Manifest", project_manifest_page())
    landing = start_page(converted)
    write_page("start.txt", "LanteaCraft DokuWiki Export", landing)
    write_page("index.txt", "LanteaCraft DokuWiki Export", landing)

    print(f"Wrote DokuWiki export to {OUT}")


if __name__ == "__main__":
    main()
