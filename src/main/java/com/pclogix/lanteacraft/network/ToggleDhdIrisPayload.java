package com.pclogix.lanteacraft.network;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleDhdIrisPayload(BlockPos dhdPos) implements CustomPacketPayload {
    public static final Type<ToggleDhdIrisPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "toggle_dhd_iris"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleDhdIrisPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ToggleDhdIrisPayload::write,
            ToggleDhdIrisPayload::read);

    private static ToggleDhdIrisPayload read(RegistryFriendlyByteBuf buffer) {
        return new ToggleDhdIrisPayload(buffer.readBlockPos());
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(dhdPos);
    }

    public static void handle(ToggleDhdIrisPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        StargateMultiblock.findNearestEntry(level, payload.dhdPos(), Config.DHD_SEARCH_RADIUS.get()).ifPresentOrElse(entry -> {
            if (level.getBlockEntity(entry.basePos()) instanceof StargateBaseBlockEntity base && base.hasIris()) {
                boolean changed = base.toggleIris();
                if (!changed && base.isIrisRedstoneLocked()) {
                    player.displayClientMessage(Component.translatable("message.lanteacraft.iris_redstone_locked").withStyle(ChatFormatting.RED), true);
                } else {
                    player.displayClientMessage(Component.translatable(base.isIrisClosedOrClosing()
                            ? "message.lanteacraft.iris_closing"
                            : "message.lanteacraft.iris_opening").withStyle(ChatFormatting.GRAY), true);
                }
            } else {
                LanteaCraft.LOGGER.warn("DHD at {} found Stargate {} for iris toggle, but it has no iris.", payload.dhdPos(), entry.basePos());
                player.displayClientMessage(Component.translatable("message.lanteacraft.iris_missing").withStyle(ChatFormatting.RED), true);
            }
        }, () -> {
            LanteaCraft.LOGGER.warn("DHD at {} failed iris toggle: no assembled Stargate within {} blocks.", payload.dhdPos(), Config.DHD_SEARCH_RADIUS.get());
            player.displayClientMessage(Component.literal("No assembled local Stargate found near this DHD.").withStyle(ChatFormatting.RED), true);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
