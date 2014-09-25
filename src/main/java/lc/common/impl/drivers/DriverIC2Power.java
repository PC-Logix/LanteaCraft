package lc.common.impl.drivers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import lc.api.components.IntegrationType;
import lc.api.drivers.IPowerDriver;
import lc.api.drivers.DeviceDrivers;
import lc.common.LCLog;
import lc.common.base.LCTile;

/**
 * IC2 power driver.
 * 
 * @author AfterLifeLochie
 * 
 */
@DeviceDrivers.DriverProvider(type = IntegrationType.POWER)
public class DriverIC2Power implements IPowerDriver, IEnergyAcceptor, IEnergyEmitter, IEnergySink, IEnergySource {

	boolean driverIC2Power_onEnergyNet;

	@Override
	public double acceptEnergy(double quantity, boolean simulated) {
		// Auto-generated method stub
		return 0.0d;
	}

	@Override
	public double pullEnergy(double quantity, boolean simulated) {
		// Auto-generated method stub
		return 0.0d;
	}

	@Override
	public boolean canInterface(ForgeDirection direction) {
		// Auto-generated method stub
		return true;
	}

	@Override
	public double getStoredEnergy() {
		// Auto-generated method stub
		return 0.0d;
	}

	@Override
	public double getEnergyCapacity() {
		// Auto-generated method stub
		return 0.0d;
	}

	@Override
	public World getWorld() {
		// Auto-generated method stub
		return null;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return canInterface(direction);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return canInterface(direction);
	}

	@Override
	public double getOfferedEnergy() {
		return getStoredEnergy();
	}

	@Override
	public void drawEnergy(double amount) {
		pullEnergy(amount, false);
	}

	@Override
	public int getSourceTier() {
		return 4;
	}

	@Override
	public double getDemandedEnergy() {
		return getEnergyCapacity() - getStoredEnergy();
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		return acceptEnergy(amount, false);
	}

	@DeviceDrivers.DriverRTCallback(event = "blockPlace")
	public void ic2AddTile(LCTile tile) {
		try {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			driverIC2Power_onEnergyNet = true;
		} catch (Throwable t) {
			LCLog.warn("Driver code could not add tile to network!", t);
		}
	}

	@DeviceDrivers.DriverRTCallback(event = "blockBreak")
	public void ic2RemoveTile(LCTile tile) {
		if (driverIC2Power_onEnergyNet)
			try {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
				driverIC2Power_onEnergyNet = false;
			} catch (Throwable t) {
				LCLog.warn("Driver code could not remove tile from network!", t);
			}
	}

}
