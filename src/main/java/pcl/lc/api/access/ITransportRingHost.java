/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.access;

/**
 * Provides an interface for external code to interact with parts of Transport
 * Ring structures which are not directly considered the root of the structure.
 * 
 * @author AfterLifeLochie
 */
public interface ITransportRingHost {

	/**
	 * Requests this Transport Ring block provide access to the root
	 * {@link ITransportRingAccess} instance, allowing access to control
	 * methods. Each Transport Ring implements only one
	 * {@link ITransportRingAccess} instance, so this value remains constant for
	 * the lifecycle of the Transport Ring. If this structure is invalid, or
	 * does not yet exist, or there is no {@link ITransportRingAccess}
	 * available, this method will return {@link null}.
	 * 
	 * @return Access to the root {@link ITransportRingAccess} implementation,
	 *         or {@link null} if the current Transport Ring configuration is
	 *         invalid or in a state which does not allow it to function as a
	 *         Transport Ring.
	 * @see {@link ITransportRingAccess}
	 */
	public ITransportRingAccess getStargate();

}
