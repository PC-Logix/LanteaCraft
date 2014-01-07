package pcl.common.api.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemEnergyStore {
	
	public abstract double getMaximumEnergy();

	public abstract double receiveEnergy(ItemStack itemStack, double quantity, boolean isSimulated);

	public abstract double extractEnergy(ItemStack itemStack, double quantity, boolean isSimulated);

	public abstract double getEnergyStored(ItemStack itemStack);
	
	public abstract void setEnergyStored(ItemStack itemStack, double value);

}
