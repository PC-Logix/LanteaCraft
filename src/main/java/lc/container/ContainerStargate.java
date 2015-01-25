package lc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import lc.common.base.LCContainer;
import lc.common.base.inventory.FilteredInventory;
import lc.common.base.inventory.FilteredSlot;
import lc.tiles.TileStargateBase;

public class ContainerStargate extends LCContainer {

	private TileStargateBase te;
	private FilteredInventory filterInventory;

	public ContainerStargate(TileStargateBase tile, EntityPlayer player) {
		super(256, 208);
		this.te = tile;
		filterInventory = (FilteredInventory) te.getInventory();
		addSlotToContainer(new FilteredSlot(filterInventory, 0, 48, 99, false));
		addPlayerSlots(player, 48, 123);
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
