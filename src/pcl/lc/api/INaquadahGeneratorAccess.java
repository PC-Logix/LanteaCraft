/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api;

/**
 * Provides an interface for external code to interact with Naquadah Generators. You should
 * avoid interacting with Naquadah Generators outside of this API.
 * 
 * @author AfterLifeLochie
 */
public interface INaquadahGeneratorAccess {

	/**
	 * Determines if this Naquadah Generator is currently enabled. An enabled Naquadah
	 * Generator will continually attempt to fill it's internal power buffer and provide power
	 * to acceptors it is connected to.
	 * 
	 * @return If this Naquadah Generator is operating.
	 */
	public boolean isEnabled();

	/**
	 * Attempts to set the enabled state of the Naquadah Generator. An enabled Naquadah
	 * Generator will continually attempt to fill it's internal power buffer and provide power
	 * to acceptors it is connected to.
	 * 
	 * @param enable
	 *            The state to attempt to set.
	 * @return The state the Naquadah Generator is entering. If the request has failed, then
	 *         the returned result will not match the passed state.
	 */
	public boolean setEnabled(boolean enable);

	/**
	 * Gets the current quantity of energy stored in the Naquadah Generator.
	 * 
	 * @return The current quantity of energy stored in the Naquadah Generator.
	 */
	public double getStoredEnergy();

	/**
	 * Gets the maximum quantity of energy which may be stored in the Naquadah Generator.
	 * 
	 * @return The maximum quantity of energy which may be stored in the Naquadah Generator.
	 */
	public double getMaximumStoredEnergy();

}
