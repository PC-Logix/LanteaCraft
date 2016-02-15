package lc.common.base;

import lc.api.event.IBlockEventHandler;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.rendering.IEntityRenderInfo;
import lc.api.rendering.IRenderInfo;
import lc.api.rendering.ITileRenderInfo;
import lc.common.LCLog;
import lc.common.configuration.IConfigure;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Generic block implementation
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCBlock extends BlockContainer implements IRenderInfo, IConfigure {

	/** Rotation direction map across Y-axis */
	protected static final EnumFacing[] directions = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.WEST,
			EnumFacing.NORTH, EnumFacing.EAST };

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
	 * @param pos
	 *            The BlockPos
	 * @return The rotation element
	 */
	public EnumFacing getRotation(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null || !(tile instanceof LCTile))
			return null;
		return ((LCTile) tile).getRotation();
	}

	/**
	 * Set the block's rotation
	 *
	 * @param world
	 *            The world object
	 * @param pos
	 *            The BlockPos
	 * @param direction
	 *            The rotation element
	 */
	public void setRotation(World world, BlockPos pos, EnumFacing direction) {
		if (world.isRemote)
			return;
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null || !(tile instanceof LCTile))
			return;
		((LCTile) tile).setRotation(direction);
	}

	@Override
	public final boolean isOpaqueCube() {
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
				return tileType.newInstance();
			} catch (Throwable t) {
				LCLog.fatal("Couldn't create new instance of tile type.", t);
				if (t instanceof Error)
					throw (Error) t;
				else
					throw new RuntimeException("Couldn't create new instance of tile type.", t);
			}
		return null;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, player, stack);
		if (canRotate() && !world.isRemote) {
			int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
			setRotation(world, pos, directions[heading]);
			world.markBlockForUpdate(pos);
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		if (tileType != null) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof IBlockEventHandler)
				((IBlockEventHandler) tile).blockPlaced();
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof IBlockEventHandler)
			((IBlockEventHandler) tile).blockBroken();
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof IBlockEventHandler)
			((IBlockEventHandler) tile).neighborChanged();
		super.onNeighborBlockChange(world, pos, state, neighborBlock);
	};

	@Override
	public IBlockRenderInfo renderInfoBlock() {
		return null;
	}

	@Override
	public ITileRenderInfo renderInfoTile() {
		return null;
	}

	@Override
	public IEntityRenderInfo renderInfoEntity() {
		return null;
	}

}
