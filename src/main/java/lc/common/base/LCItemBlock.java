package lc.common.base;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Internal base item-block stub.
 * 
 * @author AfterLifeLochie
 * 
 */
public class LCItemBlock extends ItemBlock {

	/** The block type */
	protected final LCBlock blockType;

	/**
	 * Create the item block
	 * 
	 * @param block
	 *            The block type
	 */
	public LCItemBlock(Block block) {
		this((LCBlock) block);
	}

	/**
	 * Create the item block
	 * 
	 * @param block
	 *            The LC block type
	 */
	public LCItemBlock(LCBlock block) {
		super(block);
		this.blockType = block;
		setHasSubtypes(block.isTyped);
	}

	@Override
	public int getMetadata(int data) {
		return (this.hasSubtypes) ? data : 0;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		return field_150939_a.getUnlocalizedName();
	}

}
