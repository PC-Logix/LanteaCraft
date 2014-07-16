package pcl.lc.module.core.block;

import java.util.List;

import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;
import pcl.lc.core.ResourceAccess;

public class BlockOfLanteaOre extends BlockOre {

	private IIcon missing;

	public BlockOfLanteaOre() {
		super();
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundTypeMetal);

	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		// TODO: This can be flattened.
		missing = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		OreTypes.NAQUADAH.setItemAsBlockTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "naquadah_block")));
		OreTypes.NAQUADRIAH.setItemAsBlockTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "naquadriah_block")));
		OreTypes.TRINIUM.setItemAsBlockTexture(register.registerIcon(ResourceAccess.formatResourceName(
				"${ASSET_KEY}:%s_${TEX_QUALITY}", "trinium_block")));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (data >= OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getItemAsBlockTexture();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < OreTypes.values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int damageDropped(int par1) {
		return par1;
	}
}
