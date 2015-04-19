package lc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import lc.common.base.LCContainer;
import lc.common.base.inventory.FilteredInventory;
import lc.common.configuration.xml.ComponentConfig;
import lc.tiles.TileDHD;

public class ContainerDHD extends LCContainer {

	private TileDHD te;
	private FilteredInventory filterInventory;

	public ContainerDHD(TileDHD tile, EntityPlayer player) {
		super(256, 208);
		this.te = tile;
		filterInventory = (FilteredInventory) te.getInventory();

	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		// TODO Auto-generated method stub

	}

}
