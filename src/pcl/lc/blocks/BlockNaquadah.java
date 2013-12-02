package pcl.lc.blocks;

import net.minecraft.block.BlockOreStorage;

public class BlockNaquadah extends BlockOreStorage {
	public BlockNaquadah(int id) {
		super(id);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundMetalFootstep);
	}
}
