package pcl.lc.blocks;

import java.util.List;

import pcl.lc.LanteaCraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class BlockLanteaDecor extends Block {

	private Icon missing;

	private Icon lanteaDecor;
	private Icon lanteaMetal;

	private Icon goauldDecor;
	private Icon goauldMetal;

	public BlockLanteaDecor(int id) {
		super(id, Material.ground);
	}

	@Override
	public void registerIcons(IconRegister register) {
		lanteaDecor = register.registerIcon(LanteaCraft.getAssetKey() + ":lantean_decor_"
				+ LanteaCraft.getProxy().getRenderMode());
		lanteaMetal = register.registerIcon(LanteaCraft.getAssetKey() + ":lantean_metal_"
				+ LanteaCraft.getProxy().getRenderMode());

		goauldDecor = register.registerIcon(LanteaCraft.getAssetKey() + ":goauld_golddecor_"
				+ LanteaCraft.getProxy().getRenderMode());
		goauldMetal = register.registerIcon(LanteaCraft.getAssetKey() + ":goauld_goldplain_"
				+ LanteaCraft.getProxy().getRenderMode());

		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
	}

	@Override
	public Icon getIcon(int side, int data) {
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

	@Override
	public void getSubBlocks(int itemID, CreativeTabs tab, List list) {
		for (int i = 0; i < 4; i++)
			list.add(new ItemStack(itemID, 1, i + 1));
	}

}
