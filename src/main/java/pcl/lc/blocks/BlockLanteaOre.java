package pcl.lc.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.core.OreTypes;

public class BlockLanteaOre extends BlockOre {

	private IIcon missing;

	public BlockLanteaOre() {
		super();
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 3);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		missing = register.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		OreTypes.NAQUADAH.setOreTexture(register.registerIcon(LanteaCraft
				.getAssetKey()
				+ ":naquadah_ore_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.NAQUADRIAH.setOreTexture(register.registerIcon(LanteaCraft
				.getAssetKey()
				+ ":naquadriah_ore_"
				+ LanteaCraft.getProxy().getRenderMode()));
		OreTypes.TRINIUM.setOreTexture(register.registerIcon(LanteaCraft
				.getAssetKey()
				+ ":trinium_ore_"
				+ LanteaCraft.getProxy().getRenderMode()));
	}

	@Override
	public IIcon getIcon(int side, int data) {
		if (data > OreTypes.values().length)
			return missing;
		return OreTypes.values()[data].getOreTexture();
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y,
			int z, int metadata, int fortune) {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		result.add(new ItemStack(Items.lanteaOreItem, 2, metadata));
		return result;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < OreTypes.values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}
}
