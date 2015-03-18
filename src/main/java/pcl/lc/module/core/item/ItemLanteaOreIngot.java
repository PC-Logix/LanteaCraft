package pcl.lc.module.core.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.core.OreTypes;
import pcl.lc.core.ResourceAccess;

public class ItemLanteaOreIngot extends Item {

	private IIcon missing;

	public ItemLanteaOreIngot() {
		super();
		setHasSubtypes(true);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		OreTypes.NAQUADAH.setIngotItemTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "naquadah_ingot")));
		OreTypes.NAQUADRIAH.setIngotItemTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "naquadriah_ingot")));
		OreTypes.TRINIUM.setIngotItemTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "trinium_ingot")));
	}

	@Override
	public IIcon getIconFromDamage(int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getIngotItemTexture();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < OreTypes.values().length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.lanteaOreIngot." + stack.getItemDamage();
	}

}
