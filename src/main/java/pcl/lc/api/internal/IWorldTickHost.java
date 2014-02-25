/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.internal;

/**
 * Internal tick host; used in {@link CoreTickHandler}.
 * 
 * @author AfterLifeLochie
 */
public interface IWorldTickHost {

	/**
	 * Handle a world tick.
	 */
	public void tick();

}
