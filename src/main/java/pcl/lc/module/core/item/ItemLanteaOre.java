package pcl.lc.module.core.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;

public class ItemLanteaOre extends Item {
	private IIcon missing;

	public ItemLanteaOre() {
		super();
		setHasSubtypes(true);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadah_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.NAQUADRIAH.setItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadriah_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.TRINIUM.setItemTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":trinium_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public IIcon getIconFromDamage(int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getItemTexture();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < OreTypes.values().length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.lanteaOre." + stack.getItemDamage();
	}
}
