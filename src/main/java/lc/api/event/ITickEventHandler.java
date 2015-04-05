package lc.api.event;

import cpw.mods.fml.relauncher.Side;

public interface ITickEventHandler {

	public void think(Side what);

}
