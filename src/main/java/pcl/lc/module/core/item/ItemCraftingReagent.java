package pcl.lc.module.core.item;

import java.util.List;

import pcl.lc.LanteaCraft;
import pcl.lc.core.ResourceAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemCraftingReagent extends Item {

	private IIcon missing;

	public static enum ReagentList {
		BROKENITEM, BLANKCRYSTAL, CORECRYSTAL, CONTROLCRYSTAL, IRONPLATE, NAQUADAHPLATE, TRINIUMPLATE;
		public IIcon icon;
	}

	public ItemCraftingReagent() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		for (ReagentList reagent : ReagentList.values())
			if (reagent == ReagentList.BROKENITEM)
				reagent.icon = register
						.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:reagent_broken"));
			else
				reagent.icon = register.registerIcon(ResourceAccess.formatResourceName(
						"${ASSET_KEY}:reagent_%s_${TEX_QUALITY}", reagent.name().toLowerCase()));
	}

	@Override
	public IIcon getIconFromDamage(int data) {
		if (data > ReagentList.values().length)
			return missing;
		return ReagentList.values()[data].icon;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 1; i < ReagentList.values().length; i++)
			par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.craftingReagent." + stack.getItemDamage();
	}

}
