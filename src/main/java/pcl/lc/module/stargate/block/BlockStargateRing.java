package pcl.lc.module.stargate.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericContainerBlock;
import pcl.lc.base.multiblock.EnumOrientations;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.stargate.StargateMultiblock;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.module.stargate.tile.TileStargateRing;
import pcl.lc.util.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateRing extends GenericContainerBlock {

	static final int numSubBlocks = 2;
	public static final int subBlockMask = 0x1;
	IIcon topAndBottomTexture;
	IIcon sideTextures[] = new IIcon[numSubBlocks];

	public BlockStargateRing() {
		super(Material.ground);
		setHardness(50F);
		setResistance(2000F);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	@Override
	public int getRenderType() {
		if (ModuleStargates.Render.blockStargateRingRenderer != null)
			return ModuleStargates.Render.blockStargateRingRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		topAndBottomTexture = register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "stargateBlock"));
		sideTextures[0] = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"stargateRing"));
		sideTextures[1] = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"stargateChevron"));
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public int damageDropped(int data) {
		return data;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileStargateRing te = (TileStargateRing) world.getTileEntity(x, y, z);
		if (te.getAsPart().isMerged()) {
			Vector3 base = te.getAsPart().findHostMultiblock(false).getLocation();
			Block block = world.getBlock(base.floorX(), base.floorY(), base.floorZ());
			if (block instanceof BlockStargateBase)
				block.onBlockActivated(world, base.floorX(), base.floorY(), base.floorZ(), player, side, cx, cy, cz);
			return true;
		}
		return false;
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else
			return sideTextures[data & subBlockMask];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < numSubBlocks; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileStargateRing te = (TileStargateRing) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int data) {
		TileStargateRing te = (TileStargateRing) getTileEntity(world, x, y, z);
		if (te != null) {
			te.flagDirty();
			if (te.getAsPart().findHostMultiblock(false) != null) {
				TileEntity host = te.getAsPart().findHostMultiblock(false).getTileEntity();
				if (host instanceof TileStargateBase)
					((TileStargateBase) host).hostBlockDestroyed();
			}
		}
		super.breakBlock(world, x, y, z, block, data);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileStargateRing();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z) {
		TileEntity tileof = block.getTileEntity(x, y, z);
		if (tileof instanceof TileStargateRing) {
			TileStargateRing ring = (TileStargateRing) tileof;
			if (ring.getAsPart() != null && ring.getAsPart().isMerged()) {
				StargateMultiblock master = (StargateMultiblock) ring.getAsPart().findHostMultiblock(false);
				EnumOrientations orientation = master.getOrientation();
				if (orientation != null)
					switch (orientation) {
					case NORTH:
					case SOUTH:
					case NORTH_SOUTH:
						setBlockBounds(0.35f, 0.0f, 0.0f, 0.65f, 1.0f, 1.0f);
						break;
					case EAST:
					case WEST:
					case EAST_WEST:
						setBlockBounds(0.0f, 0.0f, 0.35f, 1.0f, 1.0f, 0.65f);
						break;
					default:
						setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
					}
				else
					setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			} else
				setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		} else
			setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

}
