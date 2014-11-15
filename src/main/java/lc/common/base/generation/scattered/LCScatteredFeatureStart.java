package lc.common.base.generation.scattered;

import java.lang.reflect.Constructor;
import java.util.Random;

import lc.common.LCLog;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class LCScatteredFeatureStart extends StructureStart {

	public LCScatteredFeatureStart() {

	}

	@SuppressWarnings("unchecked")
	public LCScatteredFeatureStart(Class<? extends LCScatteredFeatureComponent> clazz, World worldObj, Random random,
			int chunkX, int chunkZ) {

		Constructor<? extends LCScatteredFeatureComponent> ctr;
		try {
			ctr = clazz.getConstructor(new Class<?>[] { Random.class, Integer.class, Integer.class });
			LCScatteredFeatureComponent feature = ctr.newInstance(random, chunkX, chunkZ);
			components.add(feature);
		} catch (Throwable e) {
			LCLog.warn("Could not add LanteaScatteredFeature.", e);
		}
		updateBoundingBox();
	}

}
