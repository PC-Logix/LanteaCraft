package pcl.lc.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.base.GenericTileEntity;
import pcl.common.helpers.ScanningHelper;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumRingPlatformState;
import pcl.lc.multiblock.TransporterRingMultiblock;

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
		multiblock.tick();
	}

	public void getStateFromPacket(ModPacket packet) {
		if (isHost())
			multiblock.unpack(packet);

	}

	public ModPacket getPacketFromState() {
		if (isHost())
			return multiblock.pack();
		return null;
	}

	@Override
	public Packet getDescriptionPacket() {
		LanteaCraft.getNetPipeline().sendToAll(getPacketFromState());
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

}
