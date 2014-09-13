package lc.common.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class LCBlock extends BlockContainer {

	/** Rotation direction map across Y-axis */
	protected static final ForgeDirection[] directions = new ForgeDirection[] { ForgeDirection.SOUTH,
			ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST };

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
	/** The renderer ID for this block */
	protected int rendererIdx = 0;

	public LCBlock(Material material) {
		super(material);
	}

	public LCBlock setOpaque(boolean b) {
		opaque = b;
		return this;
	}

	public LCBlock setRenderer(int i) {
		rendererIdx = i;
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

	public ForgeDirection getRotation(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null || !(tile instanceof LCTile))
			return null;
		return ((LCTile) tile).getRotation();
	}

	public void setRotation(World world, int x, int y, int z, ForgeDirection direction) {
		if (world.isRemote)
			return;
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null || !(tile instanceof LCTile))
			return;
		((LCTile) tile).setRotation(direction);
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
	public int getRenderType() {
		return rendererIdx;
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		if (canRotate() && !world.isRemote) {
			int heading = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			setRotation(world, x, y, z, directions[heading]);
			world.markBlockForUpdate(x, y, z);
		}
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
