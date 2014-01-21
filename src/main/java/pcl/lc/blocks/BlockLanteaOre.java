package pcl.lc.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Items;

public class BlockNaquadahOre extends BlockOre {

	public BlockNaquadahOre(int id) {
		super(id);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 3);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getTextureName() {
		return LanteaCraft.getAssetKey() + ":naquadahOre_" + LanteaCraft.getProxy().getRenderMode();
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return Items.naquadah.itemID;
	}

	@Override
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(5);
	}
}
