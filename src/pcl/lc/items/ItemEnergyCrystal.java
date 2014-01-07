package pcl.lc.items;

import pcl.common.api.energy.IEnergyStore;
import pcl.common.api.energy.IItemEnergyStore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemEnergyCrystal extends Item implements IItemEnergyStore {

	public ItemEnergyCrystal(int id) {
		super(id);
		setMaxStackSize(1);
	}

	@Override
	public double getMaximumEnergy() {
		return 32.0d;
	}

	@Override
	public double receiveEnergy(ItemStack itemStack, double quantity, boolean isSimulated) {
		double actualPayload = Math.min(getMaximumEnergy() - getEnergyStored(itemStack), quantity);
		if (!isSimulated) 
			setEnergyStored(itemStack, getEnergyStored(itemStack) + actualPayload);
		return actualPayload;
	}

	@Override
	public double extractEnergy(ItemStack itemStack, double quantity, boolean isSimulated) {
		double actualPayload = Math.min(getEnergyStored(itemStack), quantity);
		if (!isSimulated) 
			setEnergyStored(itemStack, getEnergyStored(itemStack) - actualPayload);
		return actualPayload;
	}

	@Override
	public double getEnergyStored(ItemStack itemStack) {
		return itemStack.stackTagCompound.getDouble("stored-energy");
	}

	@Override
	public void setEnergyStored(ItemStack itemStack, double value) {
		itemStack.stackTagCompound.setDouble("stored-energy", value);
	}
}
