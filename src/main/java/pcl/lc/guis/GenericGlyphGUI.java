package pcl.lc.guis;

import net.minecraft.inventory.Container;
import pcl.common.base.GenericContainer;
import pcl.common.base.GenericContainerGUI;
import pcl.lc.LanteaCraft;
import pcl.lc.core.GateAddressHelper;

public abstract class GenericGlyphGUI extends GenericContainerGUI {

	public GenericGlyphGUI(GenericContainer container) {
		super(container);
	}

	public GenericGlyphGUI(Container container, int width, int height) {
		super(container, width, height);
	}

	protected void drawFramedSymbols(int x, int y, String address) {
		int symbolsPerRow = 9;
		int scale = 2;
		int frameWidth = 472 / scale;
		int frameHeight = 88 / scale;
		int x0 = x - frameWidth / 2;
		bindSGTexture("symbol_frame.png", 512 / scale, 128 / scale);
		drawTexturedRect(x0, y, frameWidth, frameHeight, 0, 0);

		GlyphRenderer.drawAddress(mc, address, x0, y, symbolsPerRow, scale, zLevel);
	}

	void drawAddressString(int x, int y, String address, int len, String padding, String caret) {
		StringBuilder result = new StringBuilder();
		result.append(address);
		if (len != result.length())
			result.append(caret);
		while (len > result.length())
			result.append(padding);
		drawCenteredString(fontRenderer, result.toString(), x, y, 0xffffff);
	}

	void bindSGTexture(String name, int usize, int vsize) {
		bindTexture(LanteaCraft.getResource("textures/gui/" + name), usize, vsize);
	}

}
