package lc.common.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import lc.api.event.IBlockEventHandler;
import lc.common.LCLog;
import lc.common.network.IPacketHandler;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCTileSync;
import lc.common.util.math.DimensionPos;
import lc.core.LCRuntime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class LCTile extends TileEntity implements IInventory, IPacketHandler, IBlockEventHandler {

	private static HashMap<Class<? extends LCTile>, HashMap<String, ArrayList<String>>> callbacks = new HashMap<Class<? extends LCTile>, HashMap<String, ArrayList<String>>>();

	public static void registerCallback(Class me, String method, String event) {
		Class<? extends LCTile> tile = (Class<? extends LCTile>) me;
		if (!callbacks.containsKey(tile))
			callbacks.put(tile, new HashMap<String, ArrayList<String>>());
		HashMap<String, ArrayList<String>> me_calls = callbacks.get(tile);
		if (!me_calls.containsKey(event))
			me_calls.put(event, new ArrayList<String>());
		if (!me_calls.get(event).contains(method))
			me_calls.get(event).add(method);
		LCLog.debug("Driver adding callback on class %s event %s: %s", me.getName(), event, method);
	}

	public static void doCallbacksNow(Object me, String type) {
		Class<? extends LCTile> meClazz = (Class<? extends LCTile>) me.getClass();
		ArrayList<String> cbs = getCallbacks(meClazz, type);
		if (cbs == null)
			return;
		doCallbacks(meClazz, me, cbs);
	}

	public static ArrayList<String> getCallbacks(Class<? extends LCTile> me, String type) {
		if (!callbacks.containsKey(me))
			return null;
		HashMap<String, ArrayList<String>> me_calls = callbacks.get(me);
		if (!me_calls.containsKey(type))
			return null;
		return me_calls.get(type);
	}

	public static void doCallbacks(Class<? extends LCTile> me, Object meObject, ArrayList<String> methods) {
		Method[] meMethods = me.getMethods();
		for (String methodName : methods) {
			for (int i = 0; i < meMethods.length; i++)
				if (meMethods[i].getName().equalsIgnoreCase(methodName)) {
					try {
						Method invoke = meMethods[i];
						invoke.invoke(meObject, new Object[] { (LCTile) meObject });
					} catch (Throwable t) {
						LCLog.warn("Error when processing callback %s!", methodName, t);
					}
					break;
				}
		}
	}

	private NBTTagCompound compound;
	private boolean nbtDirty;

	public abstract IInventory getInventory();

	public abstract void thinkClient();

	public abstract void thinkServer();

	public abstract void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException;

	public abstract void save(NBTTagCompound compound);

	public abstract void load(NBTTagCompound compound);

	public NBTTagCompound getBaseCompound() {
		return compound;
	}

	public ForgeDirection getRotation() {
		if (compound == null || !compound.hasKey("rotation"))
			return ForgeDirection.NORTH;
		return ForgeDirection.getOrientation(compound.getInteger("rotation"));
	}

	public void setRotation(ForgeDirection direction) {
		if (compound == null)
			compound = new NBTTagCompound();
		compound.setInteger("rotation", direction.ordinal());
		markNbtDirty();
	}

	@Override
	public void blockPlaced() {
		LCTile.doCallbacksNow(this, "blockPlace");
	}

	@Override
	public void blockBroken() {
		LCTile.doCallbacksNow(this, "blockBreak");
	}

	private void markNbtDirty() {
		this.nbtDirty = true;
	}

	@Override
	public void handlePacket(LCPacket packetOf, EntityPlayer player) throws LCNetworkException {
		if (packetOf instanceof LCTileSync) {
			if (worldObj.isRemote) {
				this.compound = ((LCTileSync) packetOf).compound;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		} else
			thinkPacket(packetOf, player);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (worldObj != null) {
			if (worldObj.isRemote)
				thinkClient();
			else {
				thinkServer();
				if (nbtDirty) {
					nbtDirty = false;
					LCTileSync packet = new LCTileSync(new DimensionPos(this), compound);
					LCRuntime.runtime.network().sendToAllAround(packet, packet.target, 128.0d);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		if (p_145839_1_.hasKey("base-tag"))
			this.compound = p_145839_1_.getCompoundTag("base-tag");
		else
			this.compound = new NBTTagCompound();
		markNbtDirty();
		try {
			load(p_145839_1_);
		} catch (Throwable t) {
			LCLog.warn("Failed when loading data from NBT for tile.", t);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		if (this.compound != null)
			p_145841_1_.setTag("base-tag", this.compound);
		try {
			save(p_145841_1_);
		} catch (Throwable t) {
			LCLog.warn("Failed when saving data to NBT for tile.", t);
		}
	}

	@Override
	public int getSizeInventory() {
		if (getInventory() == null)
			return 0;
		return getInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		if (getInventory() == null)
			return null;
		return getInventory().getStackInSlot(p_70301_1_);
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		if (getInventory() == null)
			return null;
		return getInventory().decrStackSize(p_70298_1_, p_70298_2_);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		if (getInventory() == null)
			return null;
		return getInventory().getStackInSlotOnClosing(p_70304_1_);
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		if (getInventory() == null)
			return;
		getInventory().setInventorySlotContents(p_70299_1_, p_70299_2_);
	}

	@Override
	public String getInventoryName() {
		if (getInventory() == null)
			return null;
		return getInventory().getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		if (getInventory() == null)
			return false;
		return getInventory().hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		if (getInventory() == null)
			return 0;
		return getInventory().getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		if (getInventory() == null)
			return false;
		return getInventory().isUseableByPlayer(p_70300_1_);
	}

	@Override
	public void openInventory() {
		if (getInventory() == null)
			return;
		getInventory().openInventory();
	}

	@Override
	public void closeInventory() {
		if (getInventory() == null)
			return;
		getInventory().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		if (getInventory() == null)
			return false;
		return getInventory().isItemValidForSlot(p_94041_1_, p_94041_2_);
	}

}
