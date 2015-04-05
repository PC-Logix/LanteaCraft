package lc.common;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import lc.api.audio.ISoundController;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;

/**
 * Hint provider contract interface.
 *
 * @author AfterLifeLochie
 *
 */
public interface IHintProvider {

	/** Called during pre-initialization */
	public void preInit();

	/** Called during initialization */
	public void init();

	/** Called during post-initialization */
	public void postInit();

	/**
	 * Provide hints on a definition
	 *
	 * @param definition
	 *            A definition element
	 */
	public void provideHints(IContainerDefinition definition);

	/**
	 * Provide hints on a recipe
	 *
	 * @param definition
	 *            A recipe definition element
	 */
	public void provideHints(IRecipeDefinition definition);

	public void onServerStarting(FMLServerStartingEvent event);

	public void onServerStopping(FMLServerStoppingEvent event);

	public ISoundController audio();
}
