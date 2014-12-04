package lc.common.util.game;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Block container access proxy. Allows the spoofing of containers and their
 * associated special block rendering.
 *
 * @author AfterLifeLochie
 *
 */
public class BlockContainerProxy extends Block {
	private Block myBlock;
	private int mySide;

	/**
	 * Default constructor
	 *
	 * @param block
	 *            The spoofed block
	 * @param side
	 *            The spoofed side
	 */
	public BlockContainerProxy(Block block, int side) {
		super(block.getMaterial());
		myBlock = block;
		mySide = side;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_,
			int side) {
		return side == mySide ? super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, side)
				: false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_) {
		return myBlock.getIcon(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_, p_149673_5_);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return myBlock.getIcon(p_149691_1_, p_149691_2_);
	}
}
