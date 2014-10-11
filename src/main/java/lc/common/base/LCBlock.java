package lc.common.base;

import lc.api.event.IBlockEventHandler;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.rendering.IEntityRenderInfo;
import lc.api.rendering.IRenderInfo;
import lc.api.rendering.ITileRenderInfo;
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

/**
 * Generic block implementation
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class LCBlock extends BlockContainer implements IRenderInfo {

	/** Rotation direction map across Y-axis */
	protected static final ForgeDirection[] directions = new ForgeDirection[] { ForgeDirection.SOUTH,
			ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST };

	/** If the block instance is opaque */
	protected boolean isOpaque = false;
	/** If the block instance has more than one type */
	protected boolean isTyped = false;
	/** If the block instance has an hasInventory */
	protected boolean hasInventory = false;
	/** If the block instance observes canRotate */
	protected boolean canRotate = false;
	/** The type of tile entity for this block */
	protected Class<? extends LCTile> tileType;
	/** The renderer ID for this block */
	protected int rendererIdx = 0;

	/**
	 * Block with material constructor
	 * 
	 * @param material
	 *            The material to set
	 */
	public LCBlock(Material material) {
		super(material);
	}

	/**
	 * Set opaqueness of the block
	 * 
	 * @param b
	 *            Is opaque?
	 * @return This block
	 */
	public LCBlock setOpaque(boolean b) {
		isOpaque = b;
		return this;
	}

	/**
	 * Set renderer of the block
	 * 
	 * @param i
	 *            Render ID
	 * @return This block
	 */
	public LCBlock setRenderer(int i) {
		rendererIdx = i;
		return this;
	}

	/**
	 * Set tile provided of the block
	 * 
	 * @param tile
	 *            The tile class
	 */
	public void setProvidesTile(Class<? extends LCTile> tile) {
		tileType = tile;
	}

	/**
	 * Get the tile provided from the block
	 * 
	 * @return The tile class
	 */
	public Class<? extends LCTile> getTileType() {
		return tileType;
	}

	/**
	 * Set inventory provider state of the block
	 * 
	 * @param b
	 *            Is inventory provider?
	 * @return This block
	 */
	public LCBlock setProvidesInventory(boolean b) {
		hasInventory = b;
		return this;
	}

	/**
	 * Set rotatedness of the block
	 * 
	 * @param b
	 *            Is rotate-able?
	 * @return This block
	 */
	public LCBlock setCanRotate(boolean b) {
		canRotate = b;
		return this;
	}

	/**
	 * Set type provider mode of the block
	 * 
	 * @param b
	 *            Is providing types?
	 * @return This block
	 */
	public LCBlock setProvidesTypes(boolean b) {
		isTyped = b;
		return this;
	}

	/**
	 * Get rotation capabilities
	 * 
	 * @return If this block can rotate
	 */
	public boolean canRotate() {
		return canRotate;
	}

	/**
	 * Get the blocks' rotation
	 * 
	 * @param world
	 *            The world object
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @return The rotation element
	 */
	public ForgeDirection getRotation(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null || !(tile instanceof LCTile))
			return null;
		return ((LCTile) tile).getRotation();
	}

	/**
	 * Set the block's rotation
	 * 
	 * @param world
	 *            The world object
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @param direction
	 *            The rotation element
	 */
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
		return isOpaque;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return isOpaque;
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
		if (tileType != null) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof IBlockEventHandler)
				((IBlockEventHandler) tile).blockPlaced();
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block a, int b) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof IBlockEventHandler)
			((IBlockEventHandler) tile).blockBroken();
		super.breakBlock(world, x, y, z, a, b);
	}

	@Override
	public IBlockRenderInfo block() {
		return null;
	}

	@Override
	public ITileRenderInfo tile() {
		return null;
	}

	@Override
	public IEntityRenderInfo entity() {
		return null;
	}

}
