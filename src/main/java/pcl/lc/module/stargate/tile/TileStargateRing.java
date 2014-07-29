package pcl.lc.module.stargate.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import pcl.lc.LanteaCraft;
import pcl.lc.base.TileManaged;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.module.stargate.StargatePart;
import pcl.lc.util.WorldLocation;

public class TileStargateRing extends TileManaged {
	private StargatePart part = new StargatePart(this);

	@Override
	public Packet getDescriptionPacket() {
		if (!worldObj.isRemote){ 
			ModPacket packet = part.pack();
			LanteaCraft.getNetPipeline().sendToAllAround(packet, new WorldLocation(this), 128.0d);
		} else {
			StandardModPacket req = new StandardModPacket(new WorldLocation(this));
			req.setIsForServer(true);
			req.setType("LanteaPacket.MultiblockPoll");
			LanteaCraft.getNetPipeline().sendToServer(req);
		}
		return null;
	}

	@Override
	public void thinkPacket(ModPacket packetOf, EntityPlayer player) {
		part.unpack(packetOf);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		// TODO: Load chunks from NBT
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		// TODO: Save chunks to NBT
	}

	public StargatePart getAsPart() {
		return part;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void think() {
		part.tick();
		if (part.getType() == null)
			flagDirty();
	}

	public void hostBlockPlaced() {
		if (!worldObj.isRemote)
			flagDirty();
	}

	public void hostBlockDestroyed() {
		if (!worldObj.isRemote)
			flagDirty();
	}

	public void flagDirty() {
		if (!worldObj.isRemote) {
			if (part.getType() == null) {
				int ord = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 0x1);
				part.setType((ord == 0) ? "partStargateBlock" : "partStargateChevron");
			}
			part.devalidateHostMultiblock();
		}
	}

	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub

	}

}
