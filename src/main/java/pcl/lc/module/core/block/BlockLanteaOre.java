package pcl.lc.module.core.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.core.OreTypes;
import pcl.lc.core.ResourceAccess;
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
		// TODO: This can be flattened.
		missing = register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:missing"));
		OreTypes.NAQUADAH.setOreTexture(register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
						"naquadah_ore")));
		OreTypes.NAQUADRIAH.setOreTexture(register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
						"naquadriah_ore")));
		OreTypes.TRINIUM.setOreTexture(register.registerIcon(ResourceAccess
				.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
						"trinium_ore")));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getOreTexture();
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_,
			int p_149650_3_) {
		return ModuleCore.Items.lanteaOreItem;
	}

	@Override
	public int damageDropped(int metadata) {
		return metadata;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < OreTypes.values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}
}
