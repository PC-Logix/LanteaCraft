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
import pcl.lc.base.GenericContainerBlock;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.module.stargate.tile.TileTransporterRing;
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
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	@Override
	public int getRenderType() {
		if (ModuleStargates.Render.blockTransporterRingRenderer != null)
			return ModuleStargates.Render.blockTransporterRingRenderer.renderID;
		return -9001;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		TileTransporterRing ring = new TileTransporterRing();
		return ring;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int data) {
		TileEntity te = getTileEntity(world, x, y, z);
		if (te != null && te instanceof TileTransporterRing)
			((TileTransporterRing) te).hostBlockDestroyed();
		super.breakBlock(world, x, y, z, block, data);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		defaultIcon = reg.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"ring_transporter"));
		topTexture = reg.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"transport_ring_base"));
		faceTexture = reg.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"stargate_block"));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (side == 1 && data != 0)
			return topTexture;
		else
			return faceTexture;
	}
}
