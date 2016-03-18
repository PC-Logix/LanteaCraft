package lc.common.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.common.LCLog;
import lc.common.crypto.DSAProvider;
import lc.common.crypto.KeyTrustRegistry;
import lc.common.network.packets.LCNetworkHandshake;
import lc.common.network.packets.LCServerToServerEnvelope;
import lc.common.network.packets.LCNetworkHandshake.HandshakeReason;
import lc.server.HintProviderServer;

public class LCNetworkPlayer {

	private final LCNetworkController controller;
	public int expectedEnvelopes;
	private LCPacketBuffer<LCServerToServerEnvelope> envelopes;

	public LCNetworkPlayer(LCNetworkController controller) {
		this.controller = controller;
	}

	public void initialize(EntityPlayerMP player) {
		LCLog.debug("Creating LanteaCraft Enhanced network player %s.", player);
	}

	public void shutdown(EntityPlayerMP player) {
		LCLog.debug("Terminating LanteaCraft Enhanced network player %s.", player);
	}

	public void sendHandshake(EntityPlayer player) {
		controller.getPreferredPipe().sendTo(new LCNetworkHandshake(HandshakeReason.SERVER_HELLO),
				(EntityPlayerMP) player);
	}

	public void handleHandshakePacket(EntityPlayer player, LCNetworkHandshake packet, Side target) {
		if (target == Side.CLIENT) {
			if (packet.reason == HandshakeReason.SERVER_HELLO) {
				LCLog.debug("Got HELLO handshake from LanteaCraft Enhanced server.");
				/* If we get HELLO, respond back, then send pending */
				ArrayList<LCServerToServerEnvelope> pending = controller.envelopeBuffer.packets();
				controller.getPreferredPipe().sendToServer(
						new LCNetworkHandshake(HandshakeReason.CLIENT_HELLO, pending.size()));
				for (int i = 0; i < pending.size(); i++)
					controller.getPreferredPipe().sendToServer(pending.get(i));
			} else
				LCLog.warn("Strange handshake packet on client from server: %s", packet.reason);
		} else {
			if (packet.reason == HandshakeReason.CLIENT_HELLO) {
				expectedEnvelopes = (Integer) packet.parameters[0];
				LCLog.debug("Got client HELLO response, expecting %s pending datagrams.", expectedEnvelopes);
			} else
				LCLog.warn("Strange handshake packet on server from client: %s", packet.reason);
		}
	}

	public void addEnvelopePacket(EntityPlayer player, LCServerToServerEnvelope envelope) {
		if (envelopes == null)
			envelopes = new LCPacketBuffer<LCServerToServerEnvelope>();
		envelopes.addPacket(envelope);
		if (envelopes.size() >= expectedEnvelopes) {
			try {
				KeyTrustRegistry registry = ((HintProviderServer) LCRuntime.runtime.hints()).getTrustChain();
				PublicKey[] allKeys = registry.contents();
				ArrayList<LCServerToServerEnvelope> blobs = envelopes.packets();
				PublicKey foundKey = null;
				for (PublicKey aKey : allKeys)
					if (DSAProvider.verify(blobs.get(0).signature(), blobs.get(0).data(), aKey))
						foundKey = aKey;
				if (foundKey == null) {
					envelopes.clear();
					controller.getPreferredPipe().sendTo(new LCNetworkHandshake(HandshakeReason.SECURITY_ERROR, 0x02),
							(EntityPlayerMP) player);
					throw new LCNetworkException("No public key found for signed payload. Dropping contents.");
				}
				for (LCServerToServerEnvelope blob : blobs) {
					if (!DSAProvider.verify(blob.signature(), blob.data(), foundKey)) {
						envelopes.clear();
						controller.getPreferredPipe().sendTo(
								new LCNetworkHandshake(HandshakeReason.SECURITY_ERROR, 0x03), (EntityPlayerMP) player);
						throw new LCNetworkException(
								"Found invalid siganture for signed data. Possibly tampered or invalid packets!");
					}
				}
				for (LCServerToServerEnvelope blob : blobs) {
					LCPacket packet = LCServerToServerEnvelope.unenvelope(blob);
					controller.injectPacket(Side.SERVER, packet, player);
				}
			} catch (IOException ex) {
				controller.getPreferredPipe().sendTo(new LCNetworkHandshake(HandshakeReason.NEGOTIATION_ERROR, 0x01),
						(EntityPlayerMP) player);
				LCLog.warn("Problem unpacking enveloped data.", ex);
			} catch (LCNetworkException ex) {
				controller.getPreferredPipe().sendTo(new LCNetworkHandshake(HandshakeReason.NEGOTIATION_ERROR, 0x01),
						(EntityPlayerMP) player);
				LCLog.warn("Problem handling enveloped packets.", ex);
			} catch (InvalidKeyException ex) {
				controller.getPreferredPipe().sendTo(new LCNetworkHandshake(HandshakeReason.NEGOTIATION_ERROR, 0x01),
						(EntityPlayerMP) player);
				LCLog.warn("Problem with local key storage.", ex);
			} catch (SignatureException ex) {
				controller.getPreferredPipe().sendTo(new LCNetworkHandshake(HandshakeReason.NEGOTIATION_ERROR, 0x01),
						(EntityPlayerMP) player);
				LCLog.fatal("Failed to handle cryptographic data.", ex);
			}
		}
	}

}
