package lc.common.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class LCBlock extends BlockContainer {

	/** If the block instance is opaque */
	protected boolean opaque = false;
	/** If the block instance has more than one type */
	protected boolean typed = false;
	/** If the block instance has an inventory */
	protected boolean inventory = false;
	/** If the block instance observes rotation */
	protected boolean rotation = false;
	/** The type of tile entity for this block */
	protected Class<? extends LCTile> tileType;

	public LCBlock(Material material) {
		super(material);
	}

	public LCBlock setOpaque(boolean b) {
		opaque = b;
		return this;
	}

	public void setProvidesTile(Class<? extends LCTile> tile) {
		tileType = tile;
	}

	public LCBlock setProvidesInventory(boolean b) {
		inventory = b;
		return this;
	}

	public LCBlock setCanRotate(boolean b) {
		rotation = b;
		return this;
	}

	public LCBlock setProvidesTypes(boolean b) {
		typed = b;
		return this;
	}

	public boolean canRotate() {
		return rotation;
	}

	public ForgeDirection getRotation(int data, int ord) {
		int rotation = (data & (0x0F << ord)) >> ord;
		return ForgeDirection.getOrientation(rotation);
	}

	public int setRotation(int data, ForgeDirection direction, int ord) {
		int rotation = direction.ordinal() & 0x0F;
		return (data | (rotation << ord));
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
