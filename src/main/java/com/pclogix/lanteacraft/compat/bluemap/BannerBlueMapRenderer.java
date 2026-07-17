package com.pclogix.lanteacraft.compat.bluemap;

import com.pclogix.lanteacraft.LanteaCraft;
import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.mca.blockentity.BannerBlockEntity;

import java.util.Locale;
import java.util.Map;

/** Adds LanteaCraft's banner-pattern layers to BlueMap's otherwise solid banner model. */
final class BannerBlueMapRenderer implements BlockRenderer {
    private static final Map<String, Integer> DYE_COLORS = Map.ofEntries(
            Map.entry("white", 0xF9FFFE), Map.entry("orange", 0xF9801D),
            Map.entry("magenta", 0xC74EBD), Map.entry("light_blue", 0x3AB3DA),
            Map.entry("yellow", 0xFED83D), Map.entry("lime", 0x80C71F),
            Map.entry("pink", 0xF38BAA), Map.entry("gray", 0x474F52),
            Map.entry("light_gray", 0x9D9D97), Map.entry("cyan", 0x169C9C),
            Map.entry("purple", 0x8932B8), Map.entry("blue", 0x3C44AA),
            Map.entry("brown", 0x835432), Map.entry("green", 0x5E7C16),
            Map.entry("red", 0xB02E26), Map.entry("black", 0x1D1D21)
    );
    private static final float MIN_X = 1.2F / 16.0F;
    private static final float MAX_X = 14.8F / 16.0F;
    private static final float FRONT_U0 = 0.25F / 16.0F;
    private static final float FRONT_U1 = 5.5F / 16.0F;
    private static final float BACK_U0 = 5.5F / 16.0F;
    private static final float BACK_U1 = 10.5F / 16.0F;
    private static final float V0 = 0.25F / 16.0F;
    private static final float V1 = 10.25F / 16.0F;

    private final ResourceModelRenderer baseRenderer;
    private final TextureGallery textureGallery;

    BannerBlueMapRenderer(ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        this.baseRenderer = new ResourceModelRenderer(resourcePack, textureGallery, renderSettings);
        this.textureGallery = textureGallery;
    }

    @Override
    public void render(BlockNeighborhood block, Variant variant, TileModelView tileModel, Color blockColor) {
        baseRenderer.render(block, variant, tileModel, blockColor);

        BlockEntity entity = block.getBlockEntity();
        if (!(entity instanceof BannerBlockEntity banner)) {
            return;
        }

        boolean wall = block.getBlockState().getValue().endsWith("_wall_banner");
        float minY = (wall ? -14.6F : 1.4F) / 16.0F;
        float maxY = (wall ? 13.4F : 29.4F) / 16.0F;
        float frontZ = (wall ? 14.5F : 7.0F) / 16.0F;
        float backZ = (wall ? 15.0F : 7.5F) / 16.0F;
        int layer = 0;

        for (BannerBlockEntity.Pattern pattern : banner.getPatterns()) {
            String patternId = scalar(pattern.getPattern());
            if (!patternId.startsWith(LanteaCraft.MODID + ":")) continue;

            String patternName = patternId.substring(patternId.indexOf(':') + 1);
            int rgb = DYE_COLORS.getOrDefault(scalar(pattern.getColor()).toLowerCase(Locale.ROOT), 0xFFFFFF);
            float red = ((rgb >> 16) & 0xFF) / 255.0F;
            float green = ((rgb >> 8) & 0xFF) / 255.0F;
            float blue = (rgb & 0xFF) / 255.0F;
            int texture = textureGallery.get(new ResourcePath<Texture>(
                    LanteaCraft.MODID, "entity/banner/" + patternName));
            float depth = 0.0005F * ++layer;

            int overlayStart = tileModel.getTileModel().size();
            addClothSide(block, tileModel, texture, red, green, blue,
                    MAX_X, MIN_X, minY, maxY, frontZ - depth, FRONT_U0, FRONT_U1);
            addClothSide(block, tileModel, texture, red, green, blue,
                    MIN_X, MAX_X, minY, maxY, backZ + depth, BACK_U0, BACK_U1);
            tileModel.initialize(overlayStart);
            if (variant.isTransformed()) {
                tileModel.transform(variant.getTransformMatrix());
            }
        }
    }

    private static void addClothSide(BlockNeighborhood block, TileModelView view, int texture,
                                     float red, float green, float blue,
                                     float firstX, float secondX, float minY, float maxY, float z,
                                     float u0, float u1) {
        TileModel model = view.getTileModel();
        int first = view.add(2);
        int sunlight = block.getLightData().getSkyLight();
        int blocklight = block.getLightData().getBlockLight();

        model.setPositions(first,
                firstX, minY, z, secondX, minY, z, secondX, maxY, z);
        model.setPositions(first + 1,
                firstX, minY, z, secondX, maxY, z, firstX, maxY, z);
        model.setUvs(first, u0, V1, u1, V1, u1, V0);
        model.setUvs(first + 1, u0, V1, u1, V0, u0, V0);

        for (int index = first; index <= first + 1; index++) {
            model.setAOs(index, 1.0F, 1.0F, 1.0F);
            model.setColor(index, red, green, blue);
            model.setSunlight(index, sunlight);
            model.setBlocklight(index, blocklight);
            model.setMaterialIndex(index, texture);
        }
    }

    private static String scalar(Object value) {
        if (value == null) return "";
        String scalar = String.valueOf(value);
        if (scalar.length() >= 2 && scalar.startsWith("\"") && scalar.endsWith("\"")) {
            return scalar.substring(1, scalar.length() - 1);
        }
        return scalar;
    }
}
