package pcl.lc.module.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.module.ModuleCore;

public class ItemBlockOfLanteaOre extends ItemBlock {

	public ItemBlockOfLanteaOre(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public IIcon getIconFromDamage(int i) {
		return ModuleCore.Blocks.lanteaOreAsBlock.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "tile.lanteaOreIngotBlock." + stack.getItemDamage();
	}

}
