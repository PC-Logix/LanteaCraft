package pcl.lc.guis;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.logging.Level;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerStargateBase;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;

public class ScreenStargateBase extends GenericGlyphGUI {

	private static String screenTitle = "Stargate Address";
	private static final int guiWidth = 256;
	private static final int guiHeight = 208;

	private ResourceLocation background;
	private TileEntityStargateBase te;
	private String address;

	public ScreenStargateBase(TileEntityStargateBase entity, EntityPlayer player) {
		super(new ContainerStargateBase(entity, player), guiWidth, guiHeight);
		te = entity;
		background = LanteaCraft.getResource("textures/gui/sg_gui_" + LanteaCraft.getProxy().getRenderMode() + ".png");
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		String address = getAddress();
		int cx = xSize / 2;
		drawFramedSymbols(cx, 22, address);
		textColor = 0x004c66;
		drawCenteredString(screenTitle, cx, 8);
		drawCenteredString(address, cx, 72);
		IconButtonRenderer.drawButton(Minecraft.getMinecraft(), "copy", false, 232, 70, 0.5, zLevel);
	}

	@Override
	public void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		bindTexture(background, 256, 256);
		drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		if (mouseButton == 0 && x >= 232 && x <= 232 + 12 && y >= 70 && y <= 70 + 12)
			addressToClipboard();
		super.mouseClicked(x, y, mouseButton);
	}

	@Override
	protected void keyTyped(char c, int key) {
		if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))
			if (key == Keyboard.KEY_C)
				addressToClipboard();
		super.keyTyped(c, key);
	}

	private void addressToClipboard() {
		try {
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			Clipboard destination = Toolkit.getDefaultToolkit().getSystemClipboard();
			destination.setContents(new StringSelection(address), null);
		} catch (Throwable t) {
			LanteaCraft.getLogger().log(Level.WARNING, "Clipboard push failed!", t);
		}
	}

	private String getAddress() {
		if (address == null)
			try {
				address = te.getHomeAddress();
			} catch (GateAddressHelper.CoordRangeError e) {
				address = "Coordinates out of stargate range";
			} catch (GateAddressHelper.DimensionRangeError e) {
				address = "Dimension not reachable by stargate";
			} catch (GateAddressHelper.AddressingError e) {
				LanteaCraft.getLogger().log(Level.INFO, "Addressing error!", e);
				address = "Stargate addressing error; check the log";
			}
		return address;
	}
}
