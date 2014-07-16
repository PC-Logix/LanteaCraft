package pcl.lc.module.stargate.item;

import net.minecraft.item.Item;
import pcl.lc.core.ResourceAccess;

public class ItemGDO extends Item {

	public ItemGDO() {
		super();
	}

	@Override
	protected String getIconString() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", "gdo_iris_controller");
	}
}
