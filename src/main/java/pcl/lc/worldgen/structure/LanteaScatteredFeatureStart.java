package pcl.lc.worldgen.structure;

import java.lang.reflect.Constructor;
import java.util.Random;
import org.apache.logging.log4j.Level;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import pcl.lc.LanteaCraft;

public class LanteaScatteredFeatureStart extends StructureStart {

	public LanteaScatteredFeatureStart() {

	}

	public LanteaScatteredFeatureStart(Class<? extends LanteaScatteredFeature> clazz, World worldObj, Random random,
			int chunkX, int chunkZ) {

		Constructor<? extends LanteaScatteredFeature> ctr;
		try {
			ctr = clazz.getConstructor(new Class<?>[] { Random.class, Integer.class, Integer.class });
			LanteaScatteredFeature feature = ctr.newInstance(random, chunkX, chunkZ);
			components.add(feature);
		} catch (Throwable e) {
			LanteaCraft.getLogger().log(Level.WARN, "Could not add LanteaScatteredFeature.", e);
		}
		updateBoundingBox();
	}

}
