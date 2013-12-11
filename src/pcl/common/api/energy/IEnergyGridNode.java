package pcl.common.api.energy;

import pcl.common.energy.EnergyGrid;

public interface IEnergyGridNode {

	public abstract void setGrid(EnergyGrid grid);

	public abstract void doesTick(boolean tick);

}
