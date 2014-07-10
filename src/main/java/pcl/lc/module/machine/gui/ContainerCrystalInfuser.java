package pcl.lc.module.machine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.lc.base.GenericContainer;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.inventory.FilteredSlot;
import pcl.lc.module.machine.tile.TileCrystalInfuser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerCrystalInfuser extends GenericContainer {

	private TileCrystalInfuser infuser;
	private FilteredInventory filterInventory;
	private double craftingProgress;

	public ContainerCrystalInfuser(TileCrystalInfuser tile, EntityPlayer player) {
		super(177, 160);
		infuser = tile;
		filterInventory = (FilteredInventory) infuser.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 50, 4, false));
		addSlotToContainer(new FilteredSlot(filterInventory, 1, 50, 4 + 16 + 12, false));
		addSlotToContainer(new FilteredSlot(filterInventory, 2, 100, 4 + 12, false));
		addPlayerSlots(player, 8, 80);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		int progress = (int) Math.min(Math.floor(100 * 100 * (infuser.getProgress() / 10.0f)), 10000);
		crafter.sendProgressBarUpdate(this, 0, progress);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int i, int value) {
		super.updateProgressBar(i, value);
		if (i == 0)
			craftingProgress = value;
	}

	public double getCraftingProgress() {
		return craftingProgress / 100.0d;
	}

}
