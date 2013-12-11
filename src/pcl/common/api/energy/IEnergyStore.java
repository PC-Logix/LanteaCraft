package pcl.common.api.energy;

public interface IEnergyStore {

	public abstract double receiveEnergy(double quantity, boolean isSimulated);

	public abstract double extractEnergy(double quantity, boolean isSimulated);

	public abstract double getEnergyStored();

	public abstract double getMaxEnergyStored();
}
