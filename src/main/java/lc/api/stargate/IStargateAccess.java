package lc.api.stargate;

/**
 * Contract interface for Stargate access.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IStargateAccess {

	/**
	 * <p>
	 * Gets the Stargate type.
	 * </p>
	 * <p>
	 * On the server, this always returns the type of the Stargate. On the
	 * client, the cached type of the Stargate is returned or <code>null</code>
	 * if the type is currently not known.
	 * </p>
	 * 
	 * @return The Stargate type
	 */
	public StargateType getStargateType();

	/**
	 * <p>
	 * Gets the Stargate address.
	 * </p>
	 * <p>
	 * On the server, this always returns the address of the Chunk the Stargate
	 * is in. On the client, the cached address is returned or the
	 * <code>default address</code> if the address is currently not known.
	 * </p>
	 * 
	 * @return The Stargate address
	 */
	public StargateAddress getStargateAddress();

	/**
	 * Get the type of the iris on the Stargate
	 * 
	 * @return The type of the iris on the Stargate
	 */
	public IrisType getIrisType();

	/**
	 * Get the state of the iris
	 * 
	 * @return The state of the iris
	 */
	public IrisState getIrisState();

	/**
	 * Transmit a message through the Stargate
	 * 
	 * @param payload
	 *            The payload to send
	 */
	public void transmit(MessagePayload payload);

	/**
	 * Receive a message on the Stargate
	 * 
	 * @param payload
	 *            The payload to receive
	 */
	public void receive(MessagePayload payload);

	/**
	 * Select a glyph on the Stargate
	 * 
	 * @param glyph
	 *            The glyph to select
	 */
	public void selectGlyph(char glyph);

	/**
	 * Activates a chevron at the currently selected glyph
	 */
	public void activateChevron();

	/**
	 * Deactivates the last activated chevron
	 */
	public void deactivateChevron();

	/**
	 * Get the number of active chevrons on the Stargate
	 * 
	 * @return The number of active chevrons
	 */
	public int getActivatedChevrons();

	/**
	 * Get the values of the activated glyphs on the Stargate
	 * 
	 * @return The active glyphs on the Stargate
	 */
	public Character[] getActivatedGlpyhs();

	/**
	 * Engage the Stargate to establish a connection
	 */
	public void engageStargate();

	/**
	 * Disengage the Stargate to close a connection
	 */
	public void disengateStargate();

}
