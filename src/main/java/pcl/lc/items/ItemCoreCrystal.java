package pcl.lc.items;

import net.minecraft.item.Item;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCoreCrystal extends Item {

	public ItemCoreCrystal(int id) {
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":sgCoreCrystal_" + LanteaCraft.getProxy().getRenderMode();
	}
}