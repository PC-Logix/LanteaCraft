//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring tile entity
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.tileentity;

import gcewing.sg.base.BaseTileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class SGRingTE extends BaseTileEntity {

	public boolean isMerged;
	public int baseX, baseY, baseZ;

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
