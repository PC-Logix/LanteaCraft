package pcl.lc.module.decor.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.module.ModuleDecor.Blocks;

public class ItemLanteaDecorGlass extends ItemBlock {

	public ItemLanteaDecorGlass(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return Blocks.glassDecorBlock.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.lanteaGlassDecor." + stack.getItemDamage();
	}

}