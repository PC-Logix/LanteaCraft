package pcl.lc.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import pcl.lc.LanteaCraft.Blocks;

public class ItemBlockOfLanteaOre extends ItemBlock {

	public ItemBlockOfLanteaOre(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return Blocks.lanteaOreAsBlock.getIcon(0, i);
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
