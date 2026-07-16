package com.pclogix.lanteacraft.worldgen;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ExpeditionTrialTracker {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final double EXPEDITION_MESSAGE_RADIUS = 512.0D;
    private ExpeditionTrialTracker() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || level.getGameTime() % CHECK_INTERVAL_TICKS != 0L) {
            return;
        }

        ExpeditionSavedData data = ExpeditionSavedData.get(level);
        for (ExpeditionInstance expedition : data.expeditions()) {
            if (!expedition.dimension().equals(level.dimension().location())
                    || !expedition.generated()
                    || expedition.combatRoomCenters().isEmpty()) {
                continue;
            }

            boolean allDestroyed = allSpawnersDestroyed(level, expedition);
            if (expedition.rewardUnlocked() && !allDestroyed) {
                data.markRewardLocked(expedition.address());
                continue;
            }
            if (!expedition.rewardUnlocked() && allDestroyed) {
                ExpeditionGenerator.unlockRewardDoor(level, expedition);
                data.markRewardUnlocked(expedition.address());
                notifyPlayersInside(level, expedition);
            }
        }
    }

    private static boolean allSpawnersDestroyed(ServerLevel level, ExpeditionInstance expedition) {
        for (BlockPos spawnerPos : expedition.combatRoomCenters()) {
            if (level.getBlockState(spawnerPos).is(Blocks.SPAWNER)) {
                return false;
            }
        }
        return true;
    }

    private static void notifyPlayersInside(ServerLevel level, ExpeditionInstance expedition) {
        double radiusSqr = EXPEDITION_MESSAGE_RADIUS * EXPEDITION_MESSAGE_RADIUS;
        BlockPos basePos = expedition.basePos();
        for (ServerPlayer player : level.players()) {
            double dx = player.getX() - basePos.getX();
            double dz = player.getZ() - basePos.getZ();
            if (dx * dx + dz * dz <= radiusSqr) {
                player.sendSystemMessage(Component.translatable("message.lanteacraft.expedition_rewards_open")
                        .withStyle(ChatFormatting.GOLD));
            }
        }
    }
}
