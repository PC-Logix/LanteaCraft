package lc.tiles;

import net.minecraft.inventory.IInventory;
import cpw.mods.fml.relauncher.Side;
import lc.api.rendering.ITileRenderInfo;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.util.data.StateMap;

public class TileFrame extends LCMultiblockTile {

	public TileFrame() {
		setSlave(true);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public StructureConfiguration getConfiguration() {
		return null;
	}

	@Override
	public void thinkMultiblock() {
		// TODO Auto-generated method stub

	}

	@Override
	public IInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void thinkClient() {
		// TODO Auto-generated method stub

	}

	@Override
	public void thinkServer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldRender() {
		return false;
	}

	@Override
	public String[] debug(Side side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITileRenderInfo renderInfoTile() {
		return null;
	}

}
