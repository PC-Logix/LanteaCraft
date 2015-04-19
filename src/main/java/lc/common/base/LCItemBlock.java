package lc.common.base;

import lc.common.configuration.IConfigure;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Internal base item-block stub.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCItemBlock extends ItemBlock implements IConfigure {

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
		blockType = block;
		setHasSubtypes(block.isTyped);
	}

	@Override
	public int getMetadata(int data) {
		return hasSubtypes ? data : 0;
	}

	@Override
	public String getUnlocalizedName(ItemStack is) {
		return field_150939_a.getUnlocalizedName();
	}

}
