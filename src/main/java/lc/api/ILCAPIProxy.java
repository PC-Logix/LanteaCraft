/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api;

import lc.api.components.IRegistryContainer;
import lc.api.init.Biomes;
import lc.api.init.Blocks;
import lc.api.init.Dimensions;
import lc.api.init.Entities;
import lc.api.init.Interfaces;
import lc.api.init.Items;
import lc.api.init.Recipes;
import lc.api.init.Structures;

/**
 * API proxy interface
 *
 * @author AfterLifeLochie
 *
 */
public interface ILCAPIProxy {

	/**
	 * Get the current LanteaCraft build and runtime information.
	 *
	 * @return The current LanteaCraft build and runtime information proxy.
	 */
	public IModInfo info();

	/**
	 * Get the signed state of the mod.
	 *
	 * @return If the mod is signed.
	 */
	public boolean signedState();

	/**
	 * Get the current LanteaCraft block registry
	 *
	 * @return The list of blocks
	 */
	public Blocks blocks();

	/**
	 * Get the current LanteaCraft item registry
	 *
	 * @return The list of items
	 */
	public Items items();

	/**
	 * Get the current LanteaCraft entity registry
	 *
	 * @return The list of entities
	 */
	public Entities entities();

	/**
	 * Get the registry container
	 *
	 * @return The registry container
	 */
	public IRegistryContainer registries();

	/**
	 * Get the list of biomes
	 *
	 * @return The list of biomes
	 */
	public Biomes biomes();

	/**
	 * Get the list of dimensions
	 *
	 * @return The list of dimensions
	 */
	public Dimensions dimensions();

	/**
	 * Get the list of recipes
	 *
	 * @return The list of recipes
	 */
	public Recipes recipes();

	/**
	 * Get the list of structures
	 *
	 * @return The list of structures
	 */
	public Structures structures();

	/**
	 * Get the list of interfaces
	 *
	 * @return The list of interfaces
	 */
	public Interfaces interfaces();

}
