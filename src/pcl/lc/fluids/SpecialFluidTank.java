package pcl.lc.fluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class SpecialFluidTank implements IFluidTank {

	private boolean canFill;
	private boolean canDrain;
	private int capacity;
	private int volume;
	private Fluid fluid;

	public SpecialFluidTank(Fluid f, int cap, int vol, boolean fill, boolean drain) {
		fluid = f;
		capacity = cap;
		volume = vol;
		canFill = fill;
		canDrain = drain;
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
		if (doFill)
			volume += quantity;
		return quantity;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (!canDrain)
			return null;
		int quantity = Math.min(maxDrain, volume);
		if (doDrain)
			volume -= quantity;
		return new FluidStack(fluid, quantity);
	}
}
