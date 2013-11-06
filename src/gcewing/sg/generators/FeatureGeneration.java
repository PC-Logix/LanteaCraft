//------------------------------------------------------------------------------------------------
//
//   SG Craft - Map feature generation
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.generators;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.world.gen.structure.ComponentScatteredFeatureDesertPyramid;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureAccess;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

public class FeatureGeneration {

	public static void onInitMapGen(InitMapGenEvent e) {
		switch (e.type) {
		case SCATTERED_FEATURE:
			if (e.newGen instanceof MapGenStructure)
				e.newGen = modifyScatteredFeatureGen((MapGenStructure) e.newGen);
			else
				// System.out.printf("SGCraft: FeatureGeneration: SCATTERED_FEATURE generator is not a MapGenStructure, cannot customise\n");
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

class SGStructureMap extends HashMap {

	@Override
	public Object put(Object key, Object value) {
		// System.out.printf("SGCraft: FeatureGeneration: SGStructureMap.put: %s\n",
		// value);
		if (value instanceof StructureStart)
			augmentStructureStart((StructureStart) value);
		return super.put(key, value);
	}

	void augmentStructureStart(StructureStart start) {
		LinkedList oldComponents = start.getComponents();
		LinkedList newComponents = new LinkedList();
		// int i = 0;
		for (Object comp : oldComponents)
			// StructureBoundingBox box =
			// ((StructureComponent)comp).getBoundingBox();
			// System.out.printf("SGCraft: FeatureGeneration: Found component %s at (%s, %s)\n",
			// comp, box.getCenterX(), box.getCenterZ());
			if (comp instanceof ComponentScatteredFeatureDesertPyramid)
				newComponents.add(new FeatureUnderDesertPyramid((ComponentScatteredFeatureDesertPyramid) comp));
		// ++i;
		oldComponents.addAll(newComponents);
	}

}
