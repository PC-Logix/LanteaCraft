package pcl.lc.module.stargate.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import pcl.lc.base.GenericContainer;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.inventory.FilteredSlot;
import pcl.lc.module.stargate.tile.TileStargateBase;

public class ContainerStargateBase extends GenericContainer {

	private TileStargateBase te;
	private FilteredInventory filterInventory;

	public ContainerStargateBase(TileStargateBase te, EntityPlayer player) {
		super(256, 208);
		this.te = te;
		filterInventory = (FilteredInventory) te.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 48, 99, false));
		addPlayerSlots(player, 48, 123);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
