package lc.items;

import java.util.List;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCItem;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Crafting reagent item implementation.
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "lanteaCraftingReagent", type = ComponentType.CORE, itemClass = ItemCraftingReagent.class)
public class ItemCraftingReagent extends LCItem {

	private IIcon missing;

	/**
	 * Enumeration of all reagent item types.
	 *
	 * @author AfterLifeLochie
	 *
	 */
	public static enum ReagentList {
		/** Broken item */
		BROKENITEM,
		/** Blank crystal */
		BLANKCRYSTAL,
		/** Core crystal */
		CORECRYSTAL,
		/** Control crystal */
		CONTROLCRYSTAL,
		/** Iron plate */
		IRONPLATE,
		/** Naquadah plate */
		NAQUADAHPLATE,
		/** Trninium plate */
		TRINIUMPLATE;

		/** The icon for the reagent */
		public IIcon icon;
	}

	/** Default constructor */
	public ItemCraftingReagent() {
		super();
		setHasSubtypes(true);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		for (ReagentList reagent : ReagentList.values())
			if (reagent == ReagentList.BROKENITEM)
				reagent.icon = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:reagent_broken"));
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