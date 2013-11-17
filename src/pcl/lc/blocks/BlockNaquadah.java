package pcl.lc.blocks;

import net.minecraft.block.BlockOreStorage;

public class BlockNaquadah extends BlockOreStorage {

	// static int texture = 0x43;

	public BlockNaquadah(int id) {
		super(id);
		// setTextureFile(SGCraft.textureFile);
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundMetalFootstep);
	}

}
