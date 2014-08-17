/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package pcl.lc.api.access;

import pcl.lc.api.EnumIrisState;
import pcl.lc.api.EnumStargateState;
import pcl.lc.util.ChunkLocation;

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
	 * Fetches the raw state of the Stargate.
	 * 
	 * @return The raw state of the Stargate.
	 */
	public EnumStargateState getState();

	/**
	 * Fetches the chunk location of the Stargate.
	 * 
	 * @return The chunk location of the Stargate.
	 */
	public ChunkLocation getLocation();

	/**
	 * Fetches the state of the Stargate's iris.
	 * 
	 * @return The state of the Stargate's iris.
	 */
	public EnumIrisState getIrisState();

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
	 * Instructs the Stargate to dial to a particular glyph on the inner
	 * dialling ring. If the Stargate is already spinning, this has no effect.
	 * 
	 * @param glyph
	 *            The glyph to spin to.
	 * @return If the instruction was given to the Stargate successfully.
	 */
	public boolean spinToGlyph(char glyph);

	/**
	 * Instructs and waits for the Stargate to dial to a particular glyph on the
	 * inner dialling ring.
	 * 
	 * @param glpyh
	 *            The glyph to spin to.
	 * @return If the instruction was given to the Stargate successfully.
	 * @throws InterruptedException
	 *             If the thread is interrupted while waiting for the dialling
	 *             to occur, an InterruptedException will be thrown.
	 */
	public boolean spinAndWaitForGlyph(char glpyh) throws InterruptedException;

	/**
	 * Returns if the inner dialling ring is spinning.
	 * 
	 * @return If the inner dialling ring is spinning.
	 */
	public boolean isSpinning();

	/**
	 * Requests the inner dialling ring stop spinning immediately.
	 * 
	 * @return If the dialling ring has been stopped.
	 */
	public boolean stopSpinning();

	/**
	 * Instructs the Stargate to lock the current chevron waiting.
	 * 
	 * @return If the current chevron has been locked.
	 */
	public boolean lockChevron();

	/**
	 * Instructs the Stargate to unlock the last chevron written.
	 * 
	 * @return If the last chevron has been unlocked.
	 */
	public boolean unlockChevron();

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
