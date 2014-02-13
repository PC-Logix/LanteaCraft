package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.BlockOreStorage;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;

public class BlockOfLanteaOre extends BlockOreStorage {

	private Icon missing;

	public BlockOfLanteaOre(int id) {
		super(id);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundMetalFootstep);

	}

	@Override
	public void registerIcons(IconRegister register) {
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setItemAsBlockTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":naquadah_block_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.NAQAHDRIAH.setItemAsBlockTexture(register.registerIcon(LanteaCraft.getAssetKey()
				+ ":naqahdriah_block_" + LanteaCraft.getProxy().getRenderMode()));
		OreTypes.TRINIUM.setItemAsBlockTexture(register.registerIcon(LanteaCraft.getAssetKey() + ":trinium_block_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public Icon getIcon(int side, int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getItemAsBlockTexture();
	}

	@Override
	public void getSubBlocks(int itemID, CreativeTabs tab, List list) {
		for (int i = 0; i < OreTypes.values().length; i++)
			list.add(new ItemStack(itemID, 1, i));
	}
}
