package pcl.lc.base;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import pcl.lc.util.EnumUnits;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;

@InterfaceList({ @Interface(iface = "ic2.api.energy.tile.IEnergyAcceptor", modid = "IC2"),
		@Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
		@Interface(iface = "buildcraft.api.power.IPowerEmitter", modid = "BuildCraft|Core"),
		@Interface(iface = "buildcraft.api.power.IPowerReceptor", modid = "BuildCraft|Core") })
public abstract class PoweredTileEntity extends GenericTileEntity implements IEnergyAcceptor, IEnergySource,
		IPowerEmitter, IPowerReceptor {

	Object receiverBuffer;

	public PoweredTileEntity() {
		if (canReceiveEnergy() && Loader.isModLoaded("BuildCraft")) {
			setupExportBC();
		}
	}

	/**
	 * Determines if this TileEntity is capable of receiving energy from
	 * external sources. This should be used only to indicate if energy can be
	 * received at any point in time; not if it can currently perform energy
	 * exchanges.
	 * 
	 * @return If this TileEntity is capable of receiving energy from external
	 *         sources.
	 */
	public abstract boolean canReceiveEnergy();

	/**
	 * Determines if this TileEntity is capable of exporting energy. This should
	 * be used only to indicate if energy can be exported at any point in time;
	 * not if it can currently perform energy exchanges.
	 * 
	 * @return If this TileEntity is capable of exporting energy to external
	 *         sources.
	 */
	public abstract boolean canExportEnergy();

	/**
	 * Gets the maximum naquadah energy unit(s) which can be received in any one
	 * tick.
	 * 
	 * @return The maximum naquadah energy unit(s) which can be received in any
	 *         one tick.
	 */
	public abstract double getMaximumReceiveEnergy();

	/**
	 * Gets the absolute maximum naquadah energy unit(s) which can be exported
	 * in any one tick.
	 * 
	 * @return The maximum naquadah energy unit(s) which can be exported in any
	 *         one tick.
	 */
	public abstract double getMaximumExportEnergy();

	/**
	 * Gets the currently available maxima value of naquadah energy units(s)
	 * which can be exported this moment. This should not exceed the value of
	 * {@link PoweredTileEntity#getMaximumExportEnergy()} at any time.
	 * 
	 * @return The maxima value of naquadah energy units(s) which can be
	 *         exported this moment.
	 */
	public abstract double getAvailableExportEnergy();

	/**
	 * Called when a power provider issues enough energy to this TileEntity to
	 * trigger a 'naquadah energy' unit update. The TileEntity can choose to do
	 * as it wishes with this 'naquadah unit' of energy. Note that this value
	 * may not represent a whole unit of energy.
	 * 
	 * @param units
	 *            The number of, or fraction of, naquadah energy units being
	 *            received from any remote source.
	 */
	public abstract void receiveEnergy(double units);

	/**
	 * Called when a power receptor requests energy from this TileEntity. The
	 * TileEntity can choose to satisfy the entire number of units requested, or
	 * just a partial quantity.
	 * 
	 * @param units
	 *            The number of units of naquadah energy being requested.
	 * @return The number of units of nauqadah energy which is actually being
	 *         delegated to this request; this value may not necessarily reflect
	 *         the requested quantity.
	 */
	public abstract double exportEnergy(double units);

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (canExportEnergy() && !worldObj.isRemote && receiverBuffer != null)
			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				emitEnergy(direction);
	}

	private void setupExportBC() {
		PowerHandler b = new PowerHandler(this, Type.MACHINE);
		b.configure(0.0f, 20.0f, 5.0f, 1000.0f);
		b.configurePowerPerdition(0, 0);
		receiverBuffer = b;
	}

	@Override
	@Method(modid = "IC2")
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return canExportEnergy();
	}

	@Override
	@Method(modid = "BuildCraft|Core")
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return canExportEnergy();
	}

	@Override
	@Method(modid = "BuildCraft|Core")
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return ((PowerHandler) receiverBuffer).getPowerReceiver();
	}

	@Override
	@Method(modid = "BuildCraft|Core")
	public void doWork(PowerHandler workProvider) {
		float quantity = workProvider.useEnergy(0, 100.0f, true);
		double naqQuantity = EnumUnits.convertToNaquadahUnit(EnumUnits.MinecraftJoules, quantity);
		receiveEnergy(quantity);
	}

	@Override
	@Method(modid = "BuildCraft|Core")
	public World getWorld() {
		return worldObj;
	}

	@Override
	@Method(modid = "IC2")
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return canReceiveEnergy();
	}

	@Override
	@Method(modid = "IC2")
	public double getOfferedEnergy() {
		double naqAvailable = getAvailableExportEnergy();
		return EnumUnits.convertFromNaquadahUnit(EnumUnits.EnergyUnit, naqAvailable);
	}

	@Override
	@Method(modid = "IC2")
	public void drawEnergy(double amount) {
		double naqQuantity = EnumUnits.convertToNaquadahUnit(EnumUnits.NaquadahUnit, amount);
		exportEnergy(naqQuantity);
	}

	/**
	 * Attempts to emit BC energy to nearby BC IPowerReceptor objects.
	 * 
	 * @param direction
	 *            The direction to emit.
	 */
	private void emitEnergy(ForgeDirection direction) {
		int x = direction.offsetX + xCoord;
		int y = direction.offsetY + yCoord;
		int z = direction.offsetZ + zCoord;
		TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof IPowerReceptor
				&& ((IPowerReceptor) tile).getPowerReceiver(direction.getOpposite()) != null) {
			PowerReceiver receptor = ((IPowerReceptor) tile).getPowerReceiver(direction.getOpposite());

			float maxQuantity = receptor.getMaxEnergyReceived();
			float maxAvailQuantity = (float) EnumUnits.convertFromNaquadahUnit(EnumUnits.MinecraftJoules,
					getAvailableExportEnergy());

			float send = Math.min(maxQuantity, maxAvailQuantity);
			float quantityUsed = receptor.receiveEnergy(PowerHandler.Type.MACHINE, send, direction.getOpposite());
			exportEnergy(EnumUnits.convertToNaquadahUnit(EnumUnits.MinecraftJoules, quantityUsed));
		}
	}

}
