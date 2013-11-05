//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate Controller Block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import gcewing.sg.SGCraft;
import gcewing.sg.base.Base4WayCtrBlock;
import gcewing.sg.core.EnumGuiList;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
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

public class BlockStargateController extends Base4WayCtrBlock {

	Icon topTexture, bottomTexture, sideTexture;

	public BlockStargateController(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		// blockIndexInTexture = 0x0a;
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topTexture = getIcon(reg, "controller_top_" + SGCraft.RenderHD);
		bottomTexture = getIcon(reg, "controller_bottom_" + SGCraft.RenderHD);
		sideTexture = getIcon(reg, "controller_side_" + SGCraft.RenderHD);
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
		if (SGCraft.HDModels == true)
			return -1;
		else
			return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		if (SGCraft.HDModels == true)
			return false;
		else
			return true;
	}

	@Override
	public boolean isOpaqueCube() {
		if (SGCraft.HDModels == true)
			return false;
		else
			return true;
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
		SGCraft.getInstance().openGui(player, EnumGuiList.SGController, world, x, y, z);
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
