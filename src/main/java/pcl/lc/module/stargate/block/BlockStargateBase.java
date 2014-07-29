package pcl.lc.module.stargate.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateType;
import pcl.lc.base.RotationOrientedBlock;
import pcl.lc.base.multiblock.EnumOrientations;
import pcl.lc.base.multiblock.MultiblockPart;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.stargate.StargateMultiblock;
import pcl.lc.module.stargate.StargatePart;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.util.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateBase extends RotationOrientedBlock {

	private static final int blockMutex = 4;
	private static final int blockCount = EnumStargateType.values().length * blockMutex;

	private static final int blockCraftableMutex = 4;
	private static final int blockCraftableCount = blockCount;

	protected IIcon topAndBottomTexture[] = new IIcon[EnumStargateType.values().length];
	protected IIcon frontTexture[] = new IIcon[EnumStargateType.values().length];
	protected IIcon sideTexture[] = new IIcon[EnumStargateType.values().length];

	public BlockStargateBase() {
		super(Material.rock);
		setHardness(50F);
		setResistance(2000F);
		setCreativeTab(CreativeTabs.tabMisc);
		setTickRandomly(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int blockID) {
		TileEntity host = world.getTileEntity(x, y, z);
		if (host instanceof TileStargateBase) {
			TileStargateBase base = (TileStargateBase) host;
			return (base.isConnected()) ? 15 : 0;
		}
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int blockID) {
		TileEntity host = world.getTileEntity(x, y, z);
		if (host instanceof TileStargateBase) {
			TileStargateBase base = (TileStargateBase) host;
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
		if (ModuleStargates.Render.blockStargateBaseRenderer != null)
			return ModuleStargates.Render.blockStargateBaseRenderer.renderID;
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
			frontTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "base_front")));
			sideTexture[typeof.ordinal()] = register.registerIcon(ResourceAccess.formatResourceName(
					"${ASSET_KEY}:%s_${TEX_QUALITY}", String.format(typename.toString(), "ring")));
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int rotation = Math.round((180 - player.rotationYaw) / 90) & 3;
		System.out.println(rotation);
		int data = ((int) Math.floor(stack.getItemDamage() / blockMutex) * blockMutex) + rotation;
		System.out.println(data);
		world.setBlockMetadataWithNotify(x, y, z, data, 0x3);
		TileStargateBase te = (TileStargateBase) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public int damageDropped(int metadata) {
		return (int) Math.floor(metadata / blockMutex);
	}
	
	@Override
	public int extractRotation(int data) {
		return data % blockMutex;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileStargateBase te = (TileStargateBase) world.getTileEntity(x, y, z);
		if (te != null && te.getAsStructure().isValid()) {
			player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.StargateBase.ordinal(), world, x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public IIcon getIcon(int side, int data) {
		int typeof = (int) Math.floor(data / blockMutex);
		if (side <= 1)
			return topAndBottomTexture[typeof];
		else if (side == 3)
			return frontTexture[typeof];
		else
			return sideTexture[typeof];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < blockCraftableCount; i += blockCraftableMutex)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int data) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileStargateBase)
			((TileStargateBase) te).hostBlockDestroyed();
		super.breakBlock(world, x, y, z, block, data);
	}

	public void explode(World world, double x, double y, double z, double s) {
		if (true == true)
			return;

		TileEntity te = getTileEntity(world, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
		if (te != null && (te instanceof TileStargateBase)) {
			TileStargateBase gate = (TileStargateBase) te;
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
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileStargateBase();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileStargateBase)
			((TileStargateBase) te).getAsStructure().invalidate();
	}

	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileStargateBase)
			return ((TileStargateBase) te).getAsStructure().isValid();
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z) {
		TileEntity tileof = block.getTileEntity(x, y, z);
		if (tileof instanceof TileStargateBase) {
			TileStargateBase ring = (TileStargateBase) tileof;
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
