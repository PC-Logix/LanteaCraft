package pcl.lc.module.decor.block;

import net.minecraft.block.BlockStairs;
import pcl.lc.util.CreativeTabHelper;
import pcl.lc.module.ModuleDecor.Blocks;

public class BlockLanteaDecorStair extends BlockStairs {

	public BlockLanteaDecorStair(int meta) {
		super(Blocks.decorBlock, meta);
		setCreativeTab(CreativeTabHelper.getTab("LanteaCraft"));
	}
}
