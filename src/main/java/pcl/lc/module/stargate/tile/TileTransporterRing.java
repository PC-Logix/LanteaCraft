package pcl.lc.module.stargate.tile;

import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.base.TileManaged;
import pcl.lc.base.network.IPacketHandler;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.module.stargate.TransporterRingMultiblock;
import pcl.lc.module.stargate.TransporterRingPart;

public class TileTransporterRing extends TileManaged implements IPacketHandler {

	private TransporterRingMultiblock multiblock;
	private boolean isHostBlock = false;

	public TileTransporterRing() {
	}

	public boolean isHost() {
		return isHostBlock;
	}

	public void setHost(boolean b) {
		isHostBlock = b;
	}

	public TransporterRingMultiblock getAsStructure() {
		return multiblock;
	}

	@Override
	public void updateEntity() {
		if (!isHost())
			return;
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

	@Override
	public Packet getDescriptionPacket() {
		ModPacket packet = getPacketFromState();
		if (packet != null)
			LanteaCraft.getNetPipeline().sendToAllAround(packet, new WorldLocation(this), 128.0d);
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 4, yCoord, zCoord - 4, xCoord + 4, yCoord + 7, zCoord + 4);
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		getStateFromPacket(packetOf);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public TransporterRingPart getAsPart() {
		// TODO Auto-generated method stub
		return null;
	}

}
