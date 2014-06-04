package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;

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
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setItemAsBlockTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadah_block_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.NAQUADRIAH.setItemAsBlockTexture(register.registerIcon(LanteaCraft.getAssetKey()
				+ ":naquadriah_block_" + LanteaCraft.getProxy().getRenderMode()));
		OreTypes.TRINIUM.setItemAsBlockTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":trinium_block_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (data >= OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getItemAsBlockTexture();
	}

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
