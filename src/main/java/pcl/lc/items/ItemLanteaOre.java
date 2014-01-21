package pcl.lc.items;

import pcl.lc.LanteaCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;

public class ItemNaquadah extends Item {

	public ItemNaquadah(int id) {
		super(id);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":naquadah_" + LanteaCraft.getProxy().getRenderMode();
	}

}
