package lc.common.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class LCBlock extends BlockContainer {

	/** If the block instance is opaque */
	protected boolean opaque = false;
	/** If the block instance has more than one type */
	protected boolean typed = false;
	/** If the block instance has an inventory */
	protected boolean inventory = false;
	/** The type of tile entity for this block */
	protected Class<? extends LCTile> tileType;

	protected LCBlock(Material material) {
		super(material);
	}

	protected void setOpaque(boolean b) {
		opaque = b;
	}

	public void setProvidesTile(Class<? extends LCTile> tile) {
		tileType = tile;
	}

	protected void setProvidesInventory(boolean b) {
		inventory = b;
	}

	protected void setProvidesTypes(boolean b) {
		typed = b;
	}

	@Override
	public final boolean isOpaqueCube() {
		return opaque;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return opaque;
	}

	@Override
	public final TileEntity createNewTileEntity(World world, int data) {
		if (tileType != null)
			try {
				return (TileEntity) tileType.newInstance();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		return null;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (tileType != null)
			((LCTile) world.getTileEntity(x, y, z)).blockPlaced();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block a, int b) {
		LCTile tile = (LCTile) world.getTileEntity(x, y, z);
		if (tile != null)
			((LCTile) world.getTileEntity(x, y, z)).blockBroken();
		super.breakBlock(world, x, y, z, a, b);
	}

}
