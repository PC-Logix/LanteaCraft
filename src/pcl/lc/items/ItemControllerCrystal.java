package pcl.lc.items;

import net.minecraft.item.Item;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemControllerCrystal extends Item {

	public ItemControllerCrystal(int id) {
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getInstance().getAssetKey() + ":" + getUnlocalizedName() + "_"
				+ LanteaCraft.getProxy().getRenderMode();
	}
}