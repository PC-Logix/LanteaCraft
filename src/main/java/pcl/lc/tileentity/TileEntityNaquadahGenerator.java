package pcl.lc.tileentity;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import pcl.common.base.PoweredTileEntity;
import pcl.common.helpers.ReflectionHelper;
import pcl.common.inventory.FilterRule;
import pcl.common.inventory.FilteredInventory;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumUnits;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.fluids.SpecialFluidTank;
import pcl.lc.items.ItemEnergyCrystal;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.ModulePower;

public class TileEntityNaquadahGenerator extends PoweredTileEntity implements IPacketHandler, IFluidHandler,
		INaquadahGeneratorAccess {

	public boolean simulate = false;

	public double energy = 0.0;
	public double maxEnergy = 10.0;

	public double displayEnergy = 0;
	public double displayTankVolume = 0;

	public SpecialFluidTank tank = new SpecialFluidTank(ModuleCore.Fluids.fluidLiquidNaquadah, 8000, 0, true, true);

	private FilteredInventory inventory = new FilteredInventory(5) {

		@Override
		public String getInventoryName() {
			return "naquadah_generator";
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int var1) {
			return new int[] { 0, 1, 2, 3 };
		}

		@Override
		public boolean canInsertItem(int i, ItemStack itemstack, int j) {
			if (0 > i || i > items.length)
				return false;
			return items[i] == null || ItemStack.areItemStacksEqual(items[i], itemstack);
		}

		@Override
		public boolean canExtractItem(int i, ItemStack itemstack, int j) {
			if (0 > i || i > items.length)
				return false;
			return true;
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}
	};

	public TileEntityNaquadahGenerator() {
		super();
		FilterRule naquadah = new FilterRule(new ItemStack[] { new ItemStack(ModuleCore.Items.lanteaOreItem, 1) },
				null, true, true);
		FilterRule energyCrystal = new FilterRule(
				new ItemStack[] { new ItemStack(ModulePower.Items.energyCrystal, 1) }, null, true, false);
		for (int i = 0; i < 4; i++)
			inventory.setFilterRule(i, naquadah);
		inventory.setFilterRule(4, energyCrystal);
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
		nbt.setTag("tank", tankCompound);
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			List<String> ifaces = ReflectionHelper.getInterfacesOf(this.getClass(), true);
			if (!addedToEnergyNet)
				if (ifaces.contains("ic2.api.energy.tile.IEnergyEmitter")
						|| ifaces.contains("ic2.api.energy.tile.IEnergyAcceptor")) {
					postIC2Update(true);
					stateChanged();
				}
			if (tank.hasChanged())
				stateChanged();
			super.updateEntity();

			refuel();
		}
	}

	public void stateChanged() {
		markDirty();
		getDescriptionPacket();
	}

	public void getStateFromPacket(ModPacket packet) {
		// TODO: Convert to TinyModPacket, SMP is too clunky.
		StandardModPacket packetOf = (StandardModPacket) packet;
		simulate = (Boolean) packetOf.getValue("simulate");
		energy = (Double) packetOf.getValue("energy");
	}

	public ModPacket getPacketFromState() {
		// TODO: Convert to TinyModPacket, SMP is too clunky.
		StandardModPacket packet = new StandardModPacket(new WorldLocation(this));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.TileUpdate");
		packet.setValue("simulate", simulate);
		packet.setValue("energy", energy);
		return packet;
	}

	@Override
	public Packet getDescriptionPacket() {
		LanteaCraft.getNetPipeline().sendToAll(getPacketFromState());
		return null;
	}

	public void refuel() {
		if (isEnabled() && maxEnergy > energy + 1.0d)
			for (int i = 0; i < 4; i++) {
				ItemStack stackOf = inventory.getStackInSlot(i);
				if (stackOf != null && stackOf.stackSize > 0) {
					ItemStack newStack = stackOf.copy();
					newStack.stackSize--;
					if (newStack.stackSize > 0)
						inventory.setInventorySlotContents(i, newStack);
					else
						inventory.setInventorySlotContents(i, null);
					energy += 1.0;
					stateChanged();
					break;
				}
			}

		if (isEnabled() && maxEnergy > (energy + 0.1) && tank.getFluidAmount() > 100)
			if (tank.drain(100, false).amount == 100) {
				tank.drain(100, true);
				energy += 0.1;
				stateChanged();
			}

		if (isEnabled() && inventory.getStackInSlot(4) != null) {
			ItemStack stack = inventory.getStackInSlot(4);
			ItemEnergyCrystal crystal = (ItemEnergyCrystal) stack.getItem();
			if (crystal.getMaximumEnergy() > crystal.getEnergyStored(stack) && energy > 0.01d) {
				double used = crystal.receiveEnergy(stack, 0.01d, false);
				energy -= used;
			}
		}
	}

	@Override
	public void invalidate() {
		List<String> ifaces = ReflectionHelper.getInterfacesOf(this.getClass(), true);
		if (addedToEnergyNet)
			if (ifaces.contains("ic2.api.energy.tile.IEnergyEmitter")
					|| ifaces.contains("ic2.api.energy.tile.IEnergyAcceptor")) {
				postIC2Update(false);
				markDirty();
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
		return 0.1;
	}

	@Override
	public double getAvailableExportEnergy() {
		if (!isActive() || !simulate)
			return 0;
		return Math.min(energy, getMaximumExportEnergy());
	}

	@Override
	public void receiveEnergy(double units) {
		return;
	}

	@Override
	public double exportEnergy(double units) {
		if (!simulate)
			return 0.0d;
		double reallyExportedUnits = Math.min(units, energy);
		energy -= reallyExportedUnits;
		return reallyExportedUnits;
	}

	public boolean isActive() {
		return simulate && energy > 0;
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

	@Override
	public boolean isEnabled() {
		return simulate;
	}

	@Override
	public boolean setEnabled(boolean enable) {
		return (simulate = enable) || true;
	}

	@Override
	public double getStoredEnergy() {
		if (!simulate)
			return 0;
		return energy;
	}

	@Override
	public double getStoredEnergy(EnumUnits unitsOf) {
		if (!simulate)
			return 0;
		return EnumUnits.convertFromNaquadahUnit(unitsOf, energy);
	}

	@Override
	public double getMaximumStoredEnergy() {
		return maxEnergy;
	}

	public void onHostBlockBreak() {
		// TODO Auto-generated method stub

	}

	public void setRedstoneInputSignal(int sig) {
		boolean oldState = simulate;
		simulate = (sig > 0);
		if (oldState != simulate)
			getDescriptionPacket();
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		getStateFromPacket(packetOf);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
