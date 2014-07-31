package pcl.lc.module.stargate.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import pcl.lc.LanteaCraft;
import pcl.lc.base.TileManaged;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.module.stargate.TransporterRingMultiblock;
import pcl.lc.module.stargate.TransporterRingPart;
import pcl.lc.util.WorldLocation;

public class TileTransporterRing extends TileManaged {

	private TransporterRingMultiblock multiblock = new TransporterRingMultiblock(this);
	private TransporterRingPart part = new TransporterRingPart(this);
	private boolean initialized = false;

	public TileTransporterRing() {
	}

	public TransporterRingMultiblock getAsStructure() {
		return multiblock;
	}

	public TransporterRingPart getAsPart() {
		return part;
	}

	public boolean isHost() {
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 0;
	}

	@Override
	public void think() {
		if (!isHost())
			return;
		if (!initialized) {
			initialized = true;
			multiblock.invalidate();
		}
		if (multiblock != null)
			multiblock.tick();
	}

	public void getStateFromPacket(ModPacket packet) {
		if (isHost())
			if (multiblock != null)
				multiblock.unpack(packet);

	}

	public ModPacket getPacketFromState() {
		if (isHost())
			if (multiblock != null)
				return multiblock.pack();
		return null;
	}

	public void hostBlockDestroyed() {
		if (!worldObj.isRemote) {
			if (getAsStructure() != null)
				getAsStructure().disband();
			if (part != null)
				part.devalidateHostMultiblock();
		}
			
	}

	@Override
	public Packet getDescriptionPacket() {
		ModPacket packet = getPacketFromState();
		if (packet != null)
			LanteaCraft.getNetPipeline().sendToAllAround(packet, new WorldLocation(this), 128.0d);
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 4, yCoord, zCoord - 4, xCoord + 4, yCoord + 7, zCoord + 4);
	}

	@Override
	public void thinkPacket(ModPacket packetOf, EntityPlayer player) {
		if (packetOf instanceof StandardModPacket) {
			StandardModPacket packet = (StandardModPacket) packetOf;
			if (packet.getType().equals("LanteaPacket.MultiblockUpdate"))
				getStateFromPacket(packet);
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub

	}

}
