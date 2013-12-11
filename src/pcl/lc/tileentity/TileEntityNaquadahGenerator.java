package pcl.lc.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import pcl.common.base.PoweredTileEntity;
import pcl.common.inventory.FilterRule;
import pcl.common.inventory.FilteredInventory;
import pcl.lc.LanteaCraft;
import pcl.lc.core.EnumUnits;
import pcl.lc.fluids.SpecialFluidTank;

public class TileEntityNaquadahGenerator extends PoweredTileEntity implements IFluidHandler {

	private double energy = 0.0;
	private boolean addedToEnergyNet = false;
	private SpecialFluidTank tank = new SpecialFluidTank(LanteaCraft.Fluids.fluidLiquidNaquadah, 8000, 0, true, true);
	private FilteredInventory inventory = new FilteredInventory(1) {
		@Override
		public void onInventoryChanged() {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean isInvNameLocalized() {
			return true;
		}

		@Override
		public String getInvName() {
			return "Naquadah Generator";
		}
	};

	public TileEntityNaquadahGenerator() {
		super();
		inventory.setFilterRule(0, new FilterRule(new ItemStack[] { new ItemStack(LanteaCraft.Items.naquadah, 1) },
				null, true));
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagCompound tankCompound = nbt.getCompoundTag("tank");
		if (tankCompound != null)
			tank.readFromNBT(tankCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tankCompound = new NBTTagCompound();
		tank.writeToNBT(tankCompound);
		nbt.setCompoundTag("tank", tankCompound);
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote)
			if (!addedToEnergyNet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
				addedToEnergyNet = true;
				onInventoryChanged();
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

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!resource.isFluidEqual(tank.getFluid()))
			return null;
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.getID() == tank.getFluid().fluidID && tank.canFill();
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid.getID() == tank.getFluid().fluidID && tank.canDrain();
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public boolean canEnergyFormatConnectToSide(EnumUnits typeof, ForgeDirection direction) {
		return true;
	}
}
