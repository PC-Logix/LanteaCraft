//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.registry.*;

public class SGRingBlock extends BaseContainerBlock<SGRingTE> {

	//static final int textureBase = 0x02;
	//static final int topAndBottomTexture = 0x00;
	static final int numSubBlocks = 2;
	static final int subBlockMask = 0x1;
	
	//public static Material ringMaterial = new Material(MapColor.stoneColor);
	
	Icon topAndBottomTexture;
	Icon sideTextures[] = new Icon[numSubBlocks];
	
	static String[] subBlockTitles = {
		"Stargate Ring Block",
		"Stargate Chevron Block",
	};

	public SGRingBlock(int id) {
		super(id, Block.blocksList[4].blockMaterial /*Material.rock*/ /*ringMaterial*/, SGRingTE.class);
		setHardness(1.5F);
		setCreativeTab(CreativeTabs.tabMisc);
		registerSubItemNames();
	}
	
	@Override
	String getRendererClassName() {
		return "SGRingBlockRenderer";
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_" + SGCraft.RenderHD);
		sideTextures[0] = getIcon(reg, "stargateRing_" + SGCraft.RenderHD);
		sideTextures[1] = getIcon(reg, "stargateChevron_" + SGCraft.RenderHD);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta) {
		return true;
	}
	
	@Override
	public int damageDropped(int data) {
		return data;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
		int side, float cx, float cy, float cz)
	{
		//System.out.printf("SGRingBlock.onBlockActivated at (%d, %d, %d)\n", x, y, z);
		SGRingTE te = getTileEntity(world, x, y, z);
		if (te.isMerged) {
			//System.out.printf("SGRingBlock.onBlockActivated: base at (%d, %d, %d)\n",
				//te.baseX, te.baseY, te.baseZ);
			Block block = Block.blocksList[world.getBlockId(te.baseX, te.baseY, te.baseZ)];
			if (block instanceof SGBaseBlock)
				block.onBlockActivated(world, te.baseX, te.baseY, te.baseZ, player,
					side, cx, cy, cz);
			return true;
		}
		return false;
	}

	@Override
	public Icon getIcon(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else
			return sideTextures[data & subBlockMask];
	}
	
	@Override
	public void getSubBlocks(int itemID, CreativeTabs tab, List list) {
		for (int i = 0; i < numSubBlocks; i++)
			list.add(new ItemStack(itemID, 1, i));
	}
	
	void registerSubItemNames() {
		LanguageRegistry registry = LanguageRegistry.instance();
		for (int i = 0; i < SGRingBlock.numSubBlocks; i++) {
			String name = SGRingItem.subItemName(i) + ".name";
			String title = subBlockTitles[i];
			//System.out.printf("SGRingBlock.registerSubItemNames: %s --> %s\n", name, title);
			registry.addStringLocalization(name, "en_US", title);
		}
	}
	
	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		SGRingTE te = getTileEntity(world, x, y, z);
		return te.isMerged;
	}
	
	public void mergeWith(World world, int x, int y, int z, int xb, int yb, int zb) {
		SGRingTE te = getTileEntity(world, x, y, z);
		te.isMerged = true;
		te.baseX = xb;
		te.baseY = yb;
		te.baseZ = zb;
		//te.onInventoryChanged();
		world.markBlockForUpdate(x, y, z);
	}
	
	public void unmergeFrom(World world, int x, int y, int z, int xb, int yb, int zb) {
		SGRingTE te = getTileEntity(world, x, y, z);
		if (te.isMerged && te.baseX == xb && te.baseY == yb && te.baseZ == zb) {
			//System.out.printf("SGRingBlock.unmergeFrom: unmerging\n");
			te.isMerged = false;
			//te.onInventoryChanged();
			world.markBlockForUpdate(x, y, z);
		}
	}
	
//	@Override
//	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {
//		SGRingTE te = getTileEntity(world, x, y, z);
//		updateBaseBlocks(world, x, y, z, te);
//	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		SGRingTE te = getTileEntity(world, x, y, z);
		updateBaseBlocks(world, x, y, z, te);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		SGRingTE te = getTileEntity(world, x, y, z);
		super.breakBlock(world, x, y, z, id, data);
		if (te != null && te.isMerged)
			updateBaseBlocks(world, x, y, z, te);
	}
	
	void updateBaseBlocks(World world, int x, int y, int z, SGRingTE te) {
		//System.out.printf("SGRingBlock.updateBaseBlocks: merged = %s, base = (%d,%d,%d)\n",
		//	te.isMerged, te.baseX, te.baseY, te.baseZ);
		for (int i = -2; i <= 2; i++)
			for (int j = -4; j <= 0; j++)
				for (int k = -2; k <= 2; k++) {
					int xb = x + i;
					int yb = y + j;
					int zb = z + k;
					Block block = Block.blocksList[world.getBlockId(xb, yb, zb)];
					if (block instanceof SGBaseBlock) {
						//System.out.printf("SGRingBlock.updateBaseBlocks: found base at (%d,%d,%d)\n",
						//	xb, yb, zb);
						SGBaseBlock base = (SGBaseBlock)block;
						if (!te.isMerged)
							base.checkForMerge(world, xb, yb, zb);
						else if (te.baseX == xb && te.baseY == yb && te.baseZ == zb)
							base.unmerge(world, xb, yb, zb);
				}
		}
	}
	
}
