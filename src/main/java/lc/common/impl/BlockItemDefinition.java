package lc.common.impl;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import lc.api.components.ComponentType;
import lc.api.defs.ILanteaCraftDefinition;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCTile;

public class BlockItemDefinition implements ILanteaCraftDefinition {

	private final ComponentType ownerType;

	private final Class<? extends LCBlock> blockType;
	private LCBlock blockObject;

	private final Class<? extends LCItem> itemType;
	private LCItem itemObject;

	private Class<? extends LCTile> tileType;

	public BlockItemDefinition(ComponentType ownerType, Class<? extends LCBlock> blockType,
			Class<? extends LCItem> itemType) {
		this.ownerType = ownerType;
		this.blockType = blockType;
		this.itemType = itemType;
	}

	public BlockItemDefinition setTileType(Class<? extends LCTile> type) {
		tileType = type;
		return this;
	}

	public ComponentType getType() {
		return ownerType;
	}

	public void init() {

	}

	@Override
	public Block getBlock() {
		return blockObject;
	}

	@Override
	public Item getItem() {
		return itemObject;
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
