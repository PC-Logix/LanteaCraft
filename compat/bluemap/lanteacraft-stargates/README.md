# LanteaCraft for BlueMap

This BlueMap-only resource pack replaces each assembled Stargate base's empty
model with the LanteaCraft BlueMap renderer. It requires BlueMap 5.7 and a
LanteaCraft build containing the `lanteacraft:stargate` renderer.

It also routes LanteaCraft's NeoForge OBJ-backed DHDs, Naquadah generator, ZPM
hub, installed ZPMs, and obelisk through `lanteacraft:obj`. All standing and
wall-banner colors use `lanteacraft:banner` so LanteaCraft's Stargate, Lantean,
and Goa'uld pattern layers appear on BlueMap's static banner geometry.

Release builds bundle this pack inside the LanteaCraft mod JAR. When BlueMap is
installed, LanteaCraft automatically copies or updates it in
`config/bluemap/packs` before BlueMap loads resources. Manual installation is
only needed while developing the pack independently. ZIP entries must use `/`
path separators.

The OBJ mirrors `StargateBaseRenderer`'s frame, glyph ring, and idle chevrons.
It intentionally omits runtime-only geometry: event horizon, kawoosh, and iris.
The remaining assembled ring and chevron blocks continue to use the mod's empty
models, so only the assembled base contributes the full-gate mesh.

Base camouflage is supported by the custom renderer. It reads the base block
entity's `bottomCamouflage` NBT value and asks BlueMap to render seven copies of
that block's normal model across the bottom row. This works with arbitrary
ordinary block models instead of baking one camouflage texture into the OBJ.

Regenerate the OBJ after changing renderer geometry:

```powershell
python tools/export_stargate_obj.py
```

Regenerate the OBJ and banner blockstate overrides after changing their source
blockstates or supported colors:

```powershell
python tools/generate_bluemap_compat.py
```
