/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.defs;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
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
	 * Fetches the unique name of this definition in the runtime.
	 *
	 * @return The name of this definition
	 */
	public abstract String getName();

	/**
	 * Fetches the block instance for this definition. If the definition has no
	 * block or has not been initialized (due to an error or being disabled),
	 * this method must return null.
	 *
	 * @return The block instance of this definition, or null
	 */
	public abstract Block getBlock();

	/**
	 * Fetches the item instance for this definition. If the definition has no
	 * item or has not been initialized (due to an error or being disabled),
	 * this method must return null.
	 *
	 * @return The item instance of this definition, or null
	 */
	public abstract Item getItem();

	/**
	 * Fetches the tile entity class for this definition. If the definition has
	 * no tile entity (because it is not a block or does not require one), this
	 * method must return null.
	 *
	 * @return The class of tile entity for this block definition, or null
	 */
	public abstract Class<? extends TileEntity> getTileType();

	/**
	 * Fetches the entity class for this definition. If the definition has no
	 * entity (because it is not an entity or does not require one), this method
	 * must return null.
	 *
	 * @return The class of entity for this block definition, or null
	 */
	public abstract Class<? extends Entity> getEntityType();

	/**
	 * Create a stack of a specified size of the item or item-block. If the
	 * definition has not been initialized (due to an error or being disabled),
	 * this method must return null.
	 *
	 * @param size
	 *            The size (quantity) in the stack.
	 * @return An item stack of this definitions' item.
	 */
	public abstract ItemStack getStackOf(int size);

}
