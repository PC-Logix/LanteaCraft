package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class GeneratedGateProtection {
    private GeneratedGateProtection() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(GeneratedGateProtection::onBlockBreak);
    }

    public static boolean isProtectedGateBase(ServerLevel level, BlockPos basePos) {
        return Config.PROTECT_GENERATED_GATES.getAsBoolean()
                && level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base
                && base.hasAncientPower();
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || event.getPlayer().getAbilities().instabuild
                || !isProtected(level, event.getPos(), event.getState())) {
            return;
        }

        event.setCanceled(true);
        event.getPlayer().displayClientMessage(
                Component.translatable("message.lanteacraft.generated_gate_unbreakable").withStyle(ChatFormatting.GOLD),
                true);
    }

    private static boolean isProtected(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof StargateBaseBlock) {
            return isProtectedGateBase(level, pos);
        }
        if (state.getBlock() instanceof StargateComponentBlock) {
            return StargateMultiblock.findBasePosFrom(level, pos)
                    .map(basePos -> isProtectedGateBase(level, basePos))
                    .orElse(false);
        }
        if (state.getBlock() instanceof DhdBlock) {
            return StargateMultiblock.findNearestEntry(level, pos, Config.DHD_SEARCH_RADIUS.get())
                    .map(StargateEntry::basePos)
                    .map(basePos -> isProtectedGateBase(level, basePos))
                    .orElse(false);
        }
        return false;
    }
}
