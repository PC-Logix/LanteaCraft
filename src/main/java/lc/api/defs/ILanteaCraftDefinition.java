package lc.api.defs;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface ILanteaCraftDefinition {

	public abstract Block getBlock();

	public abstract Item getItem();

	public abstract Class<? extends TileEntity> getTileType();

	public abstract ItemStack getStackOf(int size);

}
