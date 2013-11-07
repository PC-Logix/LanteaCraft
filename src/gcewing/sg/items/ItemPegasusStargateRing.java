package gcewing.sg.items;

import gcewing.sg.SGCraft.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemPegasusStargateRing extends ItemStargateRing {

	public ItemPegasusStargateRing(int id) {
		super(id);
		setHasSubtypes(true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Icon getIconFromDamage(int i) {
		return Blocks.sgPegasusRingBlock.getIcon(0, i);
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
