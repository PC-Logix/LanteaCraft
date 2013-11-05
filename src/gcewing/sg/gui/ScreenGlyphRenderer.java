//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate gui base class
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.gui;

import gcewing.sg.SGCraft;
import gcewing.sg.base.BaseContainer;
import gcewing.sg.base.BaseGuiContainer;
import gcewing.sg.tileentity.TileEntityStargateBase;
import net.minecraft.inventory.Container;

//------------------------------------------------------------------------------------------------

public class ScreenGlyphRenderer extends BaseGuiContainer {

	final static String padding = "-------";

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;

	public ScreenGlyphRenderer() {
		super(new BaseContainer(0, 0));
	}

	public ScreenGlyphRenderer(Container container, int width, int height) {
		super(container, width, height);
	}

	protected void drawAddressSymbols(int x, int y, String address) {
		int symbolsPerRow = 8;
		int scale = 2;
		int frameWidth = 472 / scale;
		int frameHeight = 88 / scale;
		int borderSize = 12 / scale;
		int cellSize = 64 / scale;
		int x0 = x - frameWidth / 2;
		bindSGTexture("symbol_frame.png", 512 / scale, 128 / scale);
		drawTexturedRect(x0, y, frameWidth, frameHeight, 0, 0);
		bindSGTexture("symbols.png", 512 / scale, 256 / scale);
		int n = address.length();
		for (int i = 0; i < n; i++) {
			int s = TileEntityStargateBase.charToSymbol(address.charAt(i));
			int row = s / symbolsPerRow;
			int col = s % symbolsPerRow;
			drawTexturedRect(x0 + borderSize + i * cellSize, y + borderSize, cellSize, cellSize, col * cellSize, row
					* cellSize);
		}
	}

	void drawAddressString(int x, int y, String address, String caret) {
		drawCenteredString(fontRenderer, padAddress(address, caret), x, y, 0xffffff);
	}

	String padAddress(String address, String caret) {
		return address + caret + padding.substring(address.length(), 7);
	}

	void bindSGTexture(String name) {
		bindSGTexture(name, 1, 1);
	}

	void bindSGTexture(String name, int usize, int vsize) {
		bindTexture(SGCraft.getInstance().getResource("textures/gui/" + name), usize, vsize);
	}

}
