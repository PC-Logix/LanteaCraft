package lc.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import lc.ResourceAccess;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.world.OreType;
import lc.common.base.LCBlock;
import lc.items.ItemBlockObelisk;

@Definition(name = "blockObelisk", type = ComponentType.DECOR, blockClass = BlockObelisk.class, itemBlockClass = ItemBlockObelisk.class)
public class BlockObelisk extends LCBlock {

	private IIcon missing;

	public BlockObelisk() {
		super(Material.ground);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (data > OreType.values().length)
			return missing;
		return OreType.values()[data].getItemAsBlockTexture();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < OreType.values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}

}
