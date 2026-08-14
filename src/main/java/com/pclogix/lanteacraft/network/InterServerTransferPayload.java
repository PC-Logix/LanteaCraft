package com.pclogix.lanteacraft.network;

import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record InterServerTransferPayload(String host, int port, String token) implements CustomPacketPayload {
    public static final Type<InterServerTransferPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "inter_server_transfer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InterServerTransferPayload> STREAM_CODEC = CustomPacketPayload.codec(
            InterServerTransferPayload::write,
            InterServerTransferPayload::read);

    private static InterServerTransferPayload read(RegistryFriendlyByteBuf buffer) {
        return new InterServerTransferPayload(buffer.readUtf(255), buffer.readVarInt(), buffer.readUtf(2048));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUtf(host, 255);
        buffer.writeVarInt(port);
        buffer.writeUtf(token, 2048);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
