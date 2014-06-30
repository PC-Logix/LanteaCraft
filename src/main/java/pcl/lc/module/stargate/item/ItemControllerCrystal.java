package pcl.lc.module.stargate.item;

import net.minecraft.item.Item;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemControllerCrystal extends Item {

	public ItemControllerCrystal() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":sgControllerCrystal_" + LanteaCraft.getProxy().getRenderMode();
	}
}