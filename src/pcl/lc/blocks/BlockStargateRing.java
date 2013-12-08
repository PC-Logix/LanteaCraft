package pcl.lc.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import pcl.lc.LanteaCraft;
import pcl.lc.base.RotationOrientedBlock;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateRing;
import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericContainerBlock;
import pcl.lc.items.ItemStargateRing;
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

public class BlockStargateRing extends GenericContainerBlock {

	static final int numSubBlocks = 2;
	public static final int subBlockMask = 0x1;
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
		if (LanteaCraft.Render.blockRingRenderer != null)
			return LanteaCraft.Render.blockRingRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_" + LanteaCraft.getProxy().getRenderMode());
		sideTextures[0] = getIcon(reg, "stargateRing_" + LanteaCraft.getProxy().getRenderMode());
		sideTextures[1] = getIcon(reg, "stargateChevron_" + LanteaCraft.getProxy().getRenderMode());
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

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		te.hostBlockPlaced();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		TileEntityStargateRing te = (TileEntityStargateRing) getTileEntity(world, x, y, z);
		super.breakBlock(world, x, y, z, id, data);
		if (te != null)
			te.getAsPart().devalidateHostMultiblock();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateRing();
	}

}
