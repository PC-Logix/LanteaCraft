package com.pclogix.lanteacraft.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pclogix.lanteacraft.LanteaCraft;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class AtlantisCityManager {
    private static final ResourceLocation CITY_INDEX = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "atlantis_city/city.json");
    private static final ResourceLocation CITY_BINARY = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "atlantis_city/city.lacb");
    private static final int PLACE_BLOCKS_PER_TICK = 8000;
    private static final int EXPORT_POSITIONS_PER_TICK = 125000;
    private static final int DRAIN_POSITIONS_PER_TICK = 125000;
    private static final int MAX_DRAIN_VOLUME = 32_000_000;
    private static final int PREFERRED_CITY_BOTTOM_Y = 42;
    private static final Queue<CityJob> JOBS = new ArrayDeque<>();
    private static CityData cachedCity;

    private AtlantisCityManager() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        CityJob job = JOBS.peek();
        if (job == null || job.level != level) {
            return;
        }

        try {
            if (job.tick()) {
                JOBS.remove();
            }
        } catch (Exception ex) {
            JOBS.remove();
            LanteaCraft.LOGGER.error("Atlantis city job failed.", ex);
            job.notifyFailure("Atlantis city job failed: " + ex.getMessage());
        }
    }

    public static int queuePlace(ServerLevel level, BlockPos origin, ServerPlayer player) throws IOException {
        CityData city = loadCity(level);
        validateCityFits(level, city, origin);
        PlaceJob job = new PlaceJob(level, player == null ? null : player.getUUID(), city, origin);
        JOBS.add(job);
        job.notifyProgress("Queued Atlantis city placement at " + formatPos(origin) + " (" + city.blockCount + " blocks).");
        return city.blockCount;
    }

    public static int queueExport(ServerLevel level, BlockPos from, BlockPos to, BlockPos origin, String name, ServerPlayer player) throws IOException {
        CityData city = loadCity(level);
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        Path output = Path.of("lanteacraft_exports").resolve(safeFileName(name) + ".json");
        ExportJob job = new ExportJob(level, player == null ? null : player.getUUID(), min, max, origin, output);
        JOBS.add(job);
        job.notifyProgress("Queued Atlantis city export " + formatPos(min) + " -> " + formatPos(max) + " relative to " + formatPos(origin) + " to " + output + ".");
        return city.blockCount;
    }

    public static int queueDrain(ServerLevel level, BlockPos from, BlockPos to, ServerPlayer player) throws IOException {
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        DrainJob job = new DrainJob(level, player == null ? null : player.getUUID(), min, max);
        JOBS.add(job);
        job.notifyProgress("Queued Atlantis drain " + formatPos(min) + " -> " + formatPos(max) + " (" + job.volume + " positions).");
        return job.volume;
    }

    public static int queueConnectedDrain(ServerLevel level, BlockPos center, int radius, ServerPlayer player) throws IOException {
        BlockPos min = center.offset(-radius, -radius, -radius);
        BlockPos max = center.offset(radius, radius, radius);
        ConnectedDrainJob job = new ConnectedDrainJob(level, player == null ? null : player.getUUID(), min, max, center);
        JOBS.add(job);
        job.notifyProgress("Queued Atlantis connected drain from " + formatPos(center) + " with radius " + radius + " (" + job.volume + " positions).");
        return job.volume;
    }

    public static BlockPos defaultOrigin(ServerLevel level) throws IOException {
        CityData city = loadCity(level);
        int maxBuildY = level.getMaxBuildHeight() - 1;
        int cityHeight = city.maxY - city.minY + 1;
        int buildHeight = level.getMaxBuildHeight() - level.getMinBuildHeight();
        if (cityHeight > buildHeight) {
            throw new IOException("Atlantis city is " + cityHeight + " blocks tall, but this dimension only has " + buildHeight + " buildable blocks.");
        }

        int originY = PREFERRED_CITY_BOTTOM_Y - city.minY;
        int minAllowedOriginY = level.getMinBuildHeight() - city.minY;
        int maxAllowedOriginY = maxBuildY - city.maxY;
        if (originY < minAllowedOriginY) {
            originY = minAllowedOriginY;
        } else if (originY > maxAllowedOriginY) {
            originY = maxAllowedOriginY;
        }
        return new BlockPos(0, originY, 0);
    }

    public static Optional<CityBounds> cityBounds(ServerLevel level, BlockPos origin) throws IOException {
        CityData city = loadCity(level);
        return Optional.of(new CityBounds(
                origin.offset(city.minX, city.minY, city.minZ),
                origin.offset(city.maxX, city.maxY, city.maxZ)));
    }

    private static void validateCityFits(ServerLevel level, CityData city, BlockPos origin) throws IOException {
        int minY = origin.getY() + city.minY;
        int maxY = origin.getY() + city.maxY;
        int minBuild = level.getMinBuildHeight();
        int maxBuild = level.getMaxBuildHeight() - 1;
        if (minY < minBuild || maxY > maxBuild) {
            throw new IOException("Atlantis city Y bounds " + minY + ".." + maxY + " do not fit dimension build range " + minBuild + ".." + maxBuild + ".");
        }
    }

    private static CityData loadCity(ServerLevel level) throws IOException {
        if (cachedCity != null) {
            return cachedCity;
        }

        JsonObject index;
        try (InputStream input = level.getServer().getResourceManager().open(CITY_INDEX);
             Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            index = JsonParser.parseReader(reader).getAsJsonObject();
        }

        byte[] binary;
        try (InputStream input = level.getServer().getResourceManager().open(CITY_BINARY)) {
            binary = input.readAllBytes();
        }

        if (binary.length < 8 || binary[0] != 'L' || binary[1] != 'C' || binary[2] != 'A' || binary[3] != 'C') {
            throw new IOException("Invalid Atlantis city binary header.");
        }

        JsonArray boundsMin = index.getAsJsonObject("bounds").getAsJsonArray("min");
        JsonArray boundsMax = index.getAsJsonObject("bounds").getAsJsonArray("max");
        JsonArray paletteJson = index.getAsJsonArray("palette");
        List<BlockState> palette = new ArrayList<>();
        for (int i = 0; i < paletteJson.size(); i++) {
            ResourceLocation id = ResourceLocation.parse(paletteJson.get(i).getAsString());
            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block == Blocks.AIR) {
                throw new IOException("Unknown Atlantis city block: " + id);
            }
            palette.add(block.defaultBlockState());
        }

        List<CityChunk> chunks = new ArrayList<>();
        JsonArray chunksJson = index.getAsJsonArray("chunks");
        for (int i = 0; i < chunksJson.size(); i++) {
            JsonObject chunk = chunksJson.get(i).getAsJsonObject();
            chunks.add(new CityChunk(
                    chunk.get("chunk_x").getAsInt(),
                    chunk.get("chunk_z").getAsInt(),
                    chunk.get("offset").getAsInt(),
                    chunk.get("length").getAsInt(),
                    chunk.get("blocks").getAsInt()));
        }
        chunks.sort(Comparator.comparingInt((CityChunk chunk) -> chunk.chunkZ).thenComparingInt(chunk -> chunk.chunkX));

        cachedCity = new CityData(
                boundsMin.get(0).getAsInt(),
                boundsMin.get(1).getAsInt(),
                boundsMin.get(2).getAsInt(),
                boundsMax.get(0).getAsInt(),
                boundsMax.get(1).getAsInt(),
                boundsMax.get(2).getAsInt(),
                index.get("block_count").getAsInt(),
                palette,
                chunks,
                binary);
        return cachedCity;
    }

    private static List<CityBlock> decodeChunk(CityData city, CityChunk chunk) throws IOException {
        byte[] inflated = inflate(city.binary, chunk.offset, chunk.length);
        ByteBuffer buffer = ByteBuffer.wrap(inflated).order(ByteOrder.BIG_ENDIAN);
        int count = buffer.getInt();
        if (count != chunk.blocks) {
            throw new IOException("Atlantis chunk block count mismatch at " + chunk.chunkX + "," + chunk.chunkZ);
        }

        List<CityBlock> blocks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int localX = Byte.toUnsignedInt(buffer.get());
            int y = buffer.getShort();
            int localZ = Byte.toUnsignedInt(buffer.get());
            int palette = Short.toUnsignedInt(buffer.getShort());
            blocks.add(new CityBlock((chunk.chunkX << 4) + localX, y, (chunk.chunkZ << 4) + localZ, palette));
        }
        return blocks;
    }

    private static byte[] inflate(byte[] source, int offset, int length) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(source, offset, length);
        byte[] out = new byte[1024 * 1024];
        try {
            int inflated = inflater.inflate(out);
            if (!inflater.finished()) {
                throw new IOException("Atlantis chunk inflated beyond expected buffer.");
            }
            byte[] result = new byte[inflated];
            System.arraycopy(out, 0, result, 0, inflated);
            return result;
        } catch (DataFormatException ex) {
            throw new IOException("Invalid compressed Atlantis city chunk.", ex);
        } finally {
            inflater.end();
        }
    }

    private static String safeFileName(String name) {
        String safe = name.replaceAll("[^A-Za-z0-9_.-]", "_");
        return safe.isBlank() ? "Atlantis_City_Edited" : safe;
    }

    private static String blockId(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static boolean isWater(BlockState state) {
        return state.getFluidState().is(Fluids.WATER);
    }

    private static boolean isDrainTraversable(BlockState state) {
        return state.isAir() || isWater(state);
    }

    public record CityBounds(BlockPos min, BlockPos max) {
    }

    private abstract static class CityJob {
        final ServerLevel level;
        final UUID playerId;
        int ticks;

        CityJob(ServerLevel level, UUID playerId) {
            this.level = level;
            this.playerId = playerId;
        }

        abstract boolean tick() throws IOException;

        void notifyProgress(String message) {
            notify(message, ChatFormatting.AQUA);
        }

        void notifyDone(String message) {
            notify(message, ChatFormatting.GREEN);
        }

        void notifyFailure(String message) {
            notify(message, ChatFormatting.RED);
        }

        private void notify(String message, ChatFormatting formatting) {
            LanteaCraft.LOGGER.info(message);
            if (playerId != null) {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerId);
                if (player != null) {
                    player.sendSystemMessage(Component.literal(message).withStyle(formatting));
                }
            }
        }
    }

    private static final class PlaceJob extends CityJob {
        private final CityData city;
        private final BlockPos origin;
        private int chunkIndex;
        private List<CityBlock> currentBlocks = List.of();
        private int blockIndex;
        private int placed;

        PlaceJob(ServerLevel level, UUID playerId, CityData city, BlockPos origin) {
            super(level, playerId);
            this.city = city;
            this.origin = origin;
        }

        @Override
        boolean tick() throws IOException {
            int budget = PLACE_BLOCKS_PER_TICK;
            while (budget > 0) {
                if (blockIndex >= currentBlocks.size()) {
                    if (chunkIndex >= city.chunks.size()) {
                        notifyDone("Atlantis city placement complete: " + placed + " blocks.");
                        return true;
                    }
                    currentBlocks = decodeChunk(city, city.chunks.get(chunkIndex++));
                    blockIndex = 0;
                }

                CityBlock block = currentBlocks.get(blockIndex++);
                level.setBlock(origin.offset(block.x, block.y, block.z), city.palette.get(block.palette), Block.UPDATE_CLIENTS);
                placed++;
                budget--;
            }

            ticks++;
            if (ticks % 100 == 0) {
                notifyProgress("Atlantis city placement: " + placed + "/" + city.blockCount + " blocks.");
            }
            return false;
        }
    }

    private static final class ExportJob extends CityJob {
        private final BlockPos min;
        private final BlockPos max;
        private final BlockPos origin;
        private final Path output;
        private BufferedWriter writer;
        private int x;
        private int y;
        private int z;
        private boolean first = true;
        private int exported;

        ExportJob(ServerLevel level, UUID playerId, BlockPos min, BlockPos max, BlockPos origin, Path output) throws IOException {
            super(level, playerId);
            this.min = min;
            this.max = max;
            this.origin = origin;
            this.output = output;
            this.x = min.getX();
            this.y = min.getY();
            this.z = min.getZ();
            Files.createDirectories(output.getParent());
            this.writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);
            this.writer.write("[");
        }

        @Override
        boolean tick() throws IOException {
            int budget = EXPORT_POSITIONS_PER_TICK;
            while (budget > 0) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && !state.is(Blocks.WATER)) {
                    if (!first) {
                        writer.write(",");
                    }
                    first = false;
                    writer.write("[[");
                    writer.write(Integer.toString(pos.getX() - origin.getX()));
                    writer.write(",");
                    writer.write(Integer.toString(pos.getY() - origin.getY()));
                    writer.write(",");
                    writer.write(Integer.toString(pos.getZ() - origin.getZ()));
                    writer.write("],\"");
                    writer.write(blockId(state));
                    writer.write("\"]");
                    exported++;
                }

                if (advance()) {
                    writer.write("]\n");
                    writer.close();
                    writer = null;
                    notifyDone("Atlantis city export complete: " + exported + " blocks written to " + output + ".");
                    return true;
                }
                budget--;
            }

            ticks++;
            if (ticks % 100 == 0) {
                notifyProgress("Atlantis city export: " + exported + " blocks written so far.");
                writer.flush();
            }
            return false;
        }

        private boolean advance() {
            x++;
            if (x <= max.getX()) {
                return false;
            }
            x = min.getX();
            z++;
            if (z <= max.getZ()) {
                return false;
            }
            z = min.getZ();
            y++;
            return y > max.getY();
        }
    }

    private static final class DrainJob extends CityJob {
        private final BlockPos min;
        private final BlockPos max;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;
        private final int volume;
        private final BitSet reachable;
        private final IntQueue queue = new IntQueue();
        private DrainPhase phase = DrainPhase.SEED;
        private int cursor;
        private int drained;

        DrainJob(ServerLevel level, UUID playerId, BlockPos min, BlockPos max) throws IOException {
            super(level, playerId);
            this.min = min;
            this.max = max;
            this.sizeX = max.getX() - min.getX() + 1;
            this.sizeY = max.getY() - min.getY() + 1;
            this.sizeZ = max.getZ() - min.getZ() + 1;
            long computedVolume = (long) sizeX * sizeY * sizeZ;
            if (computedVolume > MAX_DRAIN_VOLUME) {
                throw new IOException("Drain volume is " + computedVolume + " positions; split it into sections of " + MAX_DRAIN_VOLUME + " or less.");
            }
            this.volume = (int) computedVolume;
            this.reachable = new BitSet(volume);
        }

        @Override
        boolean tick() {
            int budget = DRAIN_POSITIONS_PER_TICK;
            while (budget > 0) {
                if (phase == DrainPhase.SEED) {
                    budget = seedBoundary(budget);
                    if (cursor >= volume) {
                        cursor = 0;
                        phase = DrainPhase.FLOOD;
                        notifyProgress("Atlantis drain found outside water/air boundary; spreading through leaks.");
                    }
                } else if (phase == DrainPhase.FLOOD) {
                    budget = floodOutside(budget);
                    if (queue.isEmpty()) {
                        cursor = 0;
                        phase = DrainPhase.DRAIN;
                        notifyProgress("Atlantis drain sealed-volume scan started.");
                    }
                } else {
                    budget = drainSealedWater(budget);
                    if (cursor >= volume) {
                        notifyDone("Atlantis drain complete: removed " + drained + " water blocks.");
                        return true;
                    }
                }
            }

            ticks++;
            if (ticks % 100 == 0) {
                notifyProgress("Atlantis drain " + phase.name().toLowerCase() + ": " + cursor + "/" + volume + " positions, " + drained + " water removed.");
            }
            return false;
        }

        private int seedBoundary(int budget) {
            while (budget > 0 && cursor < volume) {
                int index = cursor++;
                int localX = localX(index);
                int localY = localY(index);
                int localZ = localZ(index);
                if (isBoundary(localX, localY, localZ) && !reachable.get(index) && isDrainTraversable(level.getBlockState(worldPos(localX, localY, localZ)))) {
                    reachable.set(index);
                    queue.add(index);
                }
                budget--;
            }
            return budget;
        }

        private int floodOutside(int budget) {
            while (budget > 0 && !queue.isEmpty()) {
                int index = queue.remove();
                int localX = localX(index);
                int localY = localY(index);
                int localZ = localZ(index);
                enqueueNeighbor(localX - 1, localY, localZ);
                enqueueNeighbor(localX + 1, localY, localZ);
                enqueueNeighbor(localX, localY - 1, localZ);
                enqueueNeighbor(localX, localY + 1, localZ);
                enqueueNeighbor(localX, localY, localZ - 1);
                enqueueNeighbor(localX, localY, localZ + 1);
                budget--;
            }
            return budget;
        }

        private int drainSealedWater(int budget) {
            while (budget > 0 && cursor < volume) {
                int index = cursor++;
                if (!reachable.get(index)) {
                    BlockPos pos = worldPos(localX(index), localY(index), localZ(index));
                    if (isWater(level.getBlockState(pos))) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                        drained++;
                    }
                }
                budget--;
            }
            return budget;
        }

        private void enqueueNeighbor(int localX, int localY, int localZ) {
            if (localX < 0 || localX >= sizeX || localY < 0 || localY >= sizeY || localZ < 0 || localZ >= sizeZ) {
                return;
            }

            int index = index(localX, localY, localZ);
            if (!reachable.get(index) && isDrainTraversable(level.getBlockState(worldPos(localX, localY, localZ)))) {
                reachable.set(index);
                queue.add(index);
            }
        }

        private boolean isBoundary(int localX, int localY, int localZ) {
            return localX == 0 || localX == sizeX - 1 || localY == 0 || localY == sizeY - 1 || localZ == 0 || localZ == sizeZ - 1;
        }

        private int index(int localX, int localY, int localZ) {
            return (localY * sizeZ + localZ) * sizeX + localX;
        }

        private int localX(int index) {
            return index % sizeX;
        }

        private int localZ(int index) {
            return (index / sizeX) % sizeZ;
        }

        private int localY(int index) {
            return index / (sizeX * sizeZ);
        }

        private BlockPos worldPos(int localX, int localY, int localZ) {
            return new BlockPos(min.getX() + localX, min.getY() + localY, min.getZ() + localZ);
        }
    }

    private static final class ConnectedDrainJob extends CityJob {
        private final BlockPos min;
        private final BlockPos max;
        private final BlockPos center;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;
        private final int volume;
        private final BitSet visited;
        private final IntQueue queue = new IntQueue();
        private boolean seeded;
        private int drained;
        private int scanned;

        ConnectedDrainJob(ServerLevel level, UUID playerId, BlockPos min, BlockPos max, BlockPos center) throws IOException {
            super(level, playerId);
            this.min = min;
            this.max = max;
            this.center = center;
            this.sizeX = max.getX() - min.getX() + 1;
            this.sizeY = max.getY() - min.getY() + 1;
            this.sizeZ = max.getZ() - min.getZ() + 1;
            long computedVolume = (long) sizeX * sizeY * sizeZ;
            if (computedVolume > MAX_DRAIN_VOLUME) {
                throw new IOException("Drain volume is " + computedVolume + " positions; use a smaller radius.");
            }
            this.volume = (int) computedVolume;
            this.visited = new BitSet(volume);
        }

        @Override
        boolean tick() {
            if (!seeded) {
                seeded = true;
                int localX = center.getX() - min.getX();
                int localY = center.getY() - min.getY();
                int localZ = center.getZ() - min.getZ();
                if (!enqueueIfTraversable(localX, localY, localZ)) {
                    notifyDone("Atlantis connected drain complete: start position is solid, removed 0 water blocks.");
                    return true;
                }
            }

            int budget = DRAIN_POSITIONS_PER_TICK;
            while (budget > 0 && !queue.isEmpty()) {
                int index = queue.remove();
                int localX = localX(index);
                int localY = localY(index);
                int localZ = localZ(index);
                BlockPos pos = worldPos(localX, localY, localZ);
                if (isWater(level.getBlockState(pos))) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    drained++;
                }

                enqueueIfTraversable(localX - 1, localY, localZ);
                enqueueIfTraversable(localX + 1, localY, localZ);
                enqueueIfTraversable(localX, localY - 1, localZ);
                enqueueIfTraversable(localX, localY + 1, localZ);
                enqueueIfTraversable(localX, localY, localZ - 1);
                enqueueIfTraversable(localX, localY, localZ + 1);
                scanned++;
                budget--;
            }

            if (queue.isEmpty()) {
                notifyDone("Atlantis connected drain complete: removed " + drained + " water blocks.");
                return true;
            }

            ticks++;
            if (ticks % 100 == 0) {
                notifyProgress("Atlantis connected drain: scanned " + scanned + " positions, removed " + drained + " water blocks.");
            }
            return false;
        }

        private boolean enqueueIfTraversable(int localX, int localY, int localZ) {
            if (localX < 0 || localX >= sizeX || localY < 0 || localY >= sizeY || localZ < 0 || localZ >= sizeZ) {
                return false;
            }

            int index = index(localX, localY, localZ);
            if (visited.get(index) || !isDrainTraversable(level.getBlockState(worldPos(localX, localY, localZ)))) {
                return false;
            }

            visited.set(index);
            queue.add(index);
            return true;
        }

        private int index(int localX, int localY, int localZ) {
            return (localY * sizeZ + localZ) * sizeX + localX;
        }

        private int localX(int index) {
            return index % sizeX;
        }

        private int localZ(int index) {
            return (index / sizeX) % sizeZ;
        }

        private int localY(int index) {
            return index / (sizeX * sizeZ);
        }

        private BlockPos worldPos(int localX, int localY, int localZ) {
            return new BlockPos(min.getX() + localX, min.getY() + localY, min.getZ() + localZ);
        }
    }

    private enum DrainPhase {
        SEED,
        FLOOD,
        DRAIN
    }

    private static final class IntQueue {
        private int[] data = new int[1024];
        private int head;
        private int tail;

        void add(int value) {
            if (tail >= data.length) {
                compactOrGrow();
            }
            data[tail++] = value;
        }

        int remove() {
            return data[head++];
        }

        boolean isEmpty() {
            return head >= tail;
        }

        private void compactOrGrow() {
            int size = tail - head;
            if (head > data.length / 2) {
                System.arraycopy(data, head, data, 0, size);
                head = 0;
                tail = size;
                return;
            }

            int[] next = new int[data.length * 2];
            System.arraycopy(data, head, next, 0, size);
            data = next;
            head = 0;
            tail = size;
        }
    }

    private record CityData(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int blockCount,
                            List<BlockState> palette, List<CityChunk> chunks, byte[] binary) {
    }

    private record CityChunk(int chunkX, int chunkZ, int offset, int length, int blocks) {
    }

    private record CityBlock(int x, int y, int z, int palette) {
    }
}
