/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api;

/**
 * Provides an interface for external code to interact with parts of Stargate
 * structures which are not directly considered the root of the structure.
 * 
 * @author AfterLifeLochie
 */
public interface IStargateHost {

	/**
	 * Requests this Stargate block provide access to the root
	 * {@link IStargateAccess} instance, allowing access to control methods.
	 * Each Stargate implements only one {@link IStargateAccess} instance, so
	 * this value remains constant for the lifecycle of the Stargate. If this
	 * structure is invalid, or does not yet exist, or there is no
	 * {@link IStargateAccess} available, this method will return {@link null}.
	 * 
	 * @return Access to the root {@link IStargateAccess} implementation, or
	 *         {@link null} if the current Stargate configuration is invalid or
	 *         in a state which does not allow it to function as a Stargate.
	 * @see {@link IStargateAccess}
	 */
	public IStargateAccess getStargate();

}
