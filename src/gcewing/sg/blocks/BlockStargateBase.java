//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import gcewing.sg.SGCraft;
import gcewing.sg.SGCraft.Blocks;
import gcewing.sg.base.RotationOrientedBlock;
import gcewing.sg.config.ConfigValue;
import gcewing.sg.core.EnumGuiList;
import gcewing.sg.core.EnumStargateState;
import gcewing.sg.tileentity.TileEntityStargateBase;
import net.minecraft.block.Block;
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

public class BlockStargateBase extends RotationOrientedBlock {

	static boolean debugMerge = false;

	static final int mergedBit = 0x8;

	static int southSide[] = { 3, 5, 2, 4 };
	static int unitX[] = { 1, 0, -1, 0 };
	static int unitZ[] = { 0, -1, 0, 1 };

	static int pattern[][] = { { 2, 1, 2, 1, 2 }, { 1, 0, 0, 0, 1 }, { 2, 0, 0, 0, 2 }, { 1, 0, 0, 0, 1 },
			{ 2, 1, 0, 1, 2 }, };

	Icon topAndBottomTexture; // = 0x00;
	Icon frontTexture; // = 0x01;
	Icon sideTexture; // = 0x02;

	public BlockStargateBase(int id) {
		super(id, Material.rock);
		setHardness(1.5F);
		setCreativeTab(CreativeTabs.tabMisc);
		setTickRandomly(true);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int blockID) {
		return TileEntityStargateBase.powerLevel;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return TileEntityStargateBase.powerLevel;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int getRenderType() {
		if (SGCraft.Render.blockBaseRenderer != null)
			return SGCraft.Render.blockBaseRenderer.renderID;
		return -9001;
	}

	@Override
	public void registerIcons(IconRegister reg) {
		topAndBottomTexture = getIcon(reg, "stargateBlock_" + SGCraft.getProxy().getRenderMode());
		frontTexture = getIcon(reg, "stargateBase_front_" + SGCraft.getProxy().getRenderMode());
		sideTexture = getIcon(reg, "stargateRing_" + SGCraft.getProxy().getRenderMode());
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

	public boolean isMerged(IBlockAccess world, int x, int y, int z) {
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		return te != null && te.isMerged;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int data = Math.round((180 - player.rotationYaw) / 90) & 3;
		world.setBlockMetadataWithNotify(x, y, z, data, 0x3);
		if (!world.isRemote)
			checkForMerge(world, x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float cx,
			float cy, float cz) {
		String Side = world.isRemote ? "Client" : "Server";
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		if (te != null) {
			if (!world.isRemote)
				te.dumpChunkLoadingState("SGBaseBlock.onBlockActivated");
			if (te.isMerged) {
				player.openGui(SGCraft.getInstance(), EnumGuiList.SGBase.ordinal(), world, x, y, z);
				return true;
			}
		}
		return false;
	}

	@Override
	public Icon getIcon(int side, int data) {
		if (side <= 1)
			return topAndBottomTexture;
		else if (side == 3) // south
			return frontTexture;
		else
			return sideTexture;
	}

	public void checkForMerge(World world, int x, int y, int z) {
		if (!isMerged(world, x, y, z)) {
			int rot = getRotation(world, x, y, z);
			int dx = unitX[rot];
			int dz = unitZ[rot];
			for (int i = -2; i <= 2; i++)
				for (int j = 0; j <= 4; j++)
					if (!(i == 0 && j == 0)) {
						int xr = x + i * dx;
						int yr = y + j;
						int zr = z + i * dz;
						int type = getRingBlockType(world, xr, yr, zr);
						if (debugMerge) {
							System.out.printf("SGBaseBlock: type %d at (%d,%d,%d)\n", type, xr, yr, zr);
							System.out.printf("SGBaseBlock: pattern = %d\n", pattern[j][2 + i]);
						}
						int pat = pattern[4 - j][2 + i];
						if (pat != 0 && type != pat)
							return;
					}
			TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
			te.isMerged = true;
			world.markBlockForUpdate(x, y, z);
			for (int i = -2; i <= 2; i++)
				for (int j = 0; j <= 4; j++)
					if (!(i == 0 && j == 0)) {
						int xr = x + i * dx;
						int yr = y + j;
						int zr = z + i * dz;
						int id = world.getBlockId(xr, yr, zr);
						Object block = Block.blocksList[id];
						if (block instanceof BlockStargateRing)
							((BlockStargateRing) block).mergeWith(world, xr, yr, zr, x, y, z);
					}
			te.checkForLink();
		}
	}

	int getRingBlockType(World world, int xr, int yr, int zr) {
		int id = world.getBlockId(xr, yr, zr);
		if (id == 0)
			return 0;
		if (id == Blocks.sgRingBlock.blockID)
			if (!Blocks.sgRingBlock.isMerged(world, xr, yr, zr)) {
				int data = world.getBlockMetadata(xr, yr, zr);
				switch (data & BlockStargateRing.subBlockMask) {
				case 0:
					return 1;
				case 1:
					return 2;
				}
			}
		return -1;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data) {
		unmerge(world, x, y, z);
		super.breakBlock(world, x, y, z, id, data);
	}

	public void unmerge(World world, int x, int y, int z) {
		TileEntityStargateBase te = (TileEntityStargateBase) getTileEntity(world, x, y, z);
		boolean goBang = false;
		if (te != null /* && te.isMerged */) {
			if (te.isMerged && te.state == EnumStargateState.Connected) {
				te.state = EnumStargateState.Idle;
				if (((ConfigValue<Boolean>) SGCraft.getProxy().getConfigValue("doGateExplosion")).getValue())
					goBang = true;
			}
			te.disconnect();
			te.unlinkFromController();
			te.isMerged = false;
			world.markBlockForUpdate(x, y, z);
			unmergeRing(world, x, y, z);
		}
		if (goBang)
			// THIS is the explode function \/ \/ \/
			explode(world, x + 0.5, y + 2.5, z + 0.5, 10);
		// world.newExplosion(null, x + 0.5, y + 2.5, z + 0.5, 10, true,
		// true);
		// SGExplosion exp = new SGExplosion(world, null, x + 0.5, y + 2.5,
		// z + 0.5, 10);
		// exp.isFlaming = true;
		// exp.isSmoking = true;
		// exp.doExplosionA();
		// exp.doExplosionB(true);
	}

	void explode(World world, double x, double y, double z, double s) {
		world.newExplosion(null, x, y, z, (float) s, true, true);
	}

	void unmergeRing(World world, int x, int y, int z) {
		for (int i = -2; i <= 2; i++)
			for (int j = 0; j <= 4; j++)
				for (int k = -2; k <= 2; k++)
					unmergeRingBlock(world, x, y, z, x + i, y + j, z + k);
	}

	void unmergeRingBlock(World world, int x, int y, int z, int xr, int yr, int zr) {
		Object block = Block.blocksList[world.getBlockId(xr, yr, zr)];
		if (block instanceof BlockStargateRing)
			((BlockStargateRing) block).unmergeFrom(world, xr, yr, zr, x, y, z);
		else if (block instanceof BlockPortal)
			world.setBlock(xr, yr, zr, 0, 0, 0x3);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateBase();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
	}

}
