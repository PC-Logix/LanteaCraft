package lc.blocks;

import java.util.List;

import lc.ResourceAccess;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
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

	public static enum DecorBlockTypes {
		LantSteel(1, "lantean_metal"), LantDecSteel(2, "lantean_decor"), GoaGold(3, "goauld_goldplain"), GoaDecGold(4,
				"goauld_golddecor");
		public final int idx;
		public final String resource;
		public IIcon icon;

		DecorBlockTypes(int i, String s) {
			idx = i;
			resource = s;
		}

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
}
