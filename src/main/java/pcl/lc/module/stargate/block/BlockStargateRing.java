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
import pcl.lc.api.EnumStargateType;
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

	private static final int blockMutex = 2;
	private static final int blockCount = EnumStargateType.values().length * blockMutex;

	private static final int blockCraftableMutex = 1;
	private static final int blockCraftableCount = blockCount;

	IIcon topAndBottomTexture[] = new IIcon[EnumStargateType.values().length];
	IIcon sideTextures[][] = new IIcon[EnumStargateType.values().length][blockMutex];

	public BlockStargateRing() {
		super(Material.ground);
		setHardness(3F);
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
		EnumStargateType[] types = EnumStargateType.values();
		for (EnumStargateType typeof : types) {
			StringBuilder typename = new StringBuilder();
			typename.append("stargate_%s");
			if (typeof.getSuffix() != null && typeof.getSuffix().length() > 0)
				typename.append("_").append(typeof.getSuffix());
			topAndBottomTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "block")));
			sideTextures[typeof.ordinal()][0] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "ring")));
			sideTextures[typeof.ordinal()][1] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "chevron")));
		}
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

	public int getBaseType(int metadata) {
		return (int) Math.floor(metadata / blockMutex);
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@Override
	public IIcon getIcon(int side, int data) {
		int typeof = getBaseType(data);
		if (side <= 1)
			return topAndBottomTexture[typeof];
		else
			return sideTextures[typeof][data % blockMutex];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < blockCraftableCount; i += blockCraftableMutex)
			list.add(new ItemStack(item, 1, i));
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
