package lc.gui;

import java.util.HashMap;

import net.minecraft.inventory.Container;
import lc.common.base.ux.LCContainerGUI;
import lc.common.base.ux.LCContainerTab;
import lc.container.ContainerStargate;

public class GUIStargate extends LCContainerGUI {

	public GUIStargate() {
		super(new ContainerStargate(0, 0));
	}

	@Override
	protected HashMap<Integer, LCContainerTab> getTabs() {
		// TODO Auto-generated method stub
		return null;
	}

}
