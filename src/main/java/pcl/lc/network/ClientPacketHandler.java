package pcl.lc.network;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraftClientProxy;
import pcl.lc.render.effects.EffectBeam;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler {

	public void handlePacket(ModPacket packet, Player player) {
		WorldLocation target = packet.getOriginLocation();
		if (target == null)
			LanteaCraft.getLogger().log(Level.WARNING, String.format("ModPacket type %s sent without OriginLocation, much bad!", packet.getClass()));
		int currentWorld = Minecraft.getMinecraft().theWorld.provider.dimensionId;
		if (currentWorld == target.dimension) {
			World world = Minecraft.getMinecraft().theWorld;
			if (packet.getType().equals("LanteaPacket.TileUpdate") || packet.getType().equals("TinyPacket")) {
				TileEntity tile = world.getBlockTileEntity(target.x, target.y, target.z);
				if (tile instanceof IPacketHandler) {
					IPacketHandler handler = (IPacketHandler) tile;
					handler.handlePacket(packet);
				} else
					LanteaCraft.getLogger().log(
							Level.WARNING,
							String.format("Dropping packet %s because the destination wasn't a handler.",
									packet.getType()));
			} else if (packet.getType().equals("LanteaPacket.EntityFX")) {
				StandardModPacket payload = (StandardModPacket) packet;
				String name = (String) payload.getValue("FXType");
				EntityFX effect = null;
				if (name.equals("EffectBeam"))
					effect = EffectBeam.fromPacket(payload);
				if (effect != null)
					((LanteaCraftClientProxy) LanteaCraft.getProxy()).spawnEffect(effect);
			}
		}

	}

}
