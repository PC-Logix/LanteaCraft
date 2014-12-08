package lc.common.base.generation.scattered;

import java.lang.reflect.Constructor;
import java.util.Random;

import lc.common.LCLog;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * LanteaCraft scattered feature start. You should create and generate
 * components here.
 *
 * @author AfterLifeLochie
 *
 */
public class LCScatteredFeatureStart extends StructureStart {

	/** Default constructor. */
	public LCScatteredFeatureStart() {

	}

	/**
	 * Construct a new feature start.
	 *
	 * @param clazz
	 *            The feature component class
	 * @param world
	 *            The world object
	 * @param rng
	 *            The random number generator
	 * @param chunkX
	 *            The chunk x-coordinate
	 * @param chunkZ
	 *            The chunk z-coordinate
	 */
	@SuppressWarnings("unchecked")
	public LCScatteredFeatureStart(Class<? extends LCScatteredFeature> clazz, World world, Random rng, int chunkX,
			int chunkZ) {

		Constructor<? extends LCScatteredFeature> ctr;
		try {
			ctr = clazz.getConstructor(new Class<?>[] { Random.class, Integer.class, Integer.class });
			LCScatteredFeature feature = ctr.newInstance(rng, chunkX, chunkZ);
			components.add(feature);
		} catch (Throwable e) {
			LCLog.warn("Could not add LanteaScatteredFeature.", e);
		}
		updateBoundingBox();
	}

}
