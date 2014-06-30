package pcl.lc.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Stub class to prevent TileEntity instances from having to directly use
 * IFluidTank interfaces in their source. Proxy calls to here.
 * 
 * @author AfterLifeLochie
 */
public class SpecialFluidTank implements IFluidTank {

	private boolean canFill;
	private boolean canDrain;
	private int capacity;
	private int volume;
	private Fluid fluid;

	private boolean changed;

	public SpecialFluidTank(Fluid f, int cap, int vol, boolean fill, boolean drain) {
		fluid = f;
		capacity = cap;
		volume = vol;
		canFill = fill;
		canDrain = drain;
	}

	public boolean hasChanged() {
		if (changed) {
			changed = !changed;
			return true;
		} else
			return false;
	}

	public boolean canFill() {
		return canFill;
	}

	public boolean canDrain() {
		return canDrain;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		capacity = nbt.getInteger("capacity");
		volume = nbt.getInteger("volume");
		changed = true;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("capacity", capacity);
		nbt.setInteger("volume", volume);
	}

	@Override
	public FluidStack getFluid() {
		if (volume > 0)
			return new FluidStack(fluid, volume);
		return null;
	}

	@Override
	public int getFluidAmount() {
		return volume;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (!canFill || !resource.isFluidEqual(new FluidStack(fluid, volume)))
			return 0;
		int quantity = Math.min(resource.amount, capacity - volume);
		if (doFill) {
			volume += quantity;
			changed = true;
		}
		return quantity;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (!canDrain)
			return null;
		int quantity = Math.min(maxDrain, volume);
		if (doDrain) {
			volume -= quantity;
			changed = true;
		}
		return new FluidStack(fluid, quantity);
	}
}
