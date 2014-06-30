package pcl.lc.base;

import java.lang.reflect.Constructor;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.Level;

import pcl.common.asm.RuntimeAnnotation.RuntimeInterface;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumUnits;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import cpw.mods.fml.common.Loader;

/**
 * Holy wrappers, Batman. Factory free wrapper for TileEntity objects which
 * import or export power/energy/'magicz' to other mods through strange and
 * undocumented API's. If you've come here to tinker, turn back now -- you can
 * change all configurable options in your class which extends this.
 * 
 * Did I lose what little sanity I had when writing this? Yes, yes I did.
 * 
 * @author AfterLifeLochie
 */
public abstract class PoweredTileEntity extends GenericTileEntity {

	/**
	 * Incoming power buffer, particularly only for BuildCraft.
	 */
	protected Object receiverBuffer;
	protected boolean addedToEnergyNet;

	/**
	 * Constructs the PoweredTileEntity instance. It's crucial that any
	 * overridden implementations explicitly invoke this constructor; failure to
	 * do so will result in crashes in APIs which are not LanteaCraft scope.
	 * 
	 * We're forced to setup BuildCraft export and import rules all the time -
	 * regardless of if we intend on exporting and/or importing BuildCraft
	 * energy, because returning null does really really weird things even if
	 * we're not actually importing any sort of energy - BC will go so far as to
	 * throw NPE's.
	 */
	public PoweredTileEntity() {
		// If BuildCraft is loaded, we must create export and import rules.
		if (Loader.isModLoaded("BuildCraft|Core")) {
			PowerHandler b = new PowerHandler((IPowerReceptor) this, Type.MACHINE);

			/*
			 * TODO: It's exceptionally likely that, due to the way BC
			 * calculates power sinks, this could cause strangeness with respect
			 * to engines and power generation sources. It might be wise to
			 * clamp a maximum prescribed value, if there is one in the BC API.
			 */
			float maxRecieveQuantity = (float) EnumUnits.convertFromNaquadahUnit(EnumUnits.MinecraftJoules,
					getMaximumReceiveEnergy());
			b.configure(0.0f, maxRecieveQuantity, (maxRecieveQuantity == 0.0f) ? 99999.0f : 1.0f,
					(maxRecieveQuantity == 0.0f) ? 0.0f : 1000.0f);
			b.configurePowerPerdition(0, 0);
			receiverBuffer = b;
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

	/**
	 * Called to establish if a connector of the unit type specified can connect
	 * to the specified side of this host.
	 * 
	 * @param typeof
	 *            The type of the connector, based on units.
	 * @param direction
	 *            The side of the host.
	 * @return If this host permits a connection of the specified type.
	 */
	public abstract boolean canEnergyFormatConnectToSide(EnumUnits typeof, ForgeDirection direction);

	// Don't look beyond here unless you want your eyes severely violated.

	/**
	 * Allows the entity to update its state.
	 * 
	 * It is critical that in overridden implementation that this method be
	 * invoked using super. Failure to do so prevents the exporting of energy to
	 * nearby BuildCraft systems.
	 */
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (canExportEnergy() && !worldObj.isRemote && receiverBuffer != null)
			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
				emitEnergy(direction);
	}

	/**
	 * Determine if this host emits IC2 EU to a forge direction and a receiver.
	 * Return a {@link PoweredTileEntity#canExportEnergy()} result.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergyEmitter")
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return canEnergyFormatConnectToSide(EnumUnits.EnergyUnit, direction) && canExportEnergy();
	}

	/**
	 * Determine if this host emits BuildCraft MJ to a forge direction. Return a
	 * {@link PoweredTileEntity#canExportEnergy()} result.
	 */
	@RuntimeInterface(modid = "BuildCraft|Core", clazz = "buildcraft.api.power.IPowerEmitter")
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return canEnergyFormatConnectToSide(EnumUnits.MinecraftJoules, side) && canExportEnergy();
	}

	/**
	 * Return the BuildCraft power receiver object. This must never be null, nor
	 * never return null.
	 * 
	 * @see {@link PoweredTileEntity#PoweredTileEntity()} for more reasons on
	 *      why BuildCraft's API hurts souls.
	 */
	@RuntimeInterface(modid = "BuildCraft|Core", clazz = "buildcraft.api.power.IPowerReceptor")
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return ((PowerHandler) receiverBuffer).getPowerReceiver();
	}

	/**
	 * BuildCraft arbitrarily returns this value when it decides the stored
	 * energy exceeds the activation energy, as setup in the configuration. It
	 * may even decide to not call this method at all. I don't know.
	 */
	@RuntimeInterface(modid = "BuildCraft|Core", clazz = "buildcraft.api.power.IPowerReceptor")
	public void doWork(PowerHandler workProvider) {
		float quantity = (float) workProvider.useEnergy(0, 100.0f, true);
		double naqQuantity = EnumUnits.convertToNaquadahUnit(EnumUnits.MinecraftJoules, quantity);
		receiveEnergy(naqQuantity);
	}

	/**
	 * BuildCraft eat world?
	 */
	@RuntimeInterface(modid = "BuildCraft|Core", clazz = "buildcraft.api.power.IPowerReceptor")
	public World getWorld() {
		return worldObj;
	}

	/**
	 * Determine if this host imports IC2 EU from a forge direction and a
	 * receiver. Return a {@link PoweredTileEntity#canReceiveEnergy()} result.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergyAcceptor")
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return canEnergyFormatConnectToSide(EnumUnits.EnergyUnit, direction) && canReceiveEnergy();
	}

	/**
	 * Determine the IC2 EU quantity of energy actively available for export.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergySource")
	public double getOfferedEnergy() {
		double naqAvailable = getAvailableExportEnergy();
		return EnumUnits.convertFromNaquadahUnit(EnumUnits.EnergyUnit, naqAvailable);
	}

	/**
	 * IC2 calls this method when exporting a quantity of energy, amount,
	 * specified. The maximum value should theoretically be no more than that of
	 * {@link PoweredTileEntity#getAvailableExportEnergy()}, but the API doesn't
	 * make this clear either. We're going to assume all values follow the rule:
	 * 
	 * (EU) getAvailableExportEnergy() > amount > 0;
	 * 
	 * If not, wellp.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergySource")
	public void drawEnergy(double amount) {
		double naqQuantity = EnumUnits.convertToNaquadahUnit(EnumUnits.EnergyUnit, amount);
		exportEnergy(naqQuantity);
	}

	/**
	 * Determine if this IC2 accepts energy, and if so, how much energy to
	 * accept.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergySink")
	public double demandedEnergyUnits() {
		if (!canReceiveEnergy())
			return 0;
		return EnumUnits.convertFromNaquadahUnit(EnumUnits.EnergyUnit, getMaximumReceiveEnergy());
	}

	/**
	 * Transfer energy from IC2 to this sink.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergySink")
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		if (!canReceiveEnergy())
			return 0;
		double quantity = EnumUnits.convertToNaquadahUnit(EnumUnits.EnergyUnit, amount);
		receiveEnergy(quantity);
		return amount;
	}

	/**
	 * Get the maximum safe input IC2 quantity for this host.
	 */
	@RuntimeInterface(modid = "IC2", clazz = "ic2.api.energy.tile.IEnergySink")
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}

	protected void postIC2Update(boolean actionIsLoad) {
		LanteaCraft.getLogger().log(Level.INFO, String.format("Sending IC2 state."));
		try {
			Class<?> clazz_ic2energytile = Class.forName("ic2.api.energy.tile.IEnergyTile", false, getClass()
					.getClassLoader());

			if (actionIsLoad) {
				Class<? extends WorldEvent> clazz_loadevent = (Class<? extends WorldEvent>) Class.forName(
						"ic2.api.energy.event.EnergyTileLoadEvent", false, getClass().getClassLoader());
				Constructor<? extends WorldEvent> c_loadevent = clazz_loadevent
						.getConstructor(new Class<?>[] { clazz_ic2energytile });
				WorldEvent event = c_loadevent.newInstance(clazz_ic2energytile.cast(this));
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Sending IC2 event class %s", event.getClass().getName()));
				MinecraftForge.EVENT_BUS.post(event);
				addedToEnergyNet = true;
			} else {
				Class<? extends WorldEvent> clazz_loadevent = (Class<? extends WorldEvent>) Class.forName(
						"ic2.api.energy.event.EnergyTileUnloadEvent", false, getClass().getClassLoader());
				Constructor<? extends WorldEvent> c_unloadevent = clazz_loadevent
						.getConstructor(new Class<?>[] { clazz_ic2energytile });
				WorldEvent event = c_unloadevent.newInstance(clazz_ic2energytile.cast(this));
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Sending IC2 event class %s", event.getClass().getName()));
				MinecraftForge.EVENT_BUS.post(event);
				addedToEnergyNet = false;
			}
		} catch (Throwable t) {
			LanteaCraft.getLogger().log(Level.WARN, "Could not push IC2 energy event.", t);
		}
	}

	/**
	 * Attempts to emit BC energy to nearby BC IPowerReceptor objects. Holy
	 * crap, this is bad. I couldn't find any BC API method to do this, so do we
	 * just `gung-ho and do shit`, or is there some other fancy way of doing
	 * this?
	 * 
	 * @param direction
	 *            The direction to try and emit to.
	 */
	private void emitEnergy(ForgeDirection direction) {
		int x = direction.offsetX + xCoord;
		int y = direction.offsetY + yCoord;
		int z = direction.offsetZ + zCoord;
		TileEntity tile = worldObj.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IPowerReceptor
				&& ((IPowerReceptor) tile).getPowerReceiver(direction.getOpposite()) != null) {
			PowerReceiver receptor = ((IPowerReceptor) tile).getPowerReceiver(direction.getOpposite());

			float maxQuantity = (float) receptor.getMaxEnergyReceived();
			float maxAvailQuantity = (float) EnumUnits.convertFromNaquadahUnit(EnumUnits.MinecraftJoules,
					getAvailableExportEnergy());

			float send = Math.min(maxQuantity, maxAvailQuantity);
			float quantityUsed = (float) receptor.receiveEnergy(PowerHandler.Type.MACHINE, send,
					direction.getOpposite());
			exportEnergy(EnumUnits.convertToNaquadahUnit(EnumUnits.MinecraftJoules, quantityUsed));
		}
	}

	@RuntimeInterface(modid = "CoFHCore", clazz = "cofh.api.energy.IEnergyHandler")
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (!canReceiveEnergy())
			return 0;
		int quantity = (int) Math.floor(EnumUnits.convertToNaquadahUnit(EnumUnits.RedstoneFlux, maxReceive));
		if (!simulate)
			receiveEnergy(quantity);
		return quantity;
	}

	@RuntimeInterface(modid = "CoFHCore", clazz = "cofh.api.energy.IEnergyHandler")
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (!canExportEnergy())
			return 0;
		int maxAvailQuantity = (int) EnumUnits.convertFromNaquadahUnit(EnumUnits.RedstoneFlux,
				getAvailableExportEnergy());
		int send = Math.min(maxExtract, maxAvailQuantity);
		if (!simulate)
			exportEnergy(send);
		return send;
	}

	@RuntimeInterface(modid = "CoFHCore", clazz = "cofh.api.energy.IEnergyHandler")
	public boolean canInterface(ForgeDirection from) {
		return true;
	}

	@RuntimeInterface(modid = "CoFHCore", clazz = "cofh.api.energy.IEnergyHandler")
	public int getEnergyStored(ForgeDirection from) {
		return (int) EnumUnits.convertFromNaquadahUnit(EnumUnits.RedstoneFlux, getAvailableExportEnergy());
	}

	@RuntimeInterface(modid = "CoFHCore", clazz = "cofh.api.energy.IEnergyHandler")
	public int getMaxEnergyStored(ForgeDirection from) {
		return (int) EnumUnits.convertFromNaquadahUnit(EnumUnits.RedstoneFlux, getMaximumExportEnergy());
	}
}
