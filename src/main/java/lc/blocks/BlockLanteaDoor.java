package lc.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import lc.LCRuntime;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.items.ItemLanteaDoor;
import lc.tiles.TileLanteaDoor;

/**
 * LanteaCraft door block implementation
 * 
 * @author AfterLifeLochie
 *
 */
@Definition(name = "lanteaDoor", type = ComponentType.DECOR, blockClass = BlockLanteaDoor.class, itemBlockClass = ItemLanteaDoor.class, tileClass = TileLanteaDoor.class)
public class BlockLanteaDoor extends LCBlock {

	private static final int blockCount = 2;

	/** Default constructor */
	public BlockLanteaDoor() {
		super(Material.ground);
		setHardness(5.0f);
		setOpaque(false).setProvidesInventory(false).setProvidesTypes(true).setCanRotate(true);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World w, int x, int y, int z) {
		if (w.getBlock(x, y - 1, z) == this)
			return true;
		if (!World.doesBlockHaveSolidTopSurface(w, x, y - 1, z))
			return false;
		return w.isAirBlock(x, y, z) || super.canPlaceBlockAt(w, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int par6, float par7, float par8,
			float par9) {
		if (world.isRemote)
			return true;
		TileLanteaDoor doorTile = (TileLanteaDoor) world.getTileEntity(x, y, z);
		if (doorTile == null)
			return true;
		doorTile.openOrCloseDoor();
		return true;
	}

	public Item getItemDropped(int metadata, Random random, int fortune) {
		return LCRuntime.runtime.blocks().lanteaDoor.getItem();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < blockCount; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	/**
	 * Set the bounds of the block
	 * 
	 * @param aabb
	 *            The axis-aligned bounding box to update
	 * @return The updated AABB
	 */
	protected AxisAlignedBB setBlockBounds(AxisAlignedBB aabb) {
		if (aabb == null)
			aabb = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		setBlockBounds((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY,
				(float) aabb.maxZ);
		return aabb;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileLanteaDoor te = (TileLanteaDoor) world.getTileEntity(x, y, z);
		if (te == null)
			return;
		setBlockBounds(te.getBoundingBox(true));
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		TileLanteaDoor te = (TileLanteaDoor) world.getTileEntity(x, y, z);
		if (te == null)
			return AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		AxisAlignedBB aabb = te.getBoundingBox(true);
		if (aabb == null) {
			return AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		}
		return aabb.offset(x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileLanteaDoor te = (TileLanteaDoor) world.getTileEntity(x, y, z);
		if (te == null)
			return null;
		AxisAlignedBB aabb = te.getBoundingBox(true);
		if (aabb == null)
			return null;
		return setBlockBounds(aabb.offset(x, y, z));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (!world.isRemote) {
			int strength = world.getStrongestIndirectPower(x, y, z);
			TileLanteaDoor te = (TileLanteaDoor) world.getTileEntity(x, y, z);
			if (te != null)
				te.setRedstoneState(strength);
		}

	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

}
