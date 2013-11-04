//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block item
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.items;

import gcewing.sg.SGCraft.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class SGRingItem extends ItemBlock {

	public SGRingItem(int id) {
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
		String result = subItemName(stack.getItemDamage());
		// System.out.printf("SGRingItem.getUnlocalizedName: %s --> %s\n",
		// stack, result);
		return result;
	}

	public static String subItemName(int i) {
		return "tile.gcewing.sg.stargateRing." + i;
	}

}
