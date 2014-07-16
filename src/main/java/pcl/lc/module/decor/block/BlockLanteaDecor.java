package pcl.lc.module.decor.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.ResourceAccess;

public class BlockLanteaDecor extends Block {

	private IIcon missing;

	private IIcon lanteaDecor;
	private IIcon lanteaMetal;

	private IIcon goauldDecor;
	private IIcon goauldMetal;

	public BlockLanteaDecor() {
		super(Material.ground);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		lanteaDecor = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"lantean_decor"));
		lanteaMetal = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"lantean_metal"));

		goauldDecor = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"goauld_golddecor"));
		goauldMetal = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"goauld_goldplain"));

		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		switch (data) {
		case 1:
			return lanteaDecor;
		case 2:
			return lanteaMetal;
		case 3:
			return goauldDecor;
		case 4:
			return goauldMetal;
		default:
			return missing;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 4; i++)
			list.add(new ItemStack(item, 1, i + 1));
	}

	@Override
	public int damageDropped(int par1) {
		return par1;
	}

}
