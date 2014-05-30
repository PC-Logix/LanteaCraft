package pcl.lc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import pcl.common.base.RotationOrientedBlock;
import pcl.common.multiblock.EnumOrientations;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.multiblock.StargateMultiblock;
import pcl.lc.multiblock.StargatePart;
import pcl.lc.tileentity.TileEntityStargateBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateBase extends RotationOrientedBlock {
	protected Icon topAndBottomTexture;
	protected Icon frontTexture;
	protected Icon sideTexture;

	public BlockStargateBase(int id) {
		super(id, Material.rock);
		setHardness(50F);
		setResistance(2000F);
		setCreativeTab(CreativeTabs.tabMisc);
		setTickRandomly(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int blockID) {
		TileEntity host = world.getBlockTileEntity(x, y, z);
		if (host instanceof TileEntityStargateBase) {
			TileEntityStargateBase base = (TileEntityStargateBase) host;
			return (base.isConnected()) ? 15 : 0;
		}
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int blockID) {
		TileEntity host = world.getBlockTileEntity(x, y, z);
		if (host instanceof TileEntityStargateBase) {
			TileEntityStargateBase base = (TileEntityStargateBase) host;
			return (base.isConnected()) ? 15 : 0;
		}
		return 0;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.Render.blockStargateBaseRenderer != null)
			return LanteaCraft.Render.blockStargateBaseRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerIcons(IconRegister register) {
		topAndBottomTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateBlock_"
				+ LanteaCraft.getProxy().getRenderMode());
		frontTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateBase_front_"
				+ LanteaCraft.getProxy().getRenderMode());
		sideTexture = register.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateRing_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int data = Math.round((180 - player.rotationYaw) / 90) & 3;
		world.setBlockMetadataWithNotify(x, y, z, data, 0x3);
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileEntityStargateBase te = (TileEntityStargateBase) world.getBlockTileEntity(x, y, z);
		if (te != null && te.getAsStructure().isValid()) {
			player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.StargateBase.ordinal(), world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public Icon getIcon(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else if (side == 3)
			return frontTexture;
		else
			return sideTexture;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase)
			((TileEntityStargateBase) te).hostBlockDestroyed();
		super.breakBlock(world, x, y, z, id, data);
	}

	public void explode(World world, double x, double y, double z, double s) {
		if (true == true)
			return;

		TileEntity te = getTileEntity(world, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
		if (te != null && (te instanceof TileEntityStargateBase)) {
			TileEntityStargateBase gate = (TileEntityStargateBase) te;
			if (gate.getAsStructure() != null) {
				StargateMultiblock struct = gate.getAsStructure();
				for (MultiblockPart part : struct.getAllParts())
					if (part instanceof StargatePart) {
						Vector3 location = part.getVectorLoc();
						world.setBlockToAir((int) Math.floor(location.x), (int) Math.floor(location.y),
								(int) Math.floor(location.z));
					}
			}
		}
		world.newExplosion(null, x, y, z, (float) s, true, true);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateBase();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase)
			((TileEntityStargateBase) te).getAsStructure().invalidate();
	}

	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase)
			return ((TileEntityStargateBase) te).getAsStructure().isValid();
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z) {
		TileEntity tileof = block.getBlockTileEntity(x, y, z);
		if (tileof instanceof TileEntityStargateBase) {
			TileEntityStargateBase ring = (TileEntityStargateBase) tileof;
			if (ring.getAsStructure().getOrientation() != null) {
				EnumOrientations orientation = ring.getAsStructure().getOrientation();
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
			} else
				setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		} else
			setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}
}
