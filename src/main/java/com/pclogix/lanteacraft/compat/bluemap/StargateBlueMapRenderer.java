package com.pclogix.lanteacraft.compat.bluemap;

import com.pclogix.lanteacraft.LanteaCraft;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.BlockStateModelRenderer;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** BlueMap 5.7 renderer for the assembled gate OBJ and NBT-backed base camouflage. */
final class StargateBlueMapRenderer implements BlockRenderer {
    private static final String OBJ_RESOURCE = "/assets/lanteacraft/models/obj/stargate_assembled.obj";
    private static final ObjMesh GATE_MESH = loadMesh();
    private static final Map<String, Float> MATERIAL_SHADE = Map.of(
            "frame", 1.0F,
            "frame_85", 0.85F,
            "frame_75", 0.75F,
            "frame_55", 0.55F,
            "glyphs", 1.0F,
            "chevron_lit", 0.28F);

    private final TextureGallery textureGallery;
    private final BlockStateModelRenderer camouflageRenderer;
    private final Color camouflageColor = new Color();

    StargateBlueMapRenderer(ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        this.textureGallery = textureGallery;
        this.camouflageRenderer = new BlockStateModelRenderer(resourcePack, textureGallery, renderSettings);
    }

    @Override
    public void render(BlockNeighborhood block, Variant variant, TileModelView tileModel, Color blockColor) {
        int meshStart = tileModel.getTileModel().size();
        renderGateMesh(block, tileModel);
        tileModel.initialize(meshStart);
        if (variant.isTransformed()) {
            tileModel.transform(variant.getTransformMatrix());
        }

        renderBottomCamouflage(block, variant, tileModel.initialize());
        blockColor.set(0.55F, 0.55F, 0.55F, 1.0F, false);
    }

    private void renderGateMesh(BlockNeighborhood block, TileModelView tileModel) {
        int frameTexture = textureId(frameTexture(block.getBlockState()));
        int glyphTexture = textureId(glyphTexture(block.getBlockState()));
        int sunlight = block.getLightData().getSkyLight();
        int blocklight = block.getLightData().getBlockLight();
        TileModel model = tileModel.getTileModel();

        for (ObjFace face : GATE_MESH.faces()) {
            int first = tileModel.add(2);
            int texture = face.material().equals("glyphs") ? glyphTexture : frameTexture;
            float shade = MATERIAL_SHADE.getOrDefault(face.material(), 1.0F);
            setTriangle(model, first, face, 0, 1, 2, texture, shade, sunlight, blocklight);
            setTriangle(model, first + 1, face, 0, 2, 3, texture, shade, sunlight, blocklight);
        }
    }

    private void renderBottomCamouflage(BlockNeighborhood block, Variant variant, TileModelView tileModel) {
        BlockEntity entity = block.getBlockEntity();
        if (!(entity instanceof BlueMapStargateBlockEntity stargate)) {
            return;
        }

        String camouflageId = stargate.getBottomCamouflage();
        if (camouflageId == null || camouflageId.isBlank() || camouflageId.equals("minecraft:air")) {
            return;
        }

        BlockState camouflage;
        try {
            camouflage = BlockState.fromString(camouflageId);
        } catch (IllegalArgumentException exception) {
            LanteaCraft.LOGGER.warn("BlueMap could not parse Stargate camouflage block '{}'.", camouflageId);
            return;
        }

        double angle = Math.toRadians(variant.getY());
        float rightX = (float)Math.cos(angle);
        float rightZ = (float)Math.sin(angle);
        for (int offset = -3; offset <= 3; offset++) {
            int camouflageStart = tileModel.getTileModel().size();
            camouflageColor.set(0.0F, 0.0F, 0.0F, 0.0F, true);
            camouflageRenderer.render(block, camouflage, tileModel.initialize(), camouflageColor);
            tileModel.initialize(camouflageStart).translate(rightX * offset, 0.0F, rightZ * offset);
        }
    }

    private int textureId(String texture) {
        return textureGallery.get(new ResourcePath<Texture>(LanteaCraft.MODID, texture));
    }

    private static String frameTexture(BlockState state) {
        return "tileentity/stargate" + variantSuffix(state);
    }

    private static String glyphTexture(BlockState state) {
        return "tileentity/stargate_glyphs" + variantSuffix(state);
    }

    private static String variantSuffix(BlockState state) {
        String id = state.getValue();
        if (id.startsWith("nox_")) return "_nox";
        if (id.startsWith("wraith_")) return "_wraith";
        if (id.startsWith("pegasus_")) return "_pegasus";
        return "";
    }

    private static void setTriangle(TileModel model, int index, ObjFace face, int a, int b, int c,
                                    int texture, float shade, int sunlight, int blocklight) {
        ObjVertex va = face.vertices()[a];
        ObjVertex vb = face.vertices()[b];
        ObjVertex vc = face.vertices()[c];
        ObjUv uva = face.uvs()[a];
        ObjUv uvb = face.uvs()[b];
        ObjUv uvc = face.uvs()[c];
        model.setPositions(index, va.x(), va.y(), va.z(), vb.x(), vb.y(), vb.z(), vc.x(), vc.y(), vc.z());
        model.setUvs(index, uva.u(), 1.0F - uva.v(), uvb.u(), 1.0F - uvb.v(), uvc.u(), 1.0F - uvc.v());
        model.setAOs(index, 1.0F, 1.0F, 1.0F);
        model.setColor(index, shade, shade, shade);
        model.setSunlight(index, sunlight);
        model.setBlocklight(index, blocklight);
        model.setMaterialIndex(index, texture);
    }

    private static ObjMesh loadMesh() {
        try (InputStream stream = StargateBlueMapRenderer.class.getResourceAsStream(OBJ_RESOURCE)) {
            if (stream == null) {
                throw new IOException("Missing " + OBJ_RESOURCE);
            }

            List<ObjVertex> vertices = new ArrayList<>();
            List<ObjUv> uvs = new ArrayList<>();
            List<ObjFace> faces = new ArrayList<>();
            String material = "frame";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length == 0) continue;
                    switch (parts[0]) {
                        case "v" -> vertices.add(new ObjVertex(parse(parts[1]), parse(parts[2]), parse(parts[3])));
                        case "vt" -> uvs.add(new ObjUv(parse(parts[1]), parse(parts[2])));
                        case "usemtl" -> material = parts[1].toLowerCase(Locale.ROOT);
                        case "f" -> faces.add(parseFace(parts, material, vertices, uvs));
                        default -> { }
                    }
                }
            }
            return new ObjMesh(List.copyOf(faces));
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Failed to load BlueMap Stargate OBJ", exception);
        }
    }

    private static ObjFace parseFace(String[] parts, String material, List<ObjVertex> vertices, List<ObjUv> uvs) {
        if (parts.length != 5) {
            throw new IllegalArgumentException("Stargate OBJ must contain quad faces");
        }
        ObjVertex[] faceVertices = new ObjVertex[4];
        ObjUv[] faceUvs = new ObjUv[4];
        for (int i = 0; i < 4; i++) {
            String[] indices = parts[i + 1].split("/");
            faceVertices[i] = vertices.get(Integer.parseInt(indices[0]) - 1);
            faceUvs[i] = uvs.get(Integer.parseInt(indices[1]) - 1);
        }
        return new ObjFace(material, faceVertices, faceUvs);
    }

    private static float parse(String value) {
        return Float.parseFloat(value);
    }

    private record ObjMesh(List<ObjFace> faces) { }
    private record ObjFace(String material, ObjVertex[] vertices, ObjUv[] uvs) { }
    private record ObjVertex(float x, float y, float z) { }
    private record ObjUv(float u, float v) { }
}
