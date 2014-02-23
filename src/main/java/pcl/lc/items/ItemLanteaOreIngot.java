package pcl.lc.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;

public class ItemLanteaOreIngot extends Item {

	private Icon missing;

	public ItemLanteaOreIngot(int id) {
		super(id);
	}

	@Override
	public void registerIcons(IconRegister register) {
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setIngotItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadah_ingot_"
				+ LanteaCraft.getProxy().getRenderMode()));
		/*
		 * Disabled because people keep bitching, and I'm trying to work.
		 * OreTypes
		 * .NAQADRIAH.setIngotItemTexture(register.registerIcon(LanteaCraft
		 * .getAssetKey() + ":naqadriah_ingot_" +
		 * LanteaCraft.getProxy().getRenderMode()));
		 */
		OreTypes.TRINIUM.setIngotItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":trinium_ingot_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public Icon getIconFromDamage(int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getIngotItemTexture();
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < OreTypes.values().length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.lanteaOreIngot." + stack.getItemDamage();
	}

}
