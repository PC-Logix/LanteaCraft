/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api;

/**
 * Provides an interface for external code to interact with Stargate structures.
 * You should avoid interacting with Stargates outside of this API.
 * 
 * @author AfterLifeLochie
 */
public interface IStargateAccess {

	/**
	 * Returns the multi-block structure state of the Stargate. As all parts of
	 * the Stargate act as an {@link IStargateAccess} host, it is important to
	 * check to ensure the Stargate is a completed structure, and that nothing
	 * is directly obstructing or preventing it's completeness.
	 * 
	 * @return If the current Stargate structure configuration is valid.
	 */
	public boolean isValid();

	/**
	 * Fetches the busy state of the Stargate. If the Stargate is in currently
	 * dialling a connection, receiving a connection, in a connection or closing
	 * a connection, this returns true. If the gate is completely idle (that is,
	 * doing nothing), this returns false.
	 * 
	 * @return If the Stargate is currently engaged in any operation.
	 */
	public boolean isBusy();

	/**
	 * Fetches the raw state of the Stargate.
	 * 
	 * @return The raw state of the Stargate.
	 */
	public EnumStargateState getState();

	/**
	 * Fetches the state of the Stargate's iris.
	 * 
	 * @return The state of the Stargate's iris.
	 */
	public EnumIrisState isIrisActive();

	/**
	 * Determines if the current dialled connection is an outgoing one (that is,
	 * initiated from this Stargate). If there is no connection, this returns
	 * false. If this Stargate initiated the connection, this will return true.
	 * All other conditions return false.
	 * 
	 * @return If the Stargate initiated the current connection.
	 */
	public boolean isOutgoingConnection();

	/**
	 * Obtains the local address of the current Stargate.
	 * 
	 * @return The current Stargate's address.
	 */
	public String getLocalAddress();

	/**
	 * Obtains the address of the current Stargate connection, regardless of
	 * which Stargate created the connection. If no connection is currently
	 * present, this returns null.
	 * 
	 * @return The current Stargate's connected address, or null if no
	 *         connection is active.
	 */
	public String getConnectionAddress();

	/**
	 * Requests this Stargate attempt to dial the provided address.
	 * 
	 * @param address
	 *            The address to attempt to dial.
	 * @return Returns true if the dialling has started successfully. Returns
	 *         false if the address is invalid, if there is not enough energy
	 *         available to open the connection, or if some other condition
	 *         currently prevents this Stargate from initiating a connection.
	 */
	public boolean connect(String address);

	/**
	 * Requests this Stargate close it's active connection.
	 * 
	 * @return Returns true if the connection has been closed. Returns false if
	 *         the connection was not closed or cannot be closed, or if there is
	 *         no connection.
	 */
	public boolean disconnect();

	/**
	 * Gets the current number of encoded (locked) chevrons in a dialling
	 * sequence. During any dialling sequence, this returns the number of locked
	 * chevrons; outside of a dialling sequence this returns -1.
	 * 
	 * @return The number of encoded chevrons if a connection is being created,
	 *         -1 otherwise.
	 */
	public int getEncodedChevrons();

	/**
	 * Gets the total quantity of energy which is immediately available to this
	 * Stargate.
	 * 
	 * @return The total quantity of energy which is immediately available to
	 *         this Stargate, measured in the arbitrary unit 'naquadah units'.
	 */
	public double getAvailableEnergy();

	/**
	 * Gets the total remaining dial requests which are currently available,
	 * based on the currently immediately available energy sources.
	 * 
	 * @return The number of dials which are currently available. Derived by
	 *         calculating the cost of a dial request, relative to the quantity
	 *         of energy which is immediately available to the Stargate.
	 */
	public double getRemainingDials();

	/**
	 * Gets the total remaining number of ticks which the gate can remain open
	 * for, based on the currently immediately available energy sources.
	 * 
	 * @return The number of ticks which the Stargate can remain open for,
	 *         ignoring the costs associated with opening the Stargate if it is
	 *         not already opened. Derived by calculating the energy consumption
	 *         per second, relative to the quantity of energy which is
	 *         immediately available to the Stargate.
	 */
	public double getRemainingConnectionTime();

}
