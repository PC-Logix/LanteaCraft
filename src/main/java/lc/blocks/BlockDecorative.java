package lc.blocks;

import java.util.List;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.items.ItemBlockDecorative;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Decorative block implementation.
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "blockDecorative", type = ComponentType.DECOR, blockClass = BlockDecorative.class, itemBlockClass = ItemBlockDecorative.class)
public class BlockDecorative extends LCBlock {

	/**
	 * Decorative block type map
	 * 
	 * @author AfterLifeLochie
	 *
	 */
	public static enum DecorBlockTypes {
		/** Lantean steel */
		LantSteel(1, "lantean_metal"),
		/** Lantean patterned steel */
		LantDecSteel(2, "lantean_decor"),
		/** Goa'uld gold */
		GoaGold(3, "goauld_goldplain"),
		/** Goa'uld decorative gold */
		GoaDecGold(4, "goauld_golddecor");

		/** The type ID */
		public final int idx;
		/** The resource-name pattern */
		public final String resource;
		/** The IIcon icon resource */
		public IIcon icon;

		DecorBlockTypes(int i, String s) {
			idx = i;
			resource = s;
		}

		/**
		 * Derives a decorative type from a metadata value, or null if no type
		 * exists.
		 * 
		 * @param q
		 *            The metadata value
		 * @return The decorative type, or null if none exists
		 */
		public static DecorBlockTypes meta(int q) {
			for (DecorBlockTypes type : values())
				if (type.idx == q)
					return type;
			return null;
		}
	}

	private IIcon missing;

	/** Default constructor. */
	public BlockDecorative() {
		super(Material.ground);
		setHardness(10.0f);
		setHarvestLevel("pickaxe", 1);
		setOpaque(true);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		for (DecorBlockTypes type : DecorBlockTypes.values())
			type.icon = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
					type.resource));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		DecorBlockTypes type = DecorBlockTypes.meta(data);
		if (type == null)
			return missing;
		return type.icon;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (DecorBlockTypes type : DecorBlockTypes.values())
			list.add(new ItemStack(item, 1, type.idx));
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}
}
