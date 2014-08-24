package lc.common.impl;

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
import lc.common.util.RegistrationHelper;

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

	public BlockItemDefinition(ComponentType ownerType, String defName, Class<? extends LCBlock> blockType,
			Class<? extends Item> itemType) {
		this.ownerType = ownerType;
		this.defName = defName;
		if (itemType.isInstance(LCItemBlock.class)) {
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

	public void init() {
		if (!RegistryContainer.instance.components().isEnabled(ownerType))
			return;
		if (blockType != null && itemBlockType != null)
			RegistrationHelper.registerBlock(blockType, itemBlockType, defName);
		else
			RegistrationHelper.registerItem(itemType, defName);
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
		return getTileType();
	}

	@Override
	public ItemStack getStackOf(int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
