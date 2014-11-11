package lc.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.core.ResourceAccess;
import lc.items.ItemBlockDecorative;

@Definition(name = "blockDecorative", type = ComponentType.DECOR, blockClass = BlockDecorative.class, itemBlockClass = ItemBlockDecorative.class)
public class BlockDecorative extends LCBlock {

	private enum BlockTypes {
		LantSteel(1, "lantean_metal"), LantDecSteel(2, "lantean_decor"), GoaGold(
				3, "goauld_goldplain"), GoaDecGold(4, "goauld_golddecor");
		public final int idx;
		public final String resource;
		public IIcon icon;

		BlockTypes(int i, String s) {
			this.idx = i;
			this.resource = s;
		}

		public static BlockTypes meta(int q) {
			for (BlockTypes type : values())
				if (type.idx == q)
					return type;
			return null;
		}
	}

	private IIcon missing;

	public BlockDecorative() {
		super(Material.ground);
		setHarvestLevel("pickaxe", 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:missing"));
		for (BlockTypes type : BlockTypes.values())
			type.icon = register.registerIcon(ResourceAccess
					.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
							type.resource));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		BlockTypes type = BlockTypes.meta(data);
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
		for (BlockTypes type : BlockTypes.values())
			list.add(new ItemStack(item, 1, type.idx));
	}
}
