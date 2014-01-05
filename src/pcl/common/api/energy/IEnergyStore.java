package pcl.common.api.energy;

import net.minecraft.nbt.NBTTagCompound;

public interface IEnergyStore {

	public abstract double receiveEnergy(double quantity, boolean isSimulated);

	public abstract double extractEnergy(double quantity, boolean isSimulated);

	public abstract double getEnergyStored();

	public abstract double getMaxEnergyStored();

	public abstract void saveEnergyStore(NBTTagCompound compound);

	public abstract void loadEnergyStore(NBTTagCompound compound);
}
