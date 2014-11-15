package lc.items;

import lc.common.base.LCItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBlockDecorative extends LCItemBlock {

	public ItemBlockDecorative(Block block) {
		super(block);
		setHasSubtypes(true);
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
		return "tile.lanteaDecor." + stack.getItemDamage();
	}

}
