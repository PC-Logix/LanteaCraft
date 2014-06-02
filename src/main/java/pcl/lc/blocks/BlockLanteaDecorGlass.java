package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityLanteaDecorGlass;

public class BlockLanteaDecorGlass extends Block implements ITileEntityProvider {

	private IIcon missing;
	private IIcon lanteaGlassDefault;

	public BlockLanteaDecorGlass() {
		super(Material.glass);
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
	public void registerBlockIcons(IIconRegister register) {
		lanteaGlassDefault = register.registerIcon(LanteaCraft.getAssetKey() + ":lantean_glass_"
				+ LanteaCraft.getProxy().getRenderMode());

		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
	}

	@Override
	public IIcon getIcon(int side, int data) {
		switch (data) {
		case 1:
			return lanteaGlassDefault;
		default:
			return missing;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 1; i++)
			list.add(new ItemStack(item, 1, i + 1));
	}

	@Override
	public int damageDropped(int par1) {
		return par1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityLanteaDecorGlass();
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighbourId) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityLanteaDecorGlass)
			((TileEntityLanteaDecorGlass) tile).neighbourChanged();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityLanteaDecorGlass)
			((TileEntityLanteaDecorGlass) tile).neighbourChanged();
	}

}
