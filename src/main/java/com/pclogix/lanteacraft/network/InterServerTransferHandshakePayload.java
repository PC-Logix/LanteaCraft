package com.pclogix.lanteacraft.network;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.InterServerLinkConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InterServerTransferHandshakePayload(String token) implements CustomPacketPayload {
    public static final Type<InterServerTransferHandshakePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "inter_server_transfer_handshake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InterServerTransferHandshakePayload> STREAM_CODEC = CustomPacketPayload.codec(
            InterServerTransferHandshakePayload::write,
            InterServerTransferHandshakePayload::read);

    private static InterServerTransferHandshakePayload read(RegistryFriendlyByteBuf buffer) {
        return new InterServerTransferHandshakePayload(buffer.readUtf(2048));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(token, 2048);
    }

    public static void handle(InterServerTransferHandshakePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            InterServerLinkConfig.acceptTransfer(player, payload.token());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
