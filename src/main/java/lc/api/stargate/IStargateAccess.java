package lc.api.stargate;

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

	public IrisType getIrisType();

	public IrisState getIrisState();

	public void transmit(MessagePayload payload);

	public void receive(MessagePayload payload);

	public void selectGlyph(char glyph);

	public void activateChevron();

	public void deactivateChevron();

	public int getActivatedChevrons();

	public Character[] getActivatedGlpyhs();

	public void engageStargate();

	public void disengateStargate();

}
