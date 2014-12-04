/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.defs;

/**
 * Game biome container interface
 *
 * @author AfterLifeLochie
 *
 */
public interface IBiomeDefinition extends IGameDef {

	/**
	 * Get the name of the biome
	 *
	 * @return The name of the biome
	 */
	public abstract String getName();

}
