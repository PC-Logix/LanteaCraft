package lc.common.impl.registry;

import lc.LCRuntime;
import lc.api.components.ComponentType;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IDefinitionReference;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;
import lc.common.util.Tracer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Implementation of definitions for items and blocks
 *
 * @author AfterLifeLochie
 *
 */
public class BlockItemDefinition implements IContainerDefinition {

	private final ComponentType ownerType;
	private final String defName;

	private final Class<? extends LCBlock> blockType;
	private LCBlock blockObject;

	private final Class<? extends LCItem> itemType;
	private Item itemObject;

	private final Class<? extends LCItemBlock> itemBlockType;
	private ItemBlock itemBlockObject;

	private Class<? extends LCTile> tileType;

	/**
	 * Create a new block or item definition
	 *
	 * @param ownerType
	 *            The type of owner
	 * @param defName
	 *            The definition name
	 * @param blockType
	 *            The block class
	 * @param itemType
	 *            The item class
	 */
	@SuppressWarnings("unchecked")
	public BlockItemDefinition(ComponentType ownerType, String defName, Class<? extends LCBlock> blockType,
			Class<? extends Item> itemType) {
		this.ownerType = ownerType;
		this.defName = defName;
		if (LCItemBlock.class.isAssignableFrom(itemType)) {
			this.itemType = null;
			itemBlockType = (Class<? extends LCItemBlock>) itemType;
		} else {
			this.itemType = (Class<? extends LCItem>) itemType;
			itemBlockType = null;
		}
		this.blockType = blockType;
	}

	/**
	 * Set the tile provider type
	 *
	 * @param type
	 *            The tile type
	 * @return This definition.
	 */
	public BlockItemDefinition setTileType(Class<? extends LCTile> type) {
		tileType = type;
		return this;
	}

	/**
	 * Get the component-type owner of this definition object.
	 *
	 * @return The component-type owner of this definition.
	 */
	public ComponentType getComponentOwner() {
		return ownerType;
	}

	/**
	 * Initialize the definition with real objects as per the definition
	 * descriptor.
	 *
	 * @param registry
	 *            The definition registry to apply changes to.
	 */
	public void init(DefinitionRegistry registry) {
		if (!LCRuntime.runtime.registries().components().isEnabled(ownerType))
			return;
		Tracer.begin(this);
		if (blockType != null && itemBlockType != null) {
			blockObject = registry.registerBlock(blockType, itemBlockType, defName, ownerType);
			blockObject.setProvidesTile(tileType);
			if (tileType != null) {
				String tileName = tileType.getSimpleName();
				if (tileName.startsWith("Tile"))
					tileName.replace("Tile", "tileEntity");
				registry.registerTileEntity(tileType, tileName);
			}
		} else if (itemType != null)
			itemObject = registry.registerItem(itemType, defName, ownerType);
		LCRuntime.runtime.hints().provideHints(this);
		Tracer.end();
	}

	@Override
	public String getName() {
		return defName;
	}

	@Override
	public Block getBlock() {
		return blockObject;
	}

	@Override
	public Item getItem() {
		return itemBlockType != null ? itemBlockObject : itemObject;
	}

	@Override
	public Class<? extends TileEntity> getTileType() {
		return tileType;
	}

	@Override
	public Class<? extends Entity> getEntityType() {
		return null;
	}

	@Override
	public ItemStack getStackOf(int size) {
		return itemBlockType != null ? new ItemStack(blockObject, size) : new ItemStack(itemObject, size);
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

}
