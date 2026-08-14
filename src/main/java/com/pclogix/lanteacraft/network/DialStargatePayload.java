package com.pclogix.lanteacraft.network;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.gate.StargateAddress;
import com.pclogix.lanteacraft.gate.StargateDialer;
import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import java.util.Optional;
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

public record DialStargatePayload(BlockPos dhdPos, String address) implements CustomPacketPayload {
    public static final Type<DialStargatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "dial_stargate"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialStargatePayload> STREAM_CODEC = CustomPacketPayload.codec(
            DialStargatePayload::write,
            DialStargatePayload::read);

    private static final int MAX_ADDRESS_LENGTH = StargateAddress.MAX_ADDRESS_LENGTH;

    private static DialStargatePayload read(RegistryFriendlyByteBuf buffer) {
        return new DialStargatePayload(buffer.readBlockPos(), normalize(buffer.readUtf(MAX_ADDRESS_LENGTH)));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(dhdPos);
        buffer.writeUtf(normalize(address), MAX_ADDRESS_LENGTH);
    }

    public static void handle(DialStargatePayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (!(level.getBlockState(payload.dhdPos()).getBlock() instanceof DhdBlock)) {
            player.displayClientMessage(Component.literal("DHD is no longer present.").withStyle(ChatFormatting.RED), false);
            return;
        }
        DhdBlock.pulse(level, payload.dhdPos());

        String targetAddress = StargateDialer.normalize(payload.address());
        Optional<StargateEntry> localGate = StargateMultiblock.findNearestEntry(level, payload.dhdPos(), Config.DHD_SEARCH_RADIUS.get());
        if (localGate.isEmpty()) {
            LanteaCraft.LOGGER.warn("DHD at {} failed to dial {}: no assembled Stargate within {} blocks.", payload.dhdPos(), targetAddress, Config.DHD_SEARCH_RADIUS.get());
            player.displayClientMessage(Component.literal("No assembled local Stargate found near this DHD.").withStyle(ChatFormatting.RED), false);
            return;
        }

        StargateEntry local = localGate.get();
        if (targetAddress.isBlank()) {
            StargateDialer.DialResult result = StargateDialer.dial(level, local, targetAddress);
            if (result.success()) {
                if ("dialing".equals(result.code())) {
                    player.displayClientMessage(Component.literal(result.message()).withStyle(ChatFormatting.GREEN), false);
                } else {
                    player.displayClientMessage(Component.literal("Stargate disconnected.").withStyle(ChatFormatting.GRAY), false);
                }
            } else if ("incoming_active".equals(result.code())) {
                player.displayClientMessage(Component.literal("Incoming wormholes must be closed from the dialing gate.").withStyle(ChatFormatting.RED), false);
            } else {
                player.displayClientMessage(Component.literal("No outgoing Stargate connection to disconnect.").withStyle(ChatFormatting.GRAY), false);
            }
            return;
        }

        StargateDialer.DialResult result = StargateDialer.dial(level, local, targetAddress, player);
        if (!result.success()) {
            LanteaCraft.LOGGER.warn("DHD at {} failed to dial {} from local gate {}: {} ({})", payload.dhdPos(), targetAddress, local.basePos(), result.message(), result.code());
            player.displayClientMessage(Component.literal(result.message()).withStyle(ChatFormatting.RED), false);
            return;
        }

        LanteaCraft.LOGGER.info("DHD at {} dialing {} from local gate {}", payload.dhdPos(), targetAddress, local.basePos());
        player.displayClientMessage(
                Component.literal("Dialing ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(targetAddress).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("... target locked.").withStyle(ChatFormatting.GREEN)),
                false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static String normalize(String address) {
        return StargateDialer.normalize(address);
    }
}
