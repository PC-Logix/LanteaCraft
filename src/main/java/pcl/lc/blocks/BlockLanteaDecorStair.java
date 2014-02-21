package pcl.lc.blocks;

import pcl.lc.LanteaCraft;
import pcl.lc.module.ModuleDecor.Blocks;
import net.minecraft.block.BlockStairs;

public class BlockLanteaDecorStair extends BlockStairs {

	public BlockLanteaDecorStair(int id, int meta) {
		super(id, Blocks.decorBlock, meta);
		this.setCreativeTab(LanteaCraft.getCreativeTab());
	}
}
