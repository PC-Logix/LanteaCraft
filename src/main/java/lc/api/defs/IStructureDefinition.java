package lc.api.defs;

import net.minecraft.world.gen.structure.MapGenStructure;

/**
 * Game world-generation structure container interface
 * 
 * @author AfterLifeLochie
 *
 */
public interface IStructureDefinition {

	public abstract String getName();

	public abstract Class<? extends MapGenStructure> getStructureClass();

}
