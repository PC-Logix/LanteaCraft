package pcl.lc.module.machine.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.lc.base.RotationOrientedBlock;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.machine.tile.TileTablePress;

public class BlockTablePress extends RotationOrientedBlock implements ITileEntityProvider {

	public BlockTablePress() {
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
		if (ModuleCore.Render.blockVoidRenderer != null)
			return ModuleCore.Render.blockVoidRenderer.renderID;
		return -9001;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileTablePress();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		// TODO Auto-generated method stub

	}

}
