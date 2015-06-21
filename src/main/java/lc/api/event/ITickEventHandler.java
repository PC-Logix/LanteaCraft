package lc.api.event;

import cpw.mods.fml.relauncher.Side;

/**
 * Tick event handler interface.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ITickEventHandler {

	/**
	 * Called by the tick controller to tick this handler. The handler is not
	 * allowed to raise any exceptions or return a value.
	 * 
	 * @param what
	 *            The side the tick is occuring on.
	 */
	public void think(Side what);

}
