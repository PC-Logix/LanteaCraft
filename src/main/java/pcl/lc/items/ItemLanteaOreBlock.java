package pcl.lc.items;

import pcl.lc.LanteaCraft.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemLanteaOreBlock extends ItemBlock {

	public ItemLanteaOreBlock(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return Blocks.lanteaOre.getIcon(0, i);
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
