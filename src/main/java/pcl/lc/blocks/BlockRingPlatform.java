package pcl.lc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.base.GenericContainerBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityRingPlatform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRingPlatform extends GenericContainerBlock {

	public BlockRingPlatform(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setTickRandomly(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.Render.blockVoidRenderer != null)
			return LanteaCraft.Render.blockVoidRenderer.renderID;
		return -9001;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityRingPlatform();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

	}

	@Override
	public void registerIcons(IconRegister reg) {

	}
}
