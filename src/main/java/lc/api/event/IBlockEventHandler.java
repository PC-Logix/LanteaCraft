/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.event;

/**
 * Contract interface for block-event handlers
 *
 * @author AfterLifeLochie
 *
 */
public interface IBlockEventHandler {

	/** Called when the host block is placed */
	public void blockPlaced();

	/** Called when the host block is broken */
	public void blockBroken();

}
