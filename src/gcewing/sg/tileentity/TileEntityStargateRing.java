package gcewing.sg.tileentity;

import java.util.HashMap;
import java.util.Map;

import gcewing.sg.SGCraft;
import gcewing.sg.base.GenericTileEntity;
import gcewing.sg.multiblock.StargatePart;
import gcewing.sg.network.SGCraftPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;

public class TileEntityStargateRing extends GenericTileEntity {

	public boolean isMerged;
	public int baseX, baseY, baseZ;

	private StargatePart thisPart = new StargatePart(this);

	public StargatePart getAsPart() {
		return thisPart;
	}

	@Override
	public boolean canUpdate() {
		return (thisPart.getType() == null && worldObj != null && !worldObj.isRemote);
	}

	@Override
	public void updateEntity() {
		if (thisPart.getType() == null)
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
		if (thisPart.getType() == null) {
			int ord = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 0x1);
			thisPart.setType((ord == 0) ? "partStargateBlock" : "partStargateChevron");
		}
		thisPart.devalidateHostMultiblock();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// System.out.printf("SGRingTE.readFromNBT\n");
		super.readFromNBT(nbt);
		isMerged = nbt.getBoolean("isMerged");
		baseX = nbt.getInteger("baseX");
		baseY = nbt.getInteger("baseY");
		baseZ = nbt.getInteger("baseZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		// System.out.printf("SGRingTE.writeToNBT\n");
		super.writeToNBT(nbt);
		nbt.setBoolean("isMerged", isMerged);
		nbt.setInteger("baseX", baseX);
		nbt.setInteger("baseY", baseY);
		nbt.setInteger("baseZ", baseZ);
	}

}
