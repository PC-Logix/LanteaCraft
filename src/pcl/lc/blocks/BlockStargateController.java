package pcl.lc.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import pcl.common.base.RotationOrientedBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;

public class BlockStargateController extends RotationOrientedBlock {

	Icon topTexture, bottomTexture, sideTexture;

	public BlockStargateController(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topTexture = getIcon(reg, "controller_top_" + LanteaCraft.getProxy().getRenderMode());
		bottomTexture = getIcon(reg, "controller_bottom_" + LanteaCraft.getProxy().getRenderMode());
		sideTexture = getIcon(reg, "controller_side_" + LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public Icon getIcon(int side, int data) {
		switch (side) {
			case 0:
				return bottomTexture;
			case 1:
				return topTexture;
			default:
				return sideTexture;
		}
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.getProxy().isUsingModels())
			return -1;
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return !LanteaCraft.getProxy().isUsingModels();
	}

	@Override
	public boolean isOpaqueCube() {
		return !LanteaCraft.getProxy().isUsingModels();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		checkForLink(world, x, y, z);
		int dir = MathHelper.floor_double(player.rotationYaw * 4F / 360F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, dir, 0);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		TileEntityStargateController cte = (TileEntityStargateController) getTileEntity(world, x, y, z);
		super.breakBlock(world, x, y, z, id, data);
		if (cte.isLinkedToStargate) {
			TileEntityStargateBase gte = cte.getLinkedStargateTE();
			if (gte != null)
				gte.clearLinkToController();
		}
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		player.openGui(LanteaCraft.getInstance(), LanteaCraft.EnumGUIs.StargateController.ordinal(), world, x, y, z);
		return true;
	}

	public void checkForLink(World world, int x, int y, int z) {
		((TileEntityStargateController) getTileEntity(world, x, y, z)).checkForLink();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateController();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		// TODO Auto-generated method stub

	}

}
