package lc.common.impl.registry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import lc.api.components.ComponentType;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IDefinitionReference;

public class EntityDefinition implements IContainerDefinition {

	private final ComponentType ownerType;
	private final String defName;

	private final Class<? extends Entity> entityType;

	public EntityDefinition(ComponentType ownerType, String defName, Class<? extends Entity> entityType) {
		this.ownerType = ownerType;
		this.defName = defName;
		this.entityType = entityType;
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

	@Override
	public String getName() {
		return defName;
	}

	@Override
	public Block getBlock() {
		return null;
	}

	@Override
	public Item getItem() {
		return null;
	}

	@Override
	public Class<? extends TileEntity> getTileType() {
		return null;
	}

	@Override
	public Class<? extends Entity> getEntityType() {
		return entityType;
	}

	@Override
	public ItemStack getStackOf(int size) {
		return null;
	}

	public ComponentType getComponentOwner() {
		return ownerType;
	}

	public void init(DefinitionRegistry registry) {
		
	}

}
