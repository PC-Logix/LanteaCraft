package lc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import lc.common.base.LCContainer;
import lc.common.configuration.xml.ComponentConfig;
import lc.tiles.TileDHD;

/**
 * DHD container implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class ContainerDHD extends LCContainer {
	/**
	 * Default constructor
	 * 
	 * @param tile
	 *            The DHD tile
	 * @param player
	 *            The local player
	 */
	public ContainerDHD(TileDHD tile, EntityPlayer player) {
		super(256, 208);
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
