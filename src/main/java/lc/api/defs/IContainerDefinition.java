package lc.api.defs;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Game definition container interface
 *
 * @author AfterLifeLochie
 *
 */
public interface IContainerDefinition extends IGameDef {

	/**
	 * @return The name of this definition
	 */
	public abstract String getName();

	/**
	 * @return The block instance of this definition, or null
	 */
	public abstract Block getBlock();

	/**
	 * @return The item instance of this definition, or null
	 */
	public abstract Item getItem();

	/**
	 * @return The class of tile entity for this block definition, or null
	 */
	public abstract Class<? extends TileEntity> getTileType();

	/**
	 * Create a stack of a specified size of the item or item-block.
	 *
	 * @param size
	 *            The size (quantity) in the stack.
	 * @return An item stack of this definitions' item.
	 */
	public abstract ItemStack getStackOf(int size);

}
