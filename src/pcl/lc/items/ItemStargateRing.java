package pcl.lc.items;

import pcl.lc.LanteaCraft.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemStargateRing extends ItemBlock {

	public ItemStargateRing(int id) {
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return Blocks.sgRingBlock.getIcon(0, i);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return subItemName(stack.getItemDamage());
	}

	public static String subItemName(int i) {
		return "tile.gcewing.sg.stargateRing." + i;
	}

}
