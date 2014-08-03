/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.access;

/**
 * Provides an interface for external code to interact with Stargate
 * Controllers. You should avoid interacting with Stargate Controllers outside
 * of this API.
 * 
 * @author AfterLifeLochie
 */
public interface IStargateControllerAccess {

	/**
	 * Determines if this controller is in range of one Stargate.
	 * 
	 * @return If this controller is in range of exactly one Stargate.
	 */
	public boolean isValid();

	/**
	 * Fetches the busy state of the controller. If the controller is currently
	 * conducting a dial out, this returns true. Any other condition returns
	 * false.
	 * 
	 * @return If the controller is currently busy.
	 */
	public boolean isBusy();

	/**
	 * Determines if this controller owns the current Stargate connection. If
	 * this controller created the connection, this returns true. If there is no
	 * connection, or if this controller did not create this connection, this
	 * returns false. Any other condition returns false.
	 * 
	 * @return If this controller own the current Stargate connection.
	 */
	public boolean ownsCurrentConnection();

	/**
	 * Gets the dialled address on the controller. If this controller was not
	 * used to dial a connection, this returns null. If the address is invalid,
	 * this will return null.
	 * 
	 * @return The address dialled.
	 */
	public String getDialledAddress();

	/**
	 * Requests this controller to instruct the Stargate to close the
	 * connection.
	 * 
	 * @return Returns true if the connection has been closed. Returns false if
	 *         the connection was not closed or cannot be closed, or if there is
	 *         no connection.
	 */
	public boolean disconnect();
}
