package pcl.lc.base.energy;


public interface IEnergyGridNode extends IEnergyHandler {

	public abstract void setGrid(EnergyGrid grid);

	public abstract void doesTick(boolean tick);

}
