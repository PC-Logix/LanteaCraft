package com.pclogix.lanteacraft.compat.bluemap;

import com.pclogix.lanteacraft.LanteaCraft;
import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public final class BlueMapCompat {
    private static final String PACK_FILE = "lanteacraft-stargates-bluemap-5.7.zip";
    private static final String PACK_RESOURCE = "/META-INF/lanteacraft/bluemap/" + PACK_FILE;
    private static boolean registered;

    private BlueMapCompat() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }

        installPack();
        BlockEntityType.REGISTRY.register(new BlockEntityType.Impl(
                new Key(LanteaCraft.MODID, "stargate_base"),
                BlueMapStargateBlockEntity.class));
        BlockEntityType.REGISTRY.register(new BlockEntityType.Impl(
                new Key(LanteaCraft.MODID, "zpm_hub"),
                BlueMapZpmHubBlockEntity.class));
        BlockRendererType.REGISTRY.register(new BlockRendererType.Impl(
                new Key(LanteaCraft.MODID, "stargate"),
                StargateBlueMapRenderer::new));
        BlockRendererType.REGISTRY.register(new BlockRendererType.Impl(
                new Key(LanteaCraft.MODID, "obj"),
                ObjBlueMapRenderer::new));
        BlockRendererType.REGISTRY.register(new BlockRendererType.Impl(
                new Key(LanteaCraft.MODID, "banner"),
                BannerBlueMapRenderer::new));
        registered = true;
        LanteaCraft.LOGGER.info("Registered BlueMap 5.7 Stargate, OBJ, and banner renderers.");
    }

    private static void installPack() {
        try (InputStream stream = BlueMapCompat.class.getResourceAsStream(PACK_RESOURCE)) {
            if (stream == null) {
                throw new IOException("Missing bundled BlueMap pack " + PACK_RESOURCE);
            }

            byte[] bundledPack = stream.readAllBytes();
            Path packsDirectory = FMLPaths.CONFIGDIR.get().resolve("bluemap").resolve("packs");
            Path installedPack = packsDirectory.resolve(PACK_FILE);
            Files.createDirectories(packsDirectory);

            if (Files.isRegularFile(installedPack)
                    && Arrays.equals(bundledPack, Files.readAllBytes(installedPack))) {
                return;
            }

            Path temporaryPack = packsDirectory.resolve(PACK_FILE + ".tmp");
            Files.write(temporaryPack, bundledPack);
            try {
                Files.move(temporaryPack, installedPack,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryPack, installedPack, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporaryPack);
            }

            LanteaCraft.LOGGER.info("Installed bundled BlueMap Stargate pack at {}.", installedPack);
        } catch (IOException exception) {
            LanteaCraft.LOGGER.error("Failed to install bundled BlueMap Stargate pack.", exception);
        }
    }
}
