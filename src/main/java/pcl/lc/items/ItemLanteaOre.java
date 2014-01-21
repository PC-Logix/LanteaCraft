package pcl.lc.items;

import java.util.List;

import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemLanteaOre extends Item {
	private Icon missing;

	public ItemLanteaOre(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public void registerIcons(IconRegister register) {
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadah_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.NAQAHDRIAH.setItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naqahdriah_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.TRINIUM.setItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":trinium_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public Icon getIconFromDamage(int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getItemTexture();
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < OreTypes.values().length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.lanteaOre." + stack.getItemDamage();
	}
}
