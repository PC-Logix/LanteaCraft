package pcl.lc.module.power.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import pcl.lc.api.EnumUnits;
import pcl.lc.api.access.INaquadahGeneratorAccess;
import pcl.lc.base.PoweredTileEntity;
import pcl.lc.base.SpecialFluidTank;
import pcl.lc.base.inventory.FilterRule;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.network.IPacketHandler;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.ModulePower;
import pcl.lc.module.power.item.ItemEnergyCrystal;
import pcl.lc.util.ReflectionHelper;

public class TileNaquadahGenerator extends PoweredTileEntity implements IPacketHandler, IFluidHandler,
		INaquadahGeneratorAccess {

	public final double maxEnergy = 10.0d;

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

	public TileNaquadahGenerator() {
		super();
		metadata.set("energy", 0.0d);
		metadata.set("simulating", false);
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
		if (nbt.hasKey("energy"))
			metadata.set("energy", nbt.getDouble("energy"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound tankCompound = new NBTTagCompound();
		tank.writeToNBT(tankCompound);
		nbt.setTag("tank", tankCompound);
		nbt.setDouble("energy", (Double) metadata.get("energy"));
	}

	@Override
	public void think() {
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
			refuel();
		}
	}

	public void stateChanged() {
		markDirty();
	}

	public void refuel() {
		if (isEnabled() && maxEnergy > (Double) metadata.get("energy") + 1.0d)
			for (int i = 0; i < 4; i++) {
				ItemStack stackOf = inventory.getStackInSlot(i);
				if (stackOf != null && stackOf.stackSize > 0) {
					ItemStack newStack = stackOf.copy();
					newStack.stackSize--;
					if (newStack.stackSize > 0)
						inventory.setInventorySlotContents(i, newStack);
					else
						inventory.setInventorySlotContents(i, null);
					metadata.set("energy", (Double) metadata.get("energy") + 1.0);
					stateChanged();
					break;
				}
			}

		if (isEnabled() && maxEnergy > ((Double) metadata.get("energy") + 0.1d) && tank.getFluidAmount() > 100)
			if (tank.drain(100, false).amount == 100) {
				tank.drain(100, true);
				metadata.set("energy", (Double) metadata.get("energy") + 0.1d);
				stateChanged();
			}

		if (isEnabled() && inventory.getStackInSlot(4) != null) {
			ItemStack stack = inventory.getStackInSlot(4);
			ItemEnergyCrystal crystal = (ItemEnergyCrystal) stack.getItem();
			if (crystal.getMaximumEnergy() > crystal.getEnergyStored(stack) && (Double) metadata.get("energy") > 0.01d) {
				double used = crystal.receiveEnergy(stack, 0.01d, false);
				metadata.set("energy", (Double) metadata.get("energy") - used);
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
		if (!isActive() || !isEnabled())
			return 0;
		return Math.min((Double) metadata.get("energy"), getMaximumExportEnergy());
	}

	@Override
	public void receiveEnergy(double units) {
		return;
	}

	@Override
	public double exportEnergy(double units) {
		if (!isEnabled())
			return 0.0d;
		double reallyExportedUnits = Math.min(units, (Double) metadata.get("energy"));
		metadata.set("energy", (Double) metadata.get("energy") - reallyExportedUnits);
		return reallyExportedUnits;
	}

	public boolean isActive() {
		return isEnabled() && (Double) metadata.get("energy") > 0;
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
		if (!metadata.containsKey("simulating"))
			return false;
		return (Boolean) metadata.get("simulating");
	}

	@Override
	public boolean setEnabled(boolean enable) {
		metadata.set("simulating", enable);
		return true;
	}

	@Override
	public double getStoredEnergy() {
		if (!isEnabled() || !metadata.containsKey("energy"))
			return 0;
		return (Double) metadata.get("energy");
	}

	@Override
	public double getStoredEnergy(EnumUnits unitsOf) {
		if (!isEnabled())
			return 0;
		return EnumUnits.convertFromNaquadahUnit(unitsOf, (Double) metadata.get("energy"));
	}

	@Override
	public double getMaximumStoredEnergy() {
		return maxEnergy;
	}

	public void onHostBlockBreak() {
		// TODO Auto-generated method stub

	}

	public void setRedstoneInputSignal(int sig) {
		metadata.set("simulating", sig > 0);
	}

	@Override
	public void thinkPacket(ModPacket packet, EntityPlayer player) {
	}

	@Override
	public void detectAndSendChanges() {
	}
}
