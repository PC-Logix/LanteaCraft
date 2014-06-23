package pcl.lc.dimension;

import java.util.Random;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.dimension.abydos.AbydosPyramid;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenFeatureStructureStart extends StructureStart {
	public MapGenFeatureStructureStart() {
	}

	public MapGenFeatureStructureStart(World worldObj, Random random, int chunkX, int chunkZ) {
		/*
		 * TODO: Need to make a way to make MapGenFeatureStructureStart abstract
		 * to allow multiple types of generators!
		 */
		AbydosPyramid pyramid = new AbydosPyramid(random, chunkX * 16, chunkZ * 16);
		LanteaCraft.getLogger().log(Level.INFO,
				String.format("Placing pyramid at (%s, %s)", (chunkX * 16), (chunkZ * 16)));
		this.components.add(pyramid);
		updateBoundingBox();
	}
}