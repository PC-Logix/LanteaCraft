package lc.common.base.generation.structure;

import java.util.Random;

import lc.common.LCLog;
import lc.generation.AbydosPyramid;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class LCFeatureStart extends StructureStart {
	public LCFeatureStart() {
	}

	@SuppressWarnings("unchecked")
	public LCFeatureStart(World worldObj, Random random,
			int chunkX, int chunkZ) {
		/*
		 * TODO: Need to make a way to make MapGenFeatureStructureStart abstract
		 * to allow multiple types of generators!
		 */
		AbydosPyramid pyramid = new AbydosPyramid(random, chunkX * 16,
				chunkZ * 16);
		LCLog.info(String.format("Placing pyramid at (%s, %s)", (chunkX * 16),
				(chunkZ * 16)));
		components.add(pyramid);
		updateBoundingBox();
	}
}