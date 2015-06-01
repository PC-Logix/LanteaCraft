/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.defs;

/**
 * Global game definition interface; all definitions in the game are
 * quantifiable to exactly one game definition type.
 *
 * @author AfterLifeLochie
 *
 */
public interface IGameDef {

	/**
	 * Get a reference to this definition.
	 *
	 * @return A reference to this definition.
	 */
	public IDefinitionReference ref();
}
