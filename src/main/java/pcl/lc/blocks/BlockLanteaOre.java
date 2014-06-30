package pcl.lc.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;
import pcl.lc.module.ModuleCore;

public class BlockLanteaOre extends BlockOre {

	private IIcon missing;

	public BlockLanteaOre() {
		super();
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 3);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setOreTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadah_ore_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.NAQUADRIAH.setOreTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadriah_ore_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.TRINIUM.setOreTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":trinium_ore_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getOreTexture();
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ModuleCore.Items.lanteaOreItem;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < OreTypes.values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}
}
