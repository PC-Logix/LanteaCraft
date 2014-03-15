package pcl.common.api.energy;

import net.minecraft.item.ItemStack;

public interface IItemEnergyStore {

	/**
	 * Get the maximum energy this container may hold.
	 * 
	 * @return The maximum energy this container may hold.
	 */
	public abstract double getMaximumEnergy();

	/**
	 * Get the maximum energy this container may transfer in any
	 * {@link IItemEnergyStore#receiveEnergy(ItemStack, double, boolean)} or
	 * {@link IItemEnergyStore#equals(Object)} request.
	 * 
	 * @return The maximum energy this container may transfer in any request.
	 */
	public abstract double getMaximumIOPayload();

	/**
	 * Receive a quantity of energy from a foreign provider.
	 * 
	 * @param itemStack
	 *            The item stack to perform the operation on.
	 * @param quantity
	 *            The quantity of energy to receive.
	 * @param isSimulated
	 *            If this request is a simulation.
	 * @return The actual energy stored as a result of this request.
	 */
	public abstract double receiveEnergy(ItemStack itemStack, double quantity, boolean isSimulated);

	/**
	 * Emit a quantity of energy to a foreign provider.
	 * 
	 * @param itemStack
	 *            The item stack to perform the operation on.
	 * @param quantity
	 *            The quantity of energy to emit.
	 * @param isSimulated
	 *            If this request is a simulation.
	 * @return The actual energy emitted as a result of this request.
	 */
	public abstract double extractEnergy(ItemStack itemStack, double quantity, boolean isSimulated);

	/**
	 * Get the energy stored in a container.
	 * 
	 * @param itemStack
	 *            The item stack to perform the operation on.
	 * @return The energy stored in a container.
	 */
	public abstract double getEnergyStored(ItemStack itemStack);

	/**
	 * Set the energy stored in a container.
	 * 
	 * @param itemStack
	 *            The item stack to perform this operation on.
	 * @param value
	 *            The energy value to set.
	 */
	public abstract void setEnergyStored(ItemStack itemStack, double value);

}
