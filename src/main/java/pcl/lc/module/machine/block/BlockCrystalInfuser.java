package pcl.lc.module.machine.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.base.RotationOrientedBlock;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.machine.tile.TileCrystalInfuser;

public class BlockCrystalInfuser extends RotationOrientedBlock implements ITileEntityProvider {

	public BlockCrystalInfuser() {
		super(Material.ground);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public int getRenderType() {
		if (ModuleCore.Render.blockModelRenderer != null)
			return ModuleCore.Render.blockModelRenderer.renderID;
		return -9001;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileCrystalInfuser();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.CrystalInfuser.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			int sig = 0;
			int a = isBlockProvidingPower(world, x, y + 1, z, 1);
			int b = isBlockProvidingPower(world, x, y - 1, z, 0);
			int c = isBlockProvidingPower(world, x, y, z + 1, 3);
			int d = isBlockProvidingPower(world, x, y, z - 1, 2);
			int e = isBlockProvidingPower(world, x + 1, y, z, 5);
			int f = isBlockProvidingPower(world, x - 1, y, z, 4);
			sig = Math.max(a, Math.max(b, Math.max(c, Math.max(d, Math.max(e, f)))));
			TileCrystalInfuser cte = (TileCrystalInfuser) world.getTileEntity(x, y, z);
			cte.setRedstoneInputSignal(sig > 0);
		}
	}

	private int isBlockProvidingPower(World world, int x, int y, int z, int direction) {
		if (y >= 0 && y < world.getHeight()) {
			int redstoneWireValue = (world.getBlock(x, y, z).equals(Blocks.redstone_wire)) ? world.getBlockMetadata(x,
					y, z) : 0;
			int indirectPowerTo = world.getIndirectPowerLevelTo(x, y, z, direction);
			int directPowerTo = world.isBlockProvidingPowerTo(x, y, z, direction);
			return Math.max(Math.max(redstoneWireValue, indirectPowerTo), directPowerTo);
		} else
			return 0;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {

	}

}
