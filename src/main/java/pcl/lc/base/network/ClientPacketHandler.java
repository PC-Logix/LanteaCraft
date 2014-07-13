package pcl.lc.base.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraftClientProxy;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.base.render.gfx.EffectBeam;
import pcl.lc.util.WorldLocation;

public class ClientPacketHandler {

	private PacketLogger logger;

	public ClientPacketHandler(PacketLogger logger) {
		this.logger = logger;
	}

	public void handlePacket(ModPacket packet, EntityPlayer player) {
		if (logger != null)
			logger.logPacket(packet);
		WorldLocation target = packet.getOriginLocation();
		if (target == null)
			LanteaCraft.getLogger().log(Level.WARN,
					String.format("ModPacket type %s sent without OriginLocation, much bad!", packet.getClass()));
		if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().theWorld.provider == null) 
			return;
		int currentWorld = Minecraft.getMinecraft().theWorld.provider.dimensionId;
		if (currentWorld == target.dimension) {
			World world = Minecraft.getMinecraft().theWorld;
			if (packet.getType().equals("LanteaPacket.EntityFX")) {
				StandardModPacket payload = (StandardModPacket) packet;
				String name = (String) payload.getValue("FXType");
				EntityFX effect = null;
				if (name.equals("EffectBeam"))
					effect = EffectBeam.fromPacket(payload);
				if (effect != null)
					((LanteaCraftClientProxy) LanteaCraft.getProxy()).spawnEffect(effect);
			} else {
				TileEntity tile = world.getTileEntity(target.x, target.y, target.z);
				if (tile instanceof IPacketHandler) {
					IPacketHandler handler = (IPacketHandler) tile;
					handler.handlePacket(packet, player);
				}
			}
		}

	}

}
