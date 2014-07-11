package pcl.lc.api.internal;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public abstract class HookedModelBase extends ModelBase {
	public abstract void preInventory(Block block, int meta, int model, RenderBlocks rbx);

	public abstract void postInventory(Block block, int meta, int model, RenderBlocks rbx);

	public abstract void preWorld(IBlockAccess world, int x, int y, int z, Block block, int renderId, RenderBlocks rbx);

	public abstract void postWorld(IBlockAccess world, int x, int y, int z, Block block, int renderId, RenderBlocks rbx);

	public abstract void preTile(TileEntity tile, double x, double y, double z, float scale);

	public abstract void postTile(TileEntity tile, double x, double y, double z, float scale);
}