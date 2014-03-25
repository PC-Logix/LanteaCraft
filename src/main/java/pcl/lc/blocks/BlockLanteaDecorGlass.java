package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityLanteaDecorGlass;

public class BlockLanteaDecorGlass extends Block implements ITileEntityProvider {

	private Icon missing;
	private Icon lanteaGlassDefault;

	public BlockLanteaDecorGlass(int id) {
		super(id, Material.glass);
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
		if (LanteaCraft.Render.blockVoidRenderer != null)
			return LanteaCraft.Render.blockVoidRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerIcons(IconRegister register) {
		lanteaGlassDefault = register.registerIcon(LanteaCraft.getAssetKey() + ":lantean_glass_"
				+ LanteaCraft.getProxy().getRenderMode());

		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
	}

	@Override
	public Icon getIcon(int side, int data) {
		switch (data) {
		case 1:
			return lanteaGlassDefault;
		default:
			return missing;
		}
	}

	@Override
	public void getSubBlocks(int itemID, CreativeTabs tab, List list) {
		for (int i = 0; i < 1; i++)
			list.add(new ItemStack(itemID, 1, i + 1));
	}

	@Override
	public int damageDropped(int par1) {
		return par1;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLanteaDecorGlass();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighbourId) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityLanteaDecorGlass)
			((TileEntityLanteaDecorGlass) tile).neighbourChanged();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityLanteaDecorGlass)
			((TileEntityLanteaDecorGlass) tile).neighbourChanged();
	}

}
