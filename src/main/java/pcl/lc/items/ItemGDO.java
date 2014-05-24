package pcl.lc.items;

import pcl.lc.LanteaCraft;
import net.minecraft.item.Item;

public class ItemGDO extends Item {

	public ItemGDO(int id) {
		super(id);
	}

	@Override
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":gdo_iris_controller_" + LanteaCraft.getProxy().getRenderMode();
	}
}
