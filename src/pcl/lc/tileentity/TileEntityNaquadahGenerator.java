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
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import ic2.api.*;
import ic2.api.energy.*;
import ic2.api.energy.tile.*;
import ic2.api.energy.event.*;
import ic2.api.tile.IWrenchable;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;

public class TileEntityNaquadahGenerator extends GenericTileEntity implements IEnergySource, IWrenchable,
		IPowerEmitter, IPipeConnection {

	private static final int MAX_SEND = 100;

	protected PowerHandler powerHandler;

	int energy = 0;
	public static boolean isActive = false;
	boolean addedToEnergyNet = false;

	public TileEntityNaquadahGenerator() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		//powerHandler.writeToNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		//powerHandler.readFromNBT(nbt);
	}

	// START IC2
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public short getFacing() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFacing(short facing) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getWrenchDropRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getOfferedEnergy() {
		// TODO Auto-generated method stub
		return 32;
	}

	@Override
	public void drawEnergy(double amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (!this.addedToEnergyNet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				this.addedToEnergyNet = true;
				onInventoryChanged();

			} else {
				if (getOfferedEnergy() > 0) {
					isActive = true;
				} else {
					isActive = false;
				}
			}
		}
		super.updateEntity();
	}

	@Override
	public void invalidate() {
		if (addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnergyNet = false;
			isActive = false;
			onInventoryChanged();
		}
		super.invalidate();
	}

	// END IC2

	// START BC
	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (type == PipeType.POWER)
			return ConnectOverride.DEFAULT;
		return ConnectOverride.DISCONNECT;
	}
	/*
	 * @Override public void updateEntity() { super.updateEntity(); if
	 * (worldObj.isRemote) return; Direction[] directions = Direction.values();
	 * for (Direction apiDirection : directions) { ForgeDirection direction =
	 * apiDirection.toForgeDirection(); if(direction.ordinal() == getFacing()) {
	 * continue; } int x = direction.offsetX + xCoord; int y = direction.offsetY
	 * + yCoord; int z = direction.offsetZ + zCoord; TileEntity tile =
	 * worldObj.getBlockTileEntity(x, y, z); if (tile!=null && tile instanceof
	 * IPowerReceptor &&
	 * ((IPowerReceptor)tile).getPowerReceiver(direction.getOpposite())!=null) {
	 * PowerReceiver receptor = ((IPowerReceptor)
	 * tile).getPowerReceiver(direction.getOpposite());
	 * if(powerHandler.getEnergyStored() >= receptor.getMinEnergyReceived() &&
	 * MAX_SEND >= receptor.getMinEnergyReceived()) { float toSend =
	 * Math.min(powerHandler.getEnergyStored(),
	 * receptor.getMaxEnergyReceived()); float needed =
	 * receptor.receiveEnergy(PowerHandler.Type.MACHINE, toSend,
	 * direction.getOpposite()); powerHandler.useEnergy(1, needed, true);
	 * data[index] += needed; } }
	 * 
	 * } }
	 */
	// END BC

}
