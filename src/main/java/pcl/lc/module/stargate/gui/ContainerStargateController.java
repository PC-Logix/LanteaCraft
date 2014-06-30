package pcl.lc.module.stargate.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pcl.lc.base.GenericContainer;
import pcl.lc.module.stargate.tile.TileEntityStargateController;

public class ContainerStargateController extends GenericContainer {
	static final int numFuelSlotColumns = 2;
	static final int fuelSlotsX = 174;
	static final int fuelSlotsY = 84;
	static final int playerSlotsX = 48;
	static final int playerSlotsY = 124;

	private static final IInventory voidInventory = new IInventory() {
		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return false;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int i) {
			return null;
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return null;
		}

		@Override
		public int getSizeInventory() {
			return 0;
		}

		@Override
		public int getInventoryStackLimit() {
			return 0;
		}

		@Override
		public String getInventoryName() {
			return "this is not an inventory";
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			return null;
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}

		@Override
		public void markDirty() {
			// TODO Auto-generated method stub

		}

		@Override
		public void openInventory() {
			// TODO Auto-generated method stub

		}

		@Override
		public void closeInventory() {
			// TODO Auto-generated method stub

		}
	};

	public TileEntityStargateController te;

	public static ContainerStargateController create(EntityPlayer player, World world, int x, int y, int z) {
		TileEntityStargateController te = (TileEntityStargateController) world.getTileEntity(x, y, z);
		if (te != null)
			return new ContainerStargateController(te, player);
		return null;
	}

	public ContainerStargateController(TileEntityStargateController te, EntityPlayer player) {
		super(256, 208);
		this.te = te;
		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(voidInventory, i, -60, -60));
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
