package com.pclogix.lanteacraft.compat.bluemap;

import com.pclogix.lanteacraft.LanteaCraft;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Renders the NeoForge OBJ-backed block models that BlueMap 5.7 cannot parse. */
final class ObjBlueMapRenderer implements BlockRenderer {
    private static final String OBJ_ROOT = "/assets/lanteacraft/models/obj/";
    private static final ModelSpec ZPM = new ModelSpec("zpm.obj", "item/zpm_glb", Transform.IDENTITY);
    private static final float ZPM_SCALE = 0.42F;
    private static final float ZPM_MODEL_CENTER_X = -0.0023025F;
    private static final float ZPM_MODEL_CENTER_Y = 0.522745F;
    private static final float ZPM_MODEL_CENTER_Z = -0.0067825F;
    private static final float ZPM_SOCKET_CENTER_Y = 0.63F;
    private static final Map<String, ModelSpec> MODELS = Map.ofEntries(
            Map.entry("lanteacraft:block/dhd", spec("model_dhd.obj", "block/dhd_off")),
            Map.entry("lanteacraft:block/dhd_on", spec("model_dhd.obj", "block/dhd_on")),
            Map.entry("lanteacraft:block/nox_dhd", spec("model_dhd.obj", "block/dhd_off_nox")),
            Map.entry("lanteacraft:block/nox_dhd_on", spec("model_dhd.obj", "block/dhd_on_nox")),
            Map.entry("lanteacraft:block/pegasus_dhd", spec("model_dhd.obj", "block/dhd_off_pegasus")),
            Map.entry("lanteacraft:block/pegasus_dhd_on", spec("model_dhd.obj", "block/dhd_on_pegasus")),
            Map.entry("lanteacraft:block/wraith_dhd", spec("model_dhd.obj", "block/dhd_off_wraith")),
            Map.entry("lanteacraft:block/wraith_dhd_on", spec("model_dhd.obj", "block/dhd_on_wraith")),
            Map.entry("lanteacraft:block/naquadah_generator", spec("model_naquadah_generator.obj", "block/naquadah_generator_off")),
            Map.entry("lanteacraft:block/naquadah_generator_on", spec("model_naquadah_generator.obj", "block/naquadah_generator_on")),
            Map.entry("lanteacraft:block/zpm_hub", spec("zpm_hub.obj", "block/zpm_hub")),
            Map.entry("lanteacraft:block/obelisk_bluemap", new ModelSpec(
                    "obelisk.obj", "block/obelisk", new Transform(0.35F, 0.675F, 0.10F, 0.675F)))
    );
    private static final Map<String, ObjMesh> MESHES = new ConcurrentHashMap<>();

    private final TextureGallery textureGallery;

    ObjBlueMapRenderer(ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        this.textureGallery = textureGallery;
    }

    @Override
    public void render(BlockNeighborhood block, Variant variant, TileModelView tileModel, Color blockColor) {
        ModelSpec spec = MODELS.get(variant.getModel().getFormatted());
        if (spec == null) {
            LanteaCraft.LOGGER.warn("No BlueMap OBJ mapping for model '{}'.", variant.getModel().getFormatted());
            return;
        }

        int modelStart = tileModel.getTileModel().size();
        renderMesh(block, tileModel, mesh(spec.obj()), spec.texture(), spec.transform());
        if (variant.getModel().getFormatted().equals("lanteacraft:block/zpm_hub")) {
            renderInstalledZpms(block, tileModel);
        }

        tileModel.initialize(modelStart);
        if (variant.isTransformed()) {
            tileModel.transform(variant.getTransformMatrix());
        }
        blockColor.set(0.55F, 0.55F, 0.55F, 1.0F, false);
    }

    private void renderInstalledZpms(BlockNeighborhood block, TileModelView tileModel) {
        BlockEntity entity = block.getBlockEntity();
        if (!(entity instanceof BlueMapZpmHubBlockEntity hub)) {
            return;
        }

        ObjMesh mesh = mesh(ZPM.obj());
        for (int slot = 0; slot < 3; slot++) {
            if (!hub.hasZpm(slot)) continue;
            float socketX = slot == 0 ? 0.68F : slot == 1 ? 0.32F : 0.50F;
            float socketZ = slot == 2 ? 0.365F : 0.625F;
            renderMesh(block, tileModel, mesh, ZPM.texture(), vertex -> {
                // Anchor the raw OBJ directly to the socket centers in the hub mesh.
                // The blockstate transform below then rotates both together for facing.
                return new ObjVertex(
                        socketX + ZPM_SCALE * (vertex.x() - ZPM_MODEL_CENTER_X),
                        ZPM_SOCKET_CENTER_Y + ZPM_SCALE * (vertex.y() - ZPM_MODEL_CENTER_Y),
                        socketZ + ZPM_SCALE * (vertex.z() - ZPM_MODEL_CENTER_Z));
            });
        }
    }

    private void renderMesh(BlockNeighborhood block, TileModelView view, ObjMesh mesh,
                            String texturePath, VertexTransform transform) {
        int texture = textureGallery.get(new ResourcePath<Texture>(LanteaCraft.MODID, texturePath));
        int sunlight = block.getLightData().getSkyLight();
        int blocklight = block.getLightData().getBlockLight();
        TileModel model = view.getTileModel();

        for (ObjTriangle triangle : mesh.triangles()) {
            int index = view.add(1);
            ObjVertex a = transform.apply(triangle.a());
            ObjVertex b = transform.apply(triangle.b());
            ObjVertex c = transform.apply(triangle.c());
            model.setPositions(index, a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), c.x(), c.y(), c.z());
            model.setUvs(index,
                    triangle.uvA().u(), 1.0F - triangle.uvA().v(),
                    triangle.uvB().u(), 1.0F - triangle.uvB().v(),
                    triangle.uvC().u(), 1.0F - triangle.uvC().v());
            model.setAOs(index, 1.0F, 1.0F, 1.0F);
            model.setColor(index, 1.0F, 1.0F, 1.0F);
            model.setSunlight(index, sunlight);
            model.setBlocklight(index, blocklight);
            model.setMaterialIndex(index, texture);
        }
    }

    private static ObjMesh mesh(String obj) {
        return MESHES.computeIfAbsent(obj, ObjBlueMapRenderer::loadMesh);
    }

    private static ObjMesh loadMesh(String obj) {
        String resource = OBJ_ROOT + obj;
        try (InputStream stream = ObjBlueMapRenderer.class.getResourceAsStream(resource)) {
            if (stream == null) throw new IOException("Missing " + resource);
            List<ObjVertex> vertices = new ArrayList<>();
            List<ObjUv> uvs = new ArrayList<>();
            List<ObjTriangle> triangles = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                    String[] parts = trimmed.split("\\s+");
                    switch (parts[0]) {
                        case "v" -> vertices.add(new ObjVertex(parse(parts[1]), parse(parts[2]), parse(parts[3])));
                        case "vt" -> uvs.add(new ObjUv(parse(parts[1]), parse(parts[2])));
                        case "f" -> triangulate(parts, vertices, uvs, triangles);
                        default -> { }
                    }
                }
            }
            return new ObjMesh(List.copyOf(triangles));
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Failed to load BlueMap OBJ " + resource, exception);
        }
    }

    private static void triangulate(String[] parts, List<ObjVertex> vertices, List<ObjUv> uvs,
                                    List<ObjTriangle> triangles) {
        if (parts.length < 4) throw new IllegalArgumentException("OBJ face has fewer than three vertices");
        ObjRef first = ref(parts[1], vertices, uvs);
        for (int i = 2; i < parts.length - 1; i++) {
            ObjRef second = ref(parts[i], vertices, uvs);
            ObjRef third = ref(parts[i + 1], vertices, uvs);
            triangles.add(new ObjTriangle(first.vertex(), second.vertex(), third.vertex(),
                    first.uv(), second.uv(), third.uv()));
        }
    }

    private static ObjRef ref(String value, List<ObjVertex> vertices, List<ObjUv> uvs) {
        String[] indices = value.split("/", -1);
        if (indices.length < 2 || indices[1].isEmpty()) {
            throw new IllegalArgumentException("OBJ face is missing texture coordinates");
        }
        return new ObjRef(vertices.get(index(indices[0], vertices.size())),
                uvs.get(index(indices[1], uvs.size())));
    }

    private static int index(String value, int size) {
        int index = Integer.parseInt(value);
        return index < 0 ? size + index : index - 1;
    }

    private static float parse(String value) {
        return Float.parseFloat(value);
    }

    private static ModelSpec spec(String obj, String texture) {
        return new ModelSpec(obj, texture, Transform.IDENTITY);
    }

    private record ModelSpec(String obj, String texture, VertexTransform transform) { }
    private record ObjMesh(List<ObjTriangle> triangles) { }
    private record ObjTriangle(ObjVertex a, ObjVertex b, ObjVertex c, ObjUv uvA, ObjUv uvB, ObjUv uvC) { }
    private record ObjRef(ObjVertex vertex, ObjUv uv) { }
    private record ObjVertex(float x, float y, float z) { }
    private record ObjUv(float u, float v) { }

    @FunctionalInterface
    private interface VertexTransform {
        ObjVertex apply(ObjVertex vertex);
    }

    private record Transform(float scale, float x, float y, float z) implements VertexTransform {
        private static final Transform IDENTITY = new Transform(1.0F, 0.0F, 0.0F, 0.0F);

        @Override
        public ObjVertex apply(ObjVertex vertex) {
            return new ObjVertex(x + scale * vertex.x(), y + scale * vertex.y(), z + scale * vertex.z());
        }
    }
}
