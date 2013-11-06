//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import gcewing.sg.SGCraft;
import gcewing.sg.base.BaseContainerBlock;
import gcewing.sg.items.ItemStargateRing;
import gcewing.sg.tileentity.TileEntityStargateRing;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockStargateRing extends BaseContainerBlock {

	// static final int textureBase = 0x02;
	// static final int topAndBottomTexture = 0x00;
	static final int numSubBlocks = 2;
	public static final int subBlockMask = 0x1;

	// public static Material ringMaterial = new Material(MapColor.stoneColor);

	Icon topAndBottomTexture;
	Icon sideTextures[] = new Icon[numSubBlocks];

	static String[] subBlockTitles = { "Stargate Ring Block", "Stargate Chevron Block", };

	public BlockStargateRing(int id) {
		super(id, Block.blocksList[4].blockMaterial);
		setHardness(1.5F);
		setCreativeTab(CreativeTabs.tabMisc);
		registerSubItemNames();
	}

	@Override
	public int getRenderType() {
		if (SGCraft.Render.blockRingRenderer != null)
			return SGCraft.Render.blockRingRenderer.renderID;
		return -9001;
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		if (te.isMerged) {
			Block block = Block.blocksList[world.getBlockId(te.baseX, te.baseY, te.baseZ)];
			if (block instanceof BlockStargateBase)
				block.onBlockActivated(world, te.baseX, te.baseY, te.baseZ, player, side, cx, cy, cz);
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
		for (int i = 0; i < BlockStargateRing.numSubBlocks; i++) {
			String name = ItemStargateRing.subItemName(i) + ".name";
			String title = subBlockTitles[i];
			// System.out.printf("SGRingBlock.registerSubItemNames: %s --> %s\n",
			// name, title);
			registry.addStringLocalization(name, "en_US", title);
		}
	}

	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		return te.isMerged;
	}

	public void mergeWith(World world, int x, int y, int z, int xb, int yb, int zb) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		te.isMerged = true;
		te.baseX = xb;
		te.baseY = yb;
		te.baseZ = zb;
		// te.onInventoryChanged();
		world.markBlockForUpdate(x, y, z);
	}

	public void unmergeFrom(World world, int x, int y, int z, int xb, int yb, int zb) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		if (te.isMerged && te.baseX == xb && te.baseY == yb && te.baseZ == zb) {
			te.isMerged = false;
			world.markBlockForUpdate(x, y, z);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		updateBaseBlocks(world, x, y, z, te);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		super.breakBlock(world, x, y, z, id, data);
		if (te != null && te.isMerged)
			updateBaseBlocks(world, x, y, z, te);
	}

	void updateBaseBlocks(World world, int x, int y, int z, TileEntityStargateRing te) {
		for (int i = -2; i <= 2; i++)
			for (int j = -4; j <= 0; j++)
				for (int k = -2; k <= 2; k++) {
					int xb = x + i;
					int yb = y + j;
					int zb = z + k;
					Block block = Block.blocksList[world.getBlockId(xb, yb, zb)];
					if (block instanceof BlockStargateBase) {
						BlockStargateBase base = (BlockStargateBase) block;
						if (!te.isMerged)
							base.checkForMerge(world, xb, yb, zb);
						else if (te.baseX == xb && te.baseY == yb && te.baseZ == zb)
							base.unmerge(world, xb, yb, zb);
					}
				}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateRing();
	}

}
