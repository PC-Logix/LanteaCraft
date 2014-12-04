/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.drivers;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Contract interface for power drivers and drivees.
 *
 * @author AfterLifeLochie
 *
 */
public interface IPowerDriver extends IDriver {

	/**
	 * Ask if interfacing is allowed in a specified direction.
	 *
	 * @param direction
	 *            The direction
	 * @return If interfacing is allowed
	 */
	public boolean canInterface(ForgeDirection direction);

	/**
	 * @return The quantity of energy stored.
	 */
	public double getStoredEnergy();

	/**
	 * @return The maximum energy stored
	 */
	public double getEnergyCapacity();

	/**
	 * Accept a quantity of energy
	 *
	 * @param quantity
	 *            The quantity
	 * @param simulated
	 *            If the request is only a simulation
	 * @return The quantity of energy really stored
	 */
	public double acceptEnergy(double quantity, boolean simulated);

	/**
	 * Extract a quantity of energy
	 *
	 * @param quantity
	 *            The desired quantity
	 * @param simulated
	 *            If the request is only a simulation
	 * @return The quantity of energy really extracted
	 */
	public double pullEnergy(double quantity, boolean simulated);

	/**
	 * @return The world object of the drivee.
	 */
	public World getWorld();

}
