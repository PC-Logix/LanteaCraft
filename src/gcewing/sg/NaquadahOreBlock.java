//------------------------------------------------------------------------------------------------
//
//   SG Craft - Naquadah ore block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;

import net.minecraftforge.common.*;

public class NaquadahOreBlock extends BlockOre {

	//static int texture = 0x40;

	public NaquadahOreBlock(int id) {
		super(id);
		//setTextureFile(SGCraft.textureFile);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 3);
		setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3) {
		return SGCraft.naquadah.itemID;
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(5);
	}

}
