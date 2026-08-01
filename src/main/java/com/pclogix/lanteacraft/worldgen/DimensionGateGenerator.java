package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.pclogix.lanteacraft.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class DimensionGateGenerator {
    private static final ResourceLocation ABYDOS_PLATFORM_TEMPLATE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "abydos_stargate_platform");
    private static final BlockPos ABYDOS_TEMPLATE_PIVOT = new BlockPos(5, 1, 2);

    private DimensionGateGenerator() {
    }

    public static boolean placeIfNeeded(ServerLevel level, PlannedStargate plan) {
        PlannedStargateSavedData data = PlannedStargateSavedData.get(level);
        BlockPos basePos = existingOrPlannedBase(level, plan);
        boolean hasDuplicates = hasDuplicateFixedGateBases(level, plan);
        if (isAssembledGateAt(level, basePos) && !hasDuplicates) {
            if (level.dimension().equals(LanteaDimensions.ABYDOS)) {
                AbydosPyramidGenerator.ensureInitialized(level, basePos, plan.facing(), 0);
                applyAbydosCamouflage(level, basePos);
            }
            StargateNetworkSavedData.get(level).registerOrUpdateActiveGate(plan.address(), level.dimension(), basePos, plan.facing(), plan.variant(), "fixed_dimension");
            if (!data.isPlaced(plan.address())) {
                data.markPlaced(plan.address());
            }
            return false;
        }

        if (level.dimension().equals(LanteaDimensions.ATLANTIS)) {
            basePos = placeAtlantisGate(level, plan);
        } else {
            basePos = placeAbydosGate(level, plan);
        }

        StargateNetworkSavedData.get(level).registerOrUpdateActiveGate(plan.address(), level.dimension(), basePos, plan.facing(), plan.variant(), "fixed_dimension");
        forceFrameAssembled(level, basePos, plan.facing());
        StargateMultiblock.tryAssembleAtBase(level, basePos);
        if (level.dimension().equals(LanteaDimensions.ABYDOS)) {
            applyAbydosCamouflage(level, basePos);
        }
        data.markPlaced(plan.address());
        return true;
    }

    private static BlockPos placeAbydosGate(ServerLevel level, PlannedStargate plan) {
        clearDuplicateAbydosInstallations(level, plan);
        BlockPos basePos = abydosGateBase(level, plan.basePos());
        Direction facing = plan.facing();
        Direction right = facing.getClockWise();

        for (int r = -7; r <= 7; r++) {
            for (int f = -5; f <= 9; f++) {
                BlockPos floor = basePos.relative(right, r).relative(facing, f).below();
                for (int y = 0; y <= 7; y++) {
                    BlockPos clear = floor.above(y + 1);
                    if (!level.getBlockState(clear).is(Blocks.BEDROCK)) {
                        level.setBlock(clear, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }

        placeAbydosPlatform(level, basePos, facing);
        AbydosPyramidGenerator.ensureInitialized(level, basePos, facing, AbydosPyramidGenerator.GATE_PLATFORM_HEIGHT);
        placeFrame(level, basePos, facing, plan.variant());
        placeDhd(level, basePos, facing, plan.variant());
        return basePos;
    }

    private static void placeAbydosPlatform(ServerLevel level, BlockPos basePos, Direction facing) {
        Optional<StructureTemplate> template = level.getStructureManager().get(ABYDOS_PLATFORM_TEMPLATE);
        if (template.isEmpty()) {
            LanteaCraft.LOGGER.warn("Missing Abydos Stargate platform template {}; using generated sandstone footing.", ABYDOS_PLATFORM_TEMPLATE);
            Direction right = facing.getClockWise();
            for (int r = -5; r <= 5; r++) {
                for (int f = -2; f <= 6; f++) {
                    level.setBlock(basePos.relative(right, r).relative(facing, f).below(), Blocks.SMOOTH_SANDSTONE.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            return;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotationFor(facing))
                .setRotationPivot(ABYDOS_TEMPLATE_PIVOT)
                .setIgnoreEntities(true)
                .setKnownShape(true);
        BlockPos origin = basePos.offset(
                -ABYDOS_TEMPLATE_PIVOT.getX(),
                -ABYDOS_TEMPLATE_PIVOT.getY() + 1,
                -ABYDOS_TEMPLATE_PIVOT.getZ()
        );
        template.get().placeInWorld(level, origin, origin, settings, RandomSource.create(level.getSeed() ^ basePos.asLong()), Block.UPDATE_ALL);
    }

    private static void applyAbydosCamouflage(ServerLevel level, BlockPos basePos) {
        if (!(level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base)) {
            return;
        }

        BlockState platformState = level.getBlockState(basePos.below());
        BlockState intendedCamouflage = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
        if (!base.hasBottomCamouflage() || base.bottomCamouflage().is(platformState.getBlock())) {
            base.setBottomCamouflage(intendedCamouflage);
        }
    }

    private static Rotation rotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            case NORTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.CLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    private static void clearDuplicateAbydosInstallations(ServerLevel level, PlannedStargate plan) {
        for (BlockPos basePos : fixedGateBases(level, plan)) {
            clearAbydosInstallationAt(level, basePos, plan.facing());
        }
    }

    private static void clearAbydosInstallationAt(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int r = -8; r <= 8; r++) {
            for (int f = -6; f <= 10; f++) {
                for (int y = -1; y <= 8; y++) {
                    clearGeneratedAbydosBlock(level, basePos.relative(right, r).relative(facing, f).above(y));
                }
            }
        }

        BlockPos pyramidCenter = basePos.relative(facing.getOpposite(), 18);
        for (int layer = 0; layer < 5; layer++) {
            int radius = 5 - layer;
            for (int r = -radius; r <= radius; r++) {
                for (int f = -radius; f <= radius; f++) {
                    clearGeneratedAbydosBlock(level, pyramidCenter.relative(right, r).relative(facing, f).above(layer));
                }
            }
        }
    }

    private static void clearGeneratedAbydosBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (isGeneratedAbydosBlock(state)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static boolean isGeneratedAbydosBlock(BlockState state) {
        return state.getBlock() instanceof StargateBaseBlock
                || state.getBlock() instanceof StargateComponentBlock
                || state.getBlock() instanceof DhdBlock
                || state.is(Blocks.SMOOTH_SANDSTONE)
                || state.is(Blocks.CUT_SANDSTONE);
    }

    private static BlockPos placeAtlantisGate(ServerLevel level, PlannedStargate plan) {
        BlockPos basePos = plan.basePos();
        Direction facing = plan.facing();
        Direction right = facing.getClockWise();

        placeAtlantisOceanField(level, basePos);

        for (int r = -9; r <= 9; r++) {
            for (int f = -9; f <= 9; f++) {
                for (int y = -1; y <= 9; y++) {
                    BlockPos pos = basePos.relative(right, r).relative(facing, f).above(y);
                    boolean boundary = r == -9 || r == 9 || f == -9 || f == 9 || y == -1 || y == 9;
                    if (boundary) {
                        level.setBlock(pos, atlantisShellState(r, f, y), Block.UPDATE_ALL);
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }

        for (int r = -4; r <= 4; r++) {
            for (int f = -3; f <= 5; f++) {
                level.setBlock(basePos.relative(right, r).relative(facing, f).below(), ModBlocks.LANTEAN_PANEL.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        placeFrame(level, basePos, facing, plan.variant());
        placeDhd(level, basePos, facing, plan.variant());
        return basePos;
    }

    private static void placeAtlantisOceanField(ServerLevel level, BlockPos basePos) {
        int radius = 56;
        int seaFloor = 30;
        int waterTop = 63;
        for (int x = basePos.getX() - radius; x <= basePos.getX() + radius; x++) {
            for (int z = basePos.getZ() - radius; z <= basePos.getZ() + radius; z++) {
                for (int y = seaFloor - 3; y <= waterTop; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (y < seaFloor) {
                        level.setBlock(pos, Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (y == seaFloor) {
                        level.setBlock(pos, Blocks.SAND.defaultBlockState(), Block.UPDATE_ALL);
                    } else {
                        level.setBlock(pos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
                for (int y = waterTop + 1; y <= waterTop + 16; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private static BlockState atlantisShellState(int r, int f, int y) {
        if (y == 9 && Math.abs(r) <= 3 && Math.abs(f) <= 3) {
            return ModBlocks.LANTEAN_GLASS.get().defaultBlockState();
        }
        if ((Math.abs(r) == 9 || Math.abs(f) == 9) && y >= 2 && y <= 5 && (Math.abs(r) <= 5 || Math.abs(f) <= 5)) {
            return ModBlocks.LANTEAN_GLASS.get().defaultBlockState();
        }
        if (y == -1) {
            return ModBlocks.LANTEAN_PANEL.get().defaultBlockState();
        }
        if (y == 9) {
            return ModBlocks.LANTEAN_CARVED_WALL.get().defaultBlockState();
        }
        return ModBlocks.LANTEAN_WALL.get().defaultBlockState();
    }

    private static void placeFrame(ServerLevel level, BlockPos basePos, Direction facing, StargateVariant variant) {
        Direction right = facing.getClockWise();
        for (int y = 0; y <= 6; y++) {
            for (int x = -3; x <= 3; x++) {
                BlockPos pos = basePos.relative(right, x).above(y);
                if (x == 0 && y == 0) {
                    level.setBlock(basePos, baseBlock(variant).defaultBlockState().setValue(StargateBaseBlock.FACING, facing), Block.UPDATE_ALL);
                    if (level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base) {
                        base.setAncientPower(true);
                    }
                } else if (isFramePosition(x, y)) {
                    Block block = isChevronPosition(x, y) ? chevronBlock(variant) : ringBlock(variant);
                    level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private static void placeDhd(ServerLevel level, BlockPos basePos, Direction facing, StargateVariant variant) {
        BlockPos dhdPos = basePos.relative(facing, 4).above(1);
        level.setBlock(dhdPos.below(), level.dimension().equals(LanteaDimensions.ATLANTIS)
                ? ModBlocks.LANTEAN_PANEL.get().defaultBlockState()
                : Blocks.SMOOTH_SANDSTONE.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(dhdPos, dhdBlock(variant).defaultBlockState().setValue(DhdBlock.FACING, facing), Block.UPDATE_ALL);
        if (level.getBlockEntity(dhdPos) instanceof DhdBlockEntity dhd) {
            dhd.installChargedCrystal();
        }
    }

    private static BlockPos existingOrPlannedBase(ServerLevel level, PlannedStargate plan) {
        return existingFixedGateBase(level, plan)
                .orElseGet(() -> {
                    if (level.dimension().equals(LanteaDimensions.ATLANTIS)) {
                        return plan.basePos();
                    }
                    return level.dimension().equals(LanteaDimensions.ABYDOS)
                            ? abydosGateBase(level, plan.basePos())
                            : surfaceBase(level, plan.basePos());
                });
    }

    private static Optional<BlockPos> existingFixedGateBase(ServerLevel level, PlannedStargate plan) {
        return fixedGateBases(level, plan).stream().findFirst();
    }

    private static List<BlockPos> fixedGateBases(ServerLevel level, PlannedStargate plan) {
        List<BlockPos> bases = new ArrayList<>();
        int minY = Math.max(level.getMinBuildHeight(), plan.basePos().getY() - 48);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, plan.basePos().getY() + 96);
        for (int y = minY; y <= maxY; y++) {
            BlockPos pos = new BlockPos(plan.basePos().getX(), y, plan.basePos().getZ());
            if (isFixedGateBase(level.getBlockState(pos), plan)) {
                bases.add(pos);
            }
        }

        return bases;
    }

    private static boolean hasDuplicateFixedGateBases(ServerLevel level, PlannedStargate plan) {
        return fixedGateBases(level, plan).size() > 1;
    }

    private static boolean isFixedGateBase(BlockState state, PlannedStargate plan) {
        return state.getBlock() instanceof StargateBaseBlock base
                && base.variant() == plan.variant()
                && state.hasProperty(StargateBaseBlock.FACING)
                && state.getValue(StargateBaseBlock.FACING) == plan.facing();
    }

    private static boolean isAssembledGateAt(ServerLevel level, BlockPos basePos) {
        BlockState state = level.getBlockState(basePos);
        return state.hasProperty(StargateBaseBlock.ASSEMBLED) && state.getValue(StargateBaseBlock.ASSEMBLED);
    }

    private static void forceFrameAssembled(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int y = 0; y <= 6; y++) {
            for (int x = -3; x <= 3; x++) {
                if (x == 0 && y == 0) {
                    BlockPos pos = basePos;
                    BlockState state = level.getBlockState(pos);
                    if (state.hasProperty(StargateBaseBlock.ASSEMBLED)) {
                        level.setBlock(pos, state.setValue(StargateBaseBlock.ASSEMBLED, true), Block.UPDATE_ALL);
                    }
                } else if (isFramePosition(x, y)) {
                    BlockPos pos = basePos.relative(right, x).above(y);
                    BlockState state = level.getBlockState(pos);
                    if (state.hasProperty(StargateComponentBlock.ASSEMBLED)) {
                        level.setBlock(pos, state.setValue(StargateComponentBlock.ASSEMBLED, true), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private static BlockPos surfaceBase(ServerLevel level, BlockPos plannedBase) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, plannedBase.getX(), plannedBase.getZ());
        return new BlockPos(plannedBase.getX(), Math.max(level.getMinBuildHeight() + 1, y), plannedBase.getZ());
    }

    private static BlockPos abydosGateBase(ServerLevel level, BlockPos plannedBase) {
        return surfaceBase(level, plannedBase).above(AbydosPyramidGenerator.GATE_PLATFORM_HEIGHT);
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

    private static Block baseBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.STARGATE_BASE.get();
            case NOX -> ModBlocks.NOX_STARGATE_BASE.get();
            case WRAITH -> ModBlocks.WRAITH_STARGATE_BASE.get();
            case PEGASUS -> ModBlocks.PEGASUS_STARGATE_BASE.get();
        };
    }

    private static Block ringBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.STARGATE_RING.get();
            case NOX -> ModBlocks.NOX_STARGATE_RING.get();
            case WRAITH -> ModBlocks.WRAITH_STARGATE_RING.get();
            case PEGASUS -> ModBlocks.PEGASUS_STARGATE_RING.get();
        };
    }

    private static Block chevronBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.STARGATE_CHEVRON.get();
            case NOX -> ModBlocks.NOX_STARGATE_CHEVRON.get();
            case WRAITH -> ModBlocks.WRAITH_STARGATE_CHEVRON.get();
            case PEGASUS -> ModBlocks.PEGASUS_STARGATE_CHEVRON.get();
        };
    }

    private static Block dhdBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.DHD.get();
            case NOX -> ModBlocks.NOX_DHD.get();
            case WRAITH -> ModBlocks.WRAITH_DHD.get();
            case PEGASUS -> ModBlocks.PEGASUS_DHD.get();
        };
    }
}
