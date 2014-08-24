package pcl.lc.api.internal;

/**
 * Used to pass event signals from blocks to tile-entities.
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IBlockTileSignalable {

	/**
	 * Called from the host block when a block instance is placed.
	 */
	public void hostBlockPlaced();

	/**
	 * Called from the block when a block instance is destroyed.
	 */
	public void hostBlockDestroyed();
}
