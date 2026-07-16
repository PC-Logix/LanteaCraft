package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class StargateMultiblock {
    private static final List<PatternPart> PARTS = buildPattern();

    private StargateMultiblock() {
    }

    public static void tryAssembleFrom(Level level, BlockPos changedPos) {
        if (!Config.AUTO_ASSEMBLE_STARGATES.getAsBoolean()) {
            return;
        }

        debugLog("Stargate scan started from changed block at {}", changedPos);
        int candidates = 0;
        for (BlockPos basePos : BlockPos.betweenClosed(changedPos.offset(-3, -6, -3), changedPos.offset(3, 0, 3))) {
            BlockState baseState = level.getBlockState(basePos);
            if (baseState.getBlock() instanceof StargateBaseBlock) {
                candidates++;
                debugLog("Stargate scan found candidate base at {} facing {}", basePos, baseState.getValue(StargateBaseBlock.FACING));
                if (tryAssembleAtBase(level, basePos.immutable())) {
                    debugLog("Stargate scan assembled frame using base at {}", basePos);
                    return;
                }
            }
        }
        debugLog("Stargate scan finished from {}; candidate bases found: {}", changedPos, candidates);
    }

    public static boolean tryAssembleAtBase(Level level, BlockPos basePos) {
        if (!Config.AUTO_ASSEMBLE_STARGATES.getAsBoolean()) {
            return false;
        }

        BlockState baseState = level.getBlockState(basePos);
        debugLog("Stargate assembly attempt at base {} with state {}", basePos, baseState);

        if (!(baseState.getBlock() instanceof StargateBaseBlock baseBlock)) {
            debugLog("Stargate assembly aborted at {}: block is not a Stargate Base", basePos);
            return false;
        }

        if (baseState.getValue(StargateBaseBlock.ASSEMBLED)) {
            debugLog("Stargate assembly skipped at {}: base is already assembled", basePos);
            return false;
        }

        Direction facing = baseState.getValue(StargateBaseBlock.FACING);
        StargateVariant variant = baseBlock.variant();
        debugLog("Stargate assembly validating 7x7 frame at {} facing {}; right direction is {}", basePos, facing, facing.getClockWise());
        Optional<List<BlockPos>> frame = findValidFrame(level, basePos, facing, variant);
        if (frame.isEmpty()) {
            debugLog("Stargate assembly failed at {}: pattern mismatch", basePos);
            return false;
        }

        debugLog("Stargate assembly succeeded at {}: marking {} frame blocks assembled", basePos, frame.get().size());
        for (BlockPos partPos : frame.get()) {
            BlockState partState = level.getBlockState(partPos);
            if (partState.hasProperty(StargateComponentBlock.ASSEMBLED)) {
                level.setBlock(partPos, partState.setValue(StargateComponentBlock.ASSEMBLED, true), Block.UPDATE_ALL);
            } else if (partState.hasProperty(StargateBaseBlock.ASSEMBLED)) {
                level.setBlock(partPos, partState.setValue(StargateBaseBlock.ASSEMBLED, true), Block.UPDATE_ALL);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            StargateEntry entry = StargateNetworkSavedData.get(serverLevel).register(serverLevel, basePos, facing, variant);
            debugLog("Stargate registered address {} for base {} in {}", entry.address(), entry.basePos(), entry.dimension());
        }

        return true;
    }

    public static Optional<StargateEntry> findEntryFrom(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        Optional<BlockPos> basePos = findBaseFrom(serverLevel, pos);
        return basePos.flatMap(foundBase -> StargateNetworkSavedData.get(serverLevel).findByBase(serverLevel, foundBase));
    }

    public static Optional<BlockPos> findBasePosFrom(Level level, BlockPos pos) {
        return findBaseFrom(level, pos);
    }

    public static Optional<StargateEntry> findNearestEntry(ServerLevel level, BlockPos origin, int radius) {
        Optional<StargateEntry> nearestEntry = Optional.empty();
        double nearestDistance = Double.MAX_VALUE;

        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            BlockState candidateState = level.getBlockState(candidate);
            if (!(candidateState.getBlock() instanceof StargateBaseBlock) || !candidateState.getValue(StargateBaseBlock.ASSEMBLED)) {
                continue;
            }

            Optional<StargateEntry> entry = StargateNetworkSavedData.get(level).findByBase(level, candidate.immutable());
            if (entry.isEmpty()) {
                continue;
            }

            double distance = candidate.distSqr(origin);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestEntry = entry;
            }
        }

        return nearestEntry;
    }

    public static boolean disassembleFrom(Level level, BlockPos changedPos) {
        return disassembleFrom(level, changedPos, false);
    }

    public static boolean disassembleFrom(Level level, BlockPos changedPos, boolean bypassProtection) {
        boolean disassembled = false;
        for (BlockPos basePos : BlockPos.betweenClosed(changedPos.offset(-3, -6, -3), changedPos.offset(3, 0, 3))) {
            if (level.getBlockState(basePos).getBlock() instanceof StargateBaseBlock) {
                disassembled |= disassembleAtBase(level, basePos.immutable(), bypassProtection);
            }
        }
        return disassembled;
    }

    public static boolean disassembleAtBase(Level level, BlockPos basePos) {
        return disassembleAtBase(level, basePos, false);
    }

    public static boolean disassembleAtBase(Level level, BlockPos basePos, boolean bypassProtection) {
        if (!bypassProtection && level instanceof ServerLevel serverLevel
                && GeneratedGateProtection.isProtectedGateBase(serverLevel, basePos)) {
            return false;
        }
        BlockState baseState = level.getBlockState(basePos);
        if (!(baseState.getBlock() instanceof StargateBaseBlock baseBlock)) {
            return false;
        }

        Direction facing = baseState.getValue(StargateBaseBlock.FACING);
        Optional<List<BlockPos>> frame = findExistingFrame(level, basePos, facing, baseBlock.variant());
        if (frame.isEmpty()) {
            return false;
        }

        for (BlockPos partPos : frame.get()) {
            BlockState partState = level.getBlockState(partPos);
            if (partState.hasProperty(StargateComponentBlock.ASSEMBLED)) {
                level.setBlock(partPos, partState.setValue(StargateComponentBlock.ASSEMBLED, false), Block.UPDATE_ALL);
            } else if (partState.hasProperty(StargateBaseBlock.ASSEMBLED)) {
                updateLinkedDhds(level, partPos, false);
                BlockState disassembledState = partState.setValue(StargateBaseBlock.ASSEMBLED, false);
                if (disassembledState.hasProperty(StargateBaseBlock.WORMHOLE_OPEN)) {
                    disassembledState = disassembledState.setValue(StargateBaseBlock.WORMHOLE_OPEN, false);
                }
                level.setBlock(partPos, disassembledState, Block.UPDATE_ALL);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            StargateNetworkSavedData network = StargateNetworkSavedData.get(serverLevel);
            Optional<StargateEntry> removedGate = network.findByBase(serverLevel, basePos);
            removedGate.ifPresent(gate -> {
                network.findConnectedDestination(gate.address()).ifPresent(destination -> StargateChunkLoading.forceConnection(serverLevel, gate, destination, false));
                network.findIncomingSource(gate.address()).ifPresent(source -> {
                    ServerLevel sourceLevel = serverLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, source.dimension()));
                    if (sourceLevel != null) {
                        StargateChunkLoading.forceConnection(sourceLevel, source, gate, false);
                    }
                });
            });
            network.remove(serverLevel, basePos);
            debugLog("Stargate removed registered address for base {} in {}", basePos, serverLevel.dimension().location());
        }

        return true;
    }

    private static void updateLinkedDhds(Level level, BlockPos basePos, boolean active) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        int radius = Config.DHD_SEARCH_RADIUS.get();
        for (BlockPos pos : BlockPos.betweenClosed(basePos.offset(-radius, -radius, -radius), basePos.offset(radius, radius, radius))) {
            BlockPos dhdPos = pos.immutable();
            if (!(serverLevel.getBlockState(dhdPos).getBlock() instanceof DhdBlock)) {
                continue;
            }

            findNearestEntry(serverLevel, dhdPos, radius)
                    .filter(entry -> entry.basePos().equals(basePos))
                    .ifPresent(entry -> DhdBlock.setActive(serverLevel, dhdPos, active));
        }
    }

    private static Optional<List<BlockPos>> findValidFrame(Level level, BlockPos basePos, Direction facing, StargateVariant variant) {
        return findFrame(level, basePos, facing, variant, false);
    }

    private static Optional<List<BlockPos>> findExistingFrame(Level level, BlockPos basePos, Direction facing, StargateVariant variant) {
        return findFrame(level, basePos, facing, variant, true);
    }

    private static Optional<BlockPos> findBaseFrom(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof StargateBaseBlock) {
            return Optional.of(pos.immutable());
        }

        for (BlockPos basePos : BlockPos.betweenClosed(pos.offset(-3, -6, -3), pos.offset(3, 0, 3))) {
            BlockState baseState = level.getBlockState(basePos);
            if (!(baseState.getBlock() instanceof StargateBaseBlock baseBlock) || !baseState.getValue(StargateBaseBlock.ASSEMBLED)) {
                continue;
            }

            Direction facing = baseState.getValue(StargateBaseBlock.FACING);
            Optional<List<BlockPos>> frame = findExistingFrame(level, basePos.immutable(), facing, baseBlock.variant());
            if (frame.isPresent() && frame.get().contains(pos)) {
                return Optional.of(basePos.immutable());
            }
        }

        return Optional.empty();
    }

    private static Optional<List<BlockPos>> findFrame(Level level, BlockPos basePos, Direction facing, StargateVariant variant, boolean allowMissingChangedPart) {
        Direction right = facing.getClockWise();
        List<BlockPos> frame = new ArrayList<>(PARTS.size());

        for (PatternPart part : PARTS) {
            BlockPos partPos = basePos.relative(right, part.x()).above(part.y());
            BlockState partState = level.getBlockState(partPos);
            if (!part.matches(partState, variant)) {
                if (allowMissingChangedPart && partPos.equals(basePos)) {
                    continue;
                }

                debugLog(
                        "Stargate pattern mismatch for base {} facing {}: expected {} at relative x={}, y={} -> world {}, found {}",
                        basePos,
                        facing,
                        part.part(),
                        part.x(),
                        part.y(),
                        partPos,
                        partState);
                return Optional.empty();
            }

            debugLog(
                    "Stargate pattern matched {} at relative x={}, y={} -> world {} ({})",
                    part.part(),
                    part.x(),
                    part.y(),
                    partPos,
                    partState);
            frame.add(partPos);
        }

        return Optional.of(frame);
    }

    private static List<PatternPart> buildPattern() {
        List<PatternPart> parts = new ArrayList<>(24);
        for (int y = 0; y <= 6; y++) {
            for (int x = -3; x <= 3; x++) {
                if (x == 0 && y == 0) {
                    parts.add(new PatternPart(x, y, GatePart.BASE));
                } else if (isFramePosition(x, y)) {
                    parts.add(new PatternPart(x, y, isChevronPosition(x, y) ? GatePart.CHEVRON : GatePart.RING));
                }
            }
        }

        return List.copyOf(parts);
    }

    private static boolean isFramePosition(int x, int y) {
        return y == 0 || y == 6 || Math.abs(x) == 3;
    }

    private static boolean isChevronPosition(int x, int y) {
        return (x == 0 && y == 6)
                || (x == -2 && y == 6)
                || (x == 2 && y == 6)
                || (x == -3 && y == 4)
                || (x == 3 && y == 4)
                || (x == -3 && y == 1)
                || (x == 3 && y == 1)
                || (x == -2 && y == 0)
                || (x == 2 && y == 0);
    }

    private enum GatePart {
        BASE,
        RING,
        CHEVRON
    }

    private record PatternPart(int x, int y, GatePart part) {
        private boolean matches(BlockState state, StargateVariant variant) {
            return switch (part) {
                case BASE -> state.getBlock() instanceof StargateBaseBlock base && base.variant() == variant;
                case RING -> state.getBlock() instanceof StargateComponentBlock ring && ring.variant() == variant && !isChevronBlock(state);
                case CHEVRON -> state.getBlock() instanceof StargateComponentBlock chevron && chevron.variant() == variant && isChevronBlock(state);
            };
        }
    }

    private static boolean isChevronBlock(BlockState state) {
        return state.is(ModBlocks.STARGATE_CHEVRON.get())
                || state.is(ModBlocks.NOX_STARGATE_CHEVRON.get())
                || state.is(ModBlocks.WRAITH_STARGATE_CHEVRON.get())
                || state.is(ModBlocks.PEGASUS_STARGATE_CHEVRON.get());
    }

    private static void debugLog(String message, Object... args) {
        if (Config.DEBUG_LOGGING.getAsBoolean()) {
            LanteaCraft.LOGGER.info(message, args);
        }
    }
}
