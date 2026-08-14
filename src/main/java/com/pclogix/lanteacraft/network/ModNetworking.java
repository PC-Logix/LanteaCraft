package com.pclogix.lanteacraft.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private static final String NETWORK_VERSION = "1";

    private ModNetworking() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(DialStargatePayload.TYPE, DialStargatePayload.STREAM_CODEC, DialStargatePayload::handle);
        registrar.playToServer(InterServerTransferHandshakePayload.TYPE, InterServerTransferHandshakePayload.STREAM_CODEC, InterServerTransferHandshakePayload::handle);
        registrar.playToServer(ToggleIrisPayload.TYPE, ToggleIrisPayload.STREAM_CODEC, ToggleIrisPayload::handle);
        registrar.playToServer(ToggleIrisRedstonePayload.TYPE, ToggleIrisRedstonePayload.STREAM_CODEC, ToggleIrisRedstonePayload::handle);
        registrar.playToServer(ToggleDhdIrisPayload.TYPE, ToggleDhdIrisPayload.STREAM_CODEC, ToggleDhdIrisPayload::handle);
    }

    public static void registerClient(IEventBus modEventBus, IPayloadHandler<InterServerTransferPayload> handler) {
        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> event.registrar(NETWORK_VERSION)
                .playToClient(InterServerTransferPayload.TYPE, InterServerTransferPayload.STREAM_CODEC, handler));
    }
}
