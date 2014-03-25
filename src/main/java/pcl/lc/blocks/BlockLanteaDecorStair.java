package pcl.lc.blocks;

import net.minecraft.block.BlockStairs;
import pcl.lc.LanteaCraft;
import pcl.lc.module.ModuleDecor.Blocks;

public class BlockLanteaDecorStair extends BlockStairs {

	public BlockLanteaDecorStair(int id, int meta) {
		super(id, Blocks.decorBlock, meta);
		setCreativeTab(LanteaCraft.getCreativeTab());
	}
}
