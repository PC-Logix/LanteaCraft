package pcl.lc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.common.base.GenericContainerBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityRingPlatform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRingPlatform extends GenericContainerBlock {

	private IIcon defaultIcon;

	public BlockRingPlatform() {
		super(Material.rock);
		setHardness(1.5F);
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
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityRingPlatform();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {

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
	public void registerBlockIcons(IIconRegister reg) {
		defaultIcon = reg.registerIcon(LanteaCraft.getAssetKey() + ":" + "ring_transporter_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public IIcon getIcon(int side, int data) {
		return defaultIcon;
	}
}
