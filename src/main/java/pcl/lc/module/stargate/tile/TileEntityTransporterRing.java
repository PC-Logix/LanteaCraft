package pcl.lc.module.stargate.tile;

import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericTileEntity;
import pcl.lc.multiblock.TransporterRingMultiblock;
import pcl.lc.multiblock.TransporterRingPart;

public class TileEntityTransporterRing extends GenericTileEntity implements IPacketHandler {

	private TransporterRingMultiblock multiblock;
	private boolean isHostBlock = false;

	public TileEntityTransporterRing() {
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
			LanteaCraft.getNetPipeline().sendToAll(packet);
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
