package pcl.lc.tileentity;

import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import pcl.lc.base.GenericTileEntity;
import pcl.lc.base.PoweredTileEntity;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import ic2.api.*;
import ic2.api.energy.*;
import ic2.api.energy.tile.*;
import ic2.api.energy.event.*;
import ic2.api.tile.IWrenchable;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;

public class TileEntityNaquadahGenerator extends PoweredTileEntity {

	private double energy = 0.0;
	private boolean addedToEnergyNet = false;

	public TileEntityNaquadahGenerator() {
		super();
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (!this.addedToEnergyNet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToEnergyNet = true;
				onInventoryChanged();
			}
		}
		super.updateEntity();

		// TODO: This is temporary logic code, so I can test energy emitting.
		if (8.0 > energy)
			energy += 0.2;
		if (energy > 8.0)
			energy = 8.0;
	}

	@Override
	public void invalidate() {
		if (addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnergyNet = false;
			onInventoryChanged();
		}
		super.invalidate();
	}

	@Override
	public boolean canReceiveEnergy() {
		return false;
	}

	@Override
	public boolean canExportEnergy() {
		return true;
	}

	@Override
	public double getMaximumReceiveEnergy() {
		return 0;
	}

	@Override
	public double getMaximumExportEnergy() {
		return 2.0;
	}

	@Override
	public double getAvailableExportEnergy() {
		if (!isActive())
			return 0;
		return Math.min(energy, getMaximumExportEnergy());
	}

	@Override
	public void receiveEnergy(double units) {
		return;
	}

	@Override
	public double exportEnergy(double units) {
		double reallyExportedUnits = Math.min(units, energy);
		energy -= reallyExportedUnits;
		return reallyExportedUnits;
	}

	public boolean isActive() {
		return (energy > 0);
	}
}
