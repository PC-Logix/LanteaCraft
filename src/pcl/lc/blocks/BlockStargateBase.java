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
import pcl.lc.LanteaCraft;
import pcl.lc.base.RotationOrientedBlock;
import pcl.lc.core.EnumGuiList;
import pcl.lc.tileentity.TileEntityStargateBase;

public class BlockStargateBase extends RotationOrientedBlock {

	static int southSide[] = { 3, 5, 2, 4 };
	static int unitX[] = { 1, 0, -1, 0 };
	static int unitZ[] = { 0, -1, 0, 1 };

	static int pattern[][] = { { 2, 1, 2, 1, 2 }, { 1, 0, 0, 0, 1 }, { 2, 0, 0, 0, 2 }, { 1, 0, 0, 0, 1 },
			{ 2, 1, 0, 1, 2 }, };

	Icon topAndBottomTexture;
	Icon frontTexture;
	Icon sideTexture;

	public BlockStargateBase(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setCreativeTab(CreativeTabs.tabMisc);
		setTickRandomly(true);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int blockID) {
		return TileEntityStargateBase.powerLevel;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return TileEntityStargateBase.powerLevel;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.Render.blockBaseRenderer != null) return LanteaCraft.Render.blockBaseRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_" + LanteaCraft.getProxy().getRenderMode());
		frontTexture = getIcon(reg, "stargateBase_front_" + LanteaCraft.getProxy().getRenderMode());
		sideTexture = getIcon(reg, "stargateRing_" + LanteaCraft.getProxy().getRenderMode());
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
		String Side = world.isRemote ? "Client" : "Server";
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		if (te != null) {
			if (te.getAsStructure().isValid()) {
				player.openGui(LanteaCraft.getInstance(), EnumGuiList.SGBase.ordinal(), world, x, y, z);
				return true;
			}
		}
		return false;
	}

	@Override
	public Icon getIcon(int side, int data) {
		if (side <= 1) return topAndBottomTexture;
		else if (side == 3) // south
		return frontTexture;
		else
			return sideTexture;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase) ((TileEntityStargateBase) te).hostBlockDestroyed();
		super.breakBlock(world, x, y, z, id, data);
	}

	void explode(World world, double x, double y, double z, double s) {
		world.newExplosion(null, x, y, z, (float) s, true, true);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateBase();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase) ((TileEntityStargateBase) te).getAsStructure()
				.invalidate();
	}

	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileEntityStargateBase) return ((TileEntityStargateBase) te).getAsStructure()
				.isValid();
		return false;
	}
}
