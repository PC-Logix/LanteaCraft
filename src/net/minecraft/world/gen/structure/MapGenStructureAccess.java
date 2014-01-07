package net.minecraft.world.gen.structure;

import java.util.*;

/**
 * FIXME: This is definitely not a safe way of accessing the structure map. We can use
 * reflection or even our runtime transformers to access this. REMOVE THIS ASAP.
 */
public class MapGenStructureAccess {

	public static Map getStructureMap(MapGenStructure obj) {
		return obj.structureMap;
	}

	public static void setStructureMap(MapGenStructure obj, Map map) {
		obj.structureMap = map;
	}

}
