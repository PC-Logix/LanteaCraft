package lc.common.impl.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import lc.api.components.ComponentType;
import lc.api.defs.ILanteaCraftDefinition;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;
import lc.core.LCRuntime;

public class BlockItemDefinition implements ILanteaCraftDefinition {

	private final ComponentType ownerType;
	private final String defName;

	private final Class<? extends LCBlock> blockType;
	private LCBlock blockObject;

	private final Class<? extends LCItem> itemType;
	private LCItem itemObject;

	private final Class<? extends LCItemBlock> itemBlockType;
	private LCItemBlock itemBlockObject;

	private Class<? extends LCTile> tileType;

	@SuppressWarnings("unchecked")
	public BlockItemDefinition(ComponentType ownerType, String defName, Class<? extends LCBlock> blockType,
			Class<? extends Item> itemType) {
		this.ownerType = ownerType;
		this.defName = defName;
		if (LCItemBlock.class.isAssignableFrom(itemType)) {
			this.itemType = null;
			this.itemBlockType = (Class<? extends LCItemBlock>) itemType;
		} else {
			this.itemType = (Class<? extends LCItem>) itemType;
			this.itemBlockType = null;
		}
		this.blockType = blockType;
	}

	public BlockItemDefinition setTileType(Class<? extends LCTile> type) {
		tileType = type;
		return this;
	}

	public ComponentType getComponentOwner() {
		return ownerType;
	}

	public void init(DefinitionRegistry registry) {
		if (!LCRuntime.runtime.registries().components().isEnabled(ownerType))
			return;
		if (blockType != null && itemBlockType != null) {
			blockObject = registry.registerBlock(blockType, itemBlockType, defName);
			blockObject.setProvidesTile(tileType);
			if (tileType != null) {
				String tileName = tileType.getSimpleName();
				if (tileName.startsWith("Tile"))
					tileName.replace("Tile", "tileEntity");
				registry.registerTileEntity(tileType, tileName);
			}
		} else if (itemType != null)
			itemObject = registry.registerItem(itemType, defName);
		LCRuntime.runtime.hints().provideHints(this);
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
		return (itemBlockType != null) ? itemBlockObject : itemObject;
	}

	@Override
	public Class<? extends TileEntity> getTileType() {
		return tileType;
	}

	@Override
	public ItemStack getStackOf(int size) {
		return (itemBlockType != null) ? new ItemStack(blockObject, size) : new ItemStack(itemObject, size);
	}

}
