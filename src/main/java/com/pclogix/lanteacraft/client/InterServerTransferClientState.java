package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.network.InterServerTransferHandshakePayload;
import com.pclogix.lanteacraft.network.InterServerTransferPayload;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.neoforged.neoforge.network.PacketDistributor;

public final class InterServerTransferClientState {
    private static PendingTransfer pending;

    private InterServerTransferClientState() {
    }

    public static synchronized void receive(InterServerTransferPayload payload) {
        pending = new PendingTransfer(payload.host(), payload.port(), payload.token());
    }

    public static synchronized void onLoggingIn(Connection connection) {
        PendingTransfer transfer = pending;
        if (transfer == null) {
            return;
        }

        if (!matchesTarget(connection.getRemoteAddress(), transfer.host(), transfer.port())) {
            pending = null;
            return;
        }

        pending = null;
        PacketDistributor.sendToServer(new InterServerTransferHandshakePayload(transfer.token()));
    }

    private static boolean matchesTarget(SocketAddress remoteAddress, String expectedHost, int expectedPort) {
        if (!(remoteAddress instanceof InetSocketAddress address)) {
            return false;
        }

        if (address.getPort() != expectedPort) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getCurrentServer() == null) {
            return false;
        }

        String configuredHost = minecraft.getCurrentServer().ip.split(":", 2)[0];
        return expectedHost.equalsIgnoreCase(configuredHost)
                || expectedHost.equalsIgnoreCase(address.getHostString())
                || expectedHost.equalsIgnoreCase(address.getAddress().getHostAddress());
    }

    private record PendingTransfer(String host, int port, String token) {
    }
}
