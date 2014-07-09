package pcl.lc.base;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.base.data.ObserverContext;
import pcl.lc.base.data.WatchedList;
import pcl.lc.base.network.IPacketHandler;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.WatchedListContainerPacket;
import pcl.lc.base.network.packet.WatchedListRequestPacket;
import pcl.lc.base.network.packet.WatchedListSyncPacket;

public abstract class TileManaged extends TileEntity implements IInventory, ISidedInventory, IPacketHandler {

	private final ObserverContext metacontext = new ObserverContext();
	private boolean cli_synchronized = false;
	private int cli_synchronize_wait = -1;
	
	public WatchedList<String, Object> metadata = new WatchedList<String, Object>();

	/**
	 * Updates the tile. Do not override this from TileManaged implementation;
	 * use the {@link #think()} method instead!
	 */
	@Override
	public void updateEntity() {
		this.think();
		if (!worldObj.isRemote) {
			if (metadata.modified(metacontext)) {
				WatchedListSyncPacket packet = new WatchedListSyncPacket(new WorldLocation(this), metadata);
				metadata.clearModified(metacontext);
				LanteaCraft.getNetPipeline().sendToAllAround(packet, packet.getOriginLocation(), 128.0d);
			}
			detectAndSendChanges();
		} else {
			if (!cli_synchronized) {
				cli_synchronized = true;
				cli_synchronize_wait = 120;
				WatchedListRequestPacket packet = new WatchedListRequestPacket(new WorldLocation(this));
				LanteaCraft.getNetPipeline().sendToServer(packet);
			} else {
				if (cli_synchronize_wait > 0)
					cli_synchronize_wait--;
				if (cli_synchronize_wait == 0)
					cli_synchronized = false;
			}
		}
	}

	/**
	 * Called once per tick to update the tile.
	 */
	public abstract void think();

	/**
	 * Called when a packet is received for this TileManaged instance.
	 * 
	 * @param packet
	 *            The network packet.
	 */
	public abstract void thinkPacket(ModPacket packet, EntityPlayer player);

	/**
	 * Detect and send any changes on the server to the client. You are
	 * responsible for dispatching any packets, excluding the metadata
	 * synchronization packets.
	 */
	public abstract void detectAndSendChanges();

	/**
	 * Marks the host block for an update.
	 */
	public void markBlockForUpdate() {
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/**
	 * Plays a sound effect immediately using the Minecraft sound engine.
	 * 
	 * @param name
	 *            The sound object name.
	 * @param volume
	 *            The volume of the sound.
	 * @param pitch
	 *            The pitch of the sound.
	 */
	public void playSoundEffect(String name, float volume, float pitch) {
		if (name.contains(":"))
			LanteaCraft.getLogger().log(Level.WARN, "Old SoundSystem label detected, can't play label: " + name);
		else {
			String label = new StringBuilder().append(LanteaCraft.getAssetKey()).append(":").append(name).toString();
			try {
				ResourceLocation location = new ResourceLocation(label);
				if (Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream() == null)
					return;
				worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, label, volume, pitch);
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.DEBUG, "Couldn't play sound, doesn't exist: " + label, t);
			}
		}
	}

	/**
	 * Gets the inventory of the tile.
	 * 
	 * @return The tile's inventory.
	 */
	public IInventory getInventory() {
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = (NBTTagList) nbt.getTag("inventory");
			int n = list.tagCount();
			for (int i = 0; i < n; i++) {
				NBTTagCompound item = list.getCompoundTagAt(i);
				int slot = item.getInteger("slot");
				ItemStack stack = ItemStack.loadItemStackFromNBT(item);
				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = new NBTTagList();
			int n = inventory.getSizeInventory();
			for (int i = 0; i < n; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null) {
					NBTTagCompound item = new NBTTagCompound();
					item.setInteger("slot", i);
					stack.writeToNBT(item);
					list.appendTag(item);
				}
			}
			nbt.setTag("inventory", list);
		}
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getSizeInventory() : 0;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getStackInSlot(slot) : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.decrStackSize(slot, amount);
			return result;
		} else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.getStackInSlotOnClosing(slot);
			return result;
		} else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.setInventorySlotContents(slot, stack);
	}

	/**
	 * Returns the name of the inventory.
	 */
	@Override
	public String getInventoryName() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getInventoryName() : "";
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getInventoryStackLimit() : 0;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.isUseableByPlayer(player) : true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.isItemValidForSlot(slot, stack);
		else
			return false;
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed
	 * by automation on the given side of this block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);
		else
			return new int[0];
	}

	/**
	 * Returns true if automation can insert the given item in the given slot
	 * from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).canInsertItem(slot, stack, side);
		else
			return true;
	}

	/**
	 * Returns true if automation can extract the given item in the given slot
	 * from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).canExtractItem(slot, stack, side);
		else
			return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * Called from the network manager when a packet is received.
	 */
	@Override
	public void handlePacket(ModPacket packetOf, EntityPlayer player) {
		if (packetOf == null)
			return;
		if (packetOf instanceof WatchedListRequestPacket && player instanceof EntityPlayerMP) {
			WatchedListContainerPacket response = new WatchedListContainerPacket(new WorldLocation(this), metadata);
			LanteaCraft.getNetPipeline().sendTo(response, (EntityPlayerMP) player);
		} else if (packetOf instanceof WatchedListContainerPacket) {
			cli_synchronize_wait = -1;
			WatchedListContainerPacket container = (WatchedListContainerPacket) packetOf;
			container.apply(metadata);
		} else if (packetOf instanceof WatchedListSyncPacket) {
			cli_synchronize_wait = -1;
			WatchedListSyncPacket sync = (WatchedListSyncPacket) packetOf;
			sync.apply(metadata);
		} else
			this.thinkPacket(packetOf, player);
	}

}
