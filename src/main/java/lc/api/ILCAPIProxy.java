package lc.api;

import lc.api.components.IRegistryContainer;
import lc.api.defs.Biomes;
import lc.api.defs.Blocks;
import lc.api.defs.Dimensions;
import lc.api.defs.Items;

/**
 * API proxy interfacve
 * 
 * @author AfterLifeLochie
 * 
 */
public interface ILCAPIProxy {

	/**
	 * Get the signed state of the mod.
	 * 
	 * @return If the mod is signed.
	 */
	public boolean signedState();

	/**
	 * Get the list of blocks
	 * 
	 * @return The list of blocks
	 */
	public Blocks blocks();

	/**
	 * Get the list of items
	 * 
	 * @return The list of items
	 */
	public Items items();

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

}
