package com.pclogix.lanteacraft.network;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleIrisRedstonePayload(BlockPos basePos) implements CustomPacketPayload {
    public static final Type<ToggleIrisRedstonePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "toggle_iris_redstone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleIrisRedstonePayload> STREAM_CODEC = CustomPacketPayload.codec(
            ToggleIrisRedstonePayload::write,
            ToggleIrisRedstonePayload::read);

    private static ToggleIrisRedstonePayload read(RegistryFriendlyByteBuf buffer) {
        return new ToggleIrisRedstonePayload(buffer.readBlockPos());
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(basePos);
    }

    public static void handle(ToggleIrisRedstonePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player
                && player.level().getBlockEntity(payload.basePos()) instanceof StargateBaseBlockEntity base) {
            base.toggleIrisRedstone();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
