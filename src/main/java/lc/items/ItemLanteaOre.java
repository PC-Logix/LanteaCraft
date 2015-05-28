package lc.items;

import java.util.List;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.world.OreType;
import lc.common.base.LCItem;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Ore block implementation.
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "lanteaOre", type = ComponentType.CORE, itemClass = ItemLanteaOre.class)
public class ItemLanteaOre extends LCItem {
	private IIcon missing;

	/** Default constructor */
	public ItemLanteaOre() {
		super();
		setHasSubtypes(true);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		OreType.NAQUADAH.setItemTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "naquadah")));
		OreType.NAQUADRIAH.setItemTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "naquadriah")));
		OreType.TRINIUM.setItemTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "trinium")));
	}

	@Override
	public IIcon getIconFromDamage(int data) {
		if (data > OreType.values().length)
			return missing;
		return OreType.values()[data].getItemTexture();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < OreType.values().length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.lanteaOre." + stack.getItemDamage();
	}
}
