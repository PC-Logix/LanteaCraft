package pcl.lc.module.machine.tile;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.LanteaInternalRecipe;
import pcl.lc.base.TileManaged;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.client.audio.AudioEngine;
import pcl.lc.client.audio.AudioPosition;
import pcl.lc.client.audio.AudioSource;
import pcl.lc.module.ModuleCore.Items;
import pcl.lc.module.core.item.ItemCraftingReagent.ReagentList;
import pcl.lc.module.machine.block.BlockCrystalInfuser;
import pcl.lc.util.Vector3;

public class TileCrystalInfuser extends TileManaged {

	private static ArrayList<LanteaInternalRecipe> crystalRecipes = new ArrayList<LanteaInternalRecipe>();

	public static void registerCrystalRecipe(LanteaInternalRecipe recipe) {
		TileCrystalInfuser.crystalRecipes.add(recipe);
	}

	static {
		registerCrystalRecipe(new LanteaInternalRecipe(true, new ItemStack[] { new ItemStack(Items.reagentItem, 1,
				ReagentList.CONTROLCRYSTAL.ordinal()) }, new ItemStack(Items.reagentItem, 1,
				ReagentList.BLANKCRYSTAL.ordinal()), new ItemStack(net.minecraft.init.Items.dye, 1, 14)));
		registerCrystalRecipe(new LanteaInternalRecipe(true, new ItemStack[] { new ItemStack(Items.reagentItem, 1,
				ReagentList.CORECRYSTAL.ordinal()) }, new ItemStack(Items.reagentItem, 1,
				ReagentList.BLANKCRYSTAL.ordinal()), new ItemStack(net.minecraft.init.Items.dye, 1, 4)));
	}

	private FilteredInventory inventory = new FilteredInventory(3) {
		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public String getInventoryName() {
			return StatCollector.translateToLocal("lanteacraft.inventory.crystalinfuser");
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int var1) {
			return new int[0];
		}

		@Override
		public boolean canInsertItem(int var1, ItemStack var2, int var3) {
			return false;
		}

		@Override
		public boolean canExtractItem(int var1, ItemStack var2, int var3) {
			return false;
		}
	};

	private AudioSource source;

	public TileCrystalInfuser() {
		metadata.set("hasRecipe", false);
		metadata.set("progress", 0.0f);
		metadata.set("enabled", false);
	}

	public boolean hasRecipe() {
		if (!metadata.containsKey("hasRecipe"))
			return false;
		return (Boolean) metadata.get("hasRecipe");
	}

	public float getProgress() {
		if (!metadata.containsKey("progress"))
			return 0.0f;
		return (Float) metadata.get("progress");
	}

	public LanteaInternalRecipe getCurrentRecipe() {
		for (LanteaInternalRecipe recipe : TileCrystalInfuser.crystalRecipes) {
			if (recipe.reagents() > 2 || recipe.products() > 1)
				continue;
			boolean flag = true;
			for (int i = 0; i < recipe.reagents(); i++) {
				ItemStack reagent = recipe.reagent(i);
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
					flag = false;
					break;
				}
				if (!reagent.isItemEqual(stack) || stack.stackSize < reagent.stackSize) {
					flag = false;
					break;
				}
			}
			if (flag)
				return recipe;
		}
		return null;
	}

	private boolean checkState() {
		return getCurrentRecipe() != null;
	}

	public void setRedstoneInputSignal(boolean state) {
		metadata.set("enabled", state);
	}

	public boolean enabled() {
		if (!metadata.containsKey("enabled"))
			return false;
		return (Boolean) metadata.get("enabled");
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void think() {
		if (!enabled()) {
			if (getProgress() > 0.0f)
				metadata.set("progress", 0.0f);
			if (worldObj.isRemote && source != null && source.isPlaying())
				source.stop();
			return;
		}

		if (!worldObj.isRemote)
			metadata.set("hasRecipe", checkState());

		if (hasRecipe()) {
			if (worldObj.isRemote) {
				if (source == null) {
					AudioEngine manager = LanteaCraft.getProxy().getAudioEngine();
					source = manager.create(this, new AudioPosition(worldObj, new Vector3(this)),
							"ambience/electrical_crackle.ogg", true, false, 1.0f);
				}
				if (!source.isPlaying())
					source.play();
			}

			if (worldObj.isRemote && getProgress() >= 10.0f) {
				if (source.isPlaying())
					source.stop();
			}

			if (!worldObj.isRemote)
				metadata.set("progress", getProgress() + 0.1f);
			if (!worldObj.isRemote && getProgress() >= 10.0f) {
				metadata.set("progress", 10.0f);
				LanteaInternalRecipe recipe = getCurrentRecipe();

				ItemStack currentStack = inventory.getStackInSlot(2);
				ItemStack product = recipe.product(0).copy();
				if (currentStack == null
						|| (currentStack.isItemEqual(recipe.product(0)) && currentStack.stackSize + product.stackSize <= 64)) {
					for (int i = 0; i < recipe.reagents(); i++) {
						ItemStack rule = recipe.reagent(i);
						inventory.getStackInSlot(i).stackSize -= rule.stackSize;
						if (inventory.getStackInSlot(i).stackSize <= 0)
							inventory.setInventorySlotContents(i, null);
					}
					if (currentStack != null && currentStack.getItem() != null && currentStack.isItemEqual(product)) {
						ItemStack newStack = currentStack.copy();
						newStack.stackSize += product.stackSize;
						inventory.setInventorySlotContents(2, newStack);
					} else
						inventory.setInventorySlotContents(2, product);
					inventory.markDirty();
					markDirty();
					metadata.set("progress", 0.0f);
				}
			}
		} else {
			if (getProgress() > 0.0f) {
				metadata.set("progress", 0.0f);
			}
			if (worldObj.isRemote && source != null && source.isPlaying())
				source.stop();
		}
	}

	@Override
	public void thinkPacket(ModPacket packet, EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub

	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 1, yCoord + 1,
				zCoord + 1);
	}

	public int getRotation() {
		return ((BlockCrystalInfuser) getBlockType()).rotationInWorld(getBlockMetadata(), this);
	}

}
