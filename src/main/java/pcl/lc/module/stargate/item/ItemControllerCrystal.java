package pcl.lc.module.stargate.item;

import net.minecraft.item.Item;
import pcl.lc.core.ResourceAccess;

public class ItemControllerCrystal extends Item {

	public ItemControllerCrystal() {
		super();
	}

	@Override
	protected String getIconString() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", "sgControllerCrystal");
	}
}