package pcl.common.api.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;

public interface IEnergyHandler {

	/**
	 * Receive a quantity of energy specified from the direction provided. Return the actual
	 * quantity of energy consumed by this {@link IEnergyHandler}
	 * 
	 * @param direction
	 *            The direction of the incoming energy.
	 * @param quantity
	 *            The quantity of energy being offered.
	 * @param isSimulated
	 *            If this is a simulated request; simulated requests are not to be completed by
	 *            the host as real energy transfers.
	 * @return The quantity of energy accepted by this host. May be the entire quantity
	 *         specified, part of the quantity specified, or zero if no energy can be accepted.
	 */
	public abstract double receiveEnergy(ForgeDirection direction, double quantity, boolean isSimulated);

	/**
	 * Extract a quantity of energy specified to the direction provided. Return the actual
	 * quantity of energy exported by this {@link IEnergyHandler}
	 * 
	 * @param direction
	 *            The direction of the outgoing energy.
	 * @param quantity
	 *            The quantity of energy being requested.
	 * @param isSimulated
	 *            If this is a simulated request; simulated requests are not to be completed by
	 *            the host as real energy transfers.
	 * @return The quantity of energy extracted by this host. May be the entire quantity
	 *         requested, part of the quantity requested, or zero if no energy can be
	 *         extracted.
	 */
	public abstract double extractEnergy(ForgeDirection direction, double quantity, boolean isSimulated);

	/**
	 * Determines if this host can connect to energy transfers on a given direction.
	 * 
	 * @param direction
	 *            The direction to test.
	 * @return If this host can connect to neighbor {@link IEnergyHandler} instances.
	 */
	public abstract boolean canConnect(ForgeDirection direction);

	/**
	 * Get the currently stored or buffered energy which can be extracted by neighbor energy
	 * handlers or sinks.
	 * 
	 * @param direction
	 *            The direction to test.
	 * @return The quantity of energy which can be extracted by neighbors on the given side
	 */
	public abstract double getAvailableEnergy(ForgeDirection direction);

	/**
	 * Get the current maximum energy which can be accepted on the given side.
	 * 
	 * @param direction
	 *            The direction to test.
	 * @return The quantity of energy which this side can receive.
	 */
	public abstract double getMaxReceiveEnergy(ForgeDirection direction);

	/**
	 * Saves the energy handler to a {@link NBTTagCompound} compound for saving inside a
	 * tile-entity NBT structure.
	 * 
	 * @param compound
	 *            The compound to save to.
	 */
	public abstract void saveEnergyHandler(NBTTagCompound compound);

	/**
	 * Loads the energy handler from a {@link NBTTagCompound} compound for loading from a
	 * tile-entity NBT structure.
	 * 
	 * @param compound
	 *            The compound to load from.
	 */
	public abstract void loadEnergyHandler(NBTTagCompound compound);
}
