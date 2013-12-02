package pcl.lc.generators;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.world.gen.structure.ComponentScatteredFeatureDesertPyramid;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureAccess;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

/**
 * I finally had an epiphany about what this mess does.
 * 
 * By hooking to the onInitMapGen event, and then testing for a scattered
 * feature, we can modify the feature's actual behavior by setting the generator
 * to use a custom HashMap implementation.
 * 
 * In replacing the HashMap object with one with callbacks, the code here is
 * able to detect when a structure start is stated by the generator
 * (StructureStart) and then invoke augmentStructureStart, which effectively
 * allows tacking the additional features onto the structure.
 * 
 * It's smart, but I'm pretty sure Forge has a more... non-hacky way of doing
 * this.
 * 
 */
public class FeatureGeneration {

	public static void onInitMapGen(InitMapGenEvent e) {
		switch (e.type) {
		case SCATTERED_FEATURE:
			if (e.newGen instanceof MapGenStructure)
				e.newGen = modifyScatteredFeatureGen((MapGenStructure) e.newGen);
			else
				break;
		default:
			break;
		}
	}

	static MapGenStructure modifyScatteredFeatureGen(MapGenStructure gen) {
		MapGenStructureAccess.setStructureMap(gen, new SGStructureMap());
		return gen;
	}

}

/**
 * The replacement HashMap with added hooks
 */
class SGStructureMap extends HashMap {

	@Override
	public Object put(Object key, Object value) {
		if (value instanceof StructureStart)
			augmentStructureStart((StructureStart) value);
		return super.put(key, value);
	}

	/**
	 * Called to detect the specific structure type, given the type being added
	 * is a StructureStart
	 * 
	 * @param start
	 *            The StructureStart reference
	 */
	void augmentStructureStart(StructureStart start) {
		LinkedList oldComponents = start.getComponents();
		LinkedList newComponents = new LinkedList();
		for (Object comp : oldComponents)
			if (comp instanceof ComponentScatteredFeatureDesertPyramid)
				newComponents.add(new FeatureUnderDesertPyramid((ComponentScatteredFeatureDesertPyramid) comp));
		oldComponents.addAll(newComponents);
	}

}
