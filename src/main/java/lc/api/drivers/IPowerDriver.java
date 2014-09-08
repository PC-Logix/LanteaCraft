package lc.api.drivers;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerDriver extends IDriver {

	public boolean canInterface(ForgeDirection direction);

	public double getStoredEnergy();
	
	public double getEnergyCapacity();

	public double acceptEnergy(double quantity, boolean simulated);

	public double pullEnergy(double quantity, boolean simulated);
	
	public World getWorld();

}
