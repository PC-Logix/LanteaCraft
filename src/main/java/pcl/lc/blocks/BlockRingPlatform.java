package pcl.lc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import pcl.common.base.GenericContainerBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityRingPlatform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRingPlatform extends GenericContainerBlock {

	private Icon defaultIcon;

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
	public void registerIcons(IconRegister reg) {
		defaultIcon = reg.registerIcon(LanteaCraft.getAssetKey() + ":" + "ring_transporter_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public Icon getIcon(int side, int data) {
		return defaultIcon;
	}
}
