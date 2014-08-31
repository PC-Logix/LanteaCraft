package lc.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import lc.common.base.LCItemBlock;

public class ItemBlockLanteaOre extends LCItemBlock {

	public ItemBlockLanteaOre(Block block) {
		super(block);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return blockType.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.lanteaOre." + stack.getItemDamage();
	}

}
