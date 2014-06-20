package pcl.lc.blocks;

import java.util.List;

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
import pcl.common.base.GenericContainerBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityTransporterRing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTransporterRing extends GenericContainerBlock {

	private IIcon defaultIcon;
	IIcon topTexture;
	IIcon faceTexture;

	public BlockTransporterRing() {
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
		if (LanteaCraft.Render.blockTransporterRingRenderer != null)
			return LanteaCraft.Render.blockTransporterRingRenderer.renderID;
		return -9001;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileEntityTransporterRing ring = new TileEntityTransporterRing();
		ring.setHost(metadata != 0);
		return ring;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}
	
	
	@Override
	public int damageDropped(int data) {
		return data;
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
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 2; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		defaultIcon = reg.registerIcon(LanteaCraft.getAssetKey() + ":" + "ring_transporter_"
				+ LanteaCraft.getProxy().getRenderMode());
		topTexture = reg.registerIcon(LanteaCraft.getAssetKey() + ":" + "transport_ring_base_"
				+ LanteaCraft.getProxy().getRenderMode());
		faceTexture = reg.registerIcon(LanteaCraft.getAssetKey() + ":" + "stargateBlock_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (side == 1 && data != 0)
			return topTexture;
		else
			return faceTexture;
	}
}
