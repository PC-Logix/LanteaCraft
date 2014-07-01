/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.internal;

/**
 * Internal integration label.
 * 
 * @author AfterLifeLochie
 */
public interface IIntegrationAgent {

	/**
	 * The name of the host mod.
	 * 
	 * @return The host mod name.
	 */
	public String modName();

	/**
	 * Perform any extended integration logic.
	 */
	public void init();
	
	/**
	 * Perform any final deferred integration logic.
	 */
	public void postInit();

}
