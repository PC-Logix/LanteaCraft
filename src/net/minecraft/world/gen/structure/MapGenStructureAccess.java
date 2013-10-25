//------------------------------------------------------------------------------------------------
//
//   SG Craft - Access wrapper for MapGenStructure
//
//------------------------------------------------------------------------------------------------

package net.minecraft.world.gen.structure;

import java.util.*;

public class MapGenStructureAccess {
	
	public static Map getStructureMap(MapGenStructure obj) {
		return obj.structureMap;
	}
	
	public static void setStructureMap(MapGenStructure obj, Map map) {
		obj.structureMap = map;
	}

}
