package pcl.lc.guis;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.logging.Level;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.util.ResourceLocation;
import pcl.common.util.ImmutablePair;
import pcl.lc.LanteaCraft;
import pcl.lc.core.AddressingError;
import pcl.lc.core.AddressingError.CoordRangeError;
import pcl.lc.core.AddressingError.DimensionRangeError;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.common.base.GenericContainer;

public class ScreenStargateBase extends GenericGlyphGUI {

	private static String screenTitle = "Stargate Address";
	private static final int guiWidth = 256;
	private static final int guiHeight = 208;

	private ResourceLocation background;
	private TileEntityStargateBase te;
	private String address;

	private boolean mouseDown = false;
	private int clipboardAction = 0;

	public ScreenStargateBase(TileEntityStargateBase entity, EntityPlayer player) {
		super(new GenericContainer(guiWidth, guiHeight) {
			@Override
			public void sendStateTo(ICrafting crafter) {
			}
		});
		te = entity;
		background = LanteaCraft.getResource("textures/gui/sg_gui_" + LanteaCraft.getProxy().getRenderMode() + ".png");
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (clipboardAction > 0)
			clipboardAction--;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawForegroundLayer(int x, int y) {
		String address = getAddress();
		int cx = xSize / 2;
		drawFramedSymbols(cx, 22, address);
		textColor = 0x004c66;
		drawCenteredString(screenTitle, cx, 8);
		drawCenteredString(address, cx, 72);
		ImmutablePair<Integer, Integer> coord = transformMouseCoordinates(x, y);
		IconButtonRenderer.drawButton(Minecraft.getMinecraft(), (clipboardAction > 0) ? "tick" : "copy",
				(coord.getA() >= 232 && coord.getA() <= 232 + 12 && coord.getB() >= 70 && coord.getB() <= 70 + 12),
				mouseDown, 232, 70, 0.5, zLevel);
	}

	@Override
	public void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		bindTexture(background, 256, 256);
		drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
		GL11.glPopMatrix();
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		if (mouseButton == 0) {
			ImmutablePair<Integer, Integer> coord = transformMouseCoordinates(x, y);
			if (coord.getA() >= 232 && coord.getA() <= 232 + 12 && coord.getB() >= 70 && coord.getB() <= 70 + 12)
				mouseDown = true;
		}
		super.mouseClicked(x, y, mouseButton);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int which) {
		if (which == 0 || which == 1) {
			if (mouseDown) {
				ImmutablePair<Integer, Integer> coord = transformMouseCoordinates(x, y);
				if (coord.getA() >= 232 && coord.getA() <= 232 + 12 && coord.getB() >= 70 && coord.getB() <= 70 + 12)
					addressToClipboard();
				mouseDown = false;
			}
		}
		super.mouseMovedOrUp(x, y, which);
	}

	@Override
	protected void keyTyped(char c, int key) {
		if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))
			if (key == Keyboard.KEY_C)
				addressToClipboard();
		super.keyTyped(c, key);
	}

	private ImmutablePair<Integer, Integer> transformMouseCoordinates(int x, int y) {
		return new ImmutablePair<Integer, Integer>(x - guiLeft, y - guiTop);
	}

	private void addressToClipboard() {
		try {
			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
			Clipboard destination = Toolkit.getDefaultToolkit().getSystemClipboard();
			destination.setContents(new StringSelection(address), null);
			clipboardAction = 60;
		} catch (Throwable t) {
			LanteaCraft.getLogger().log(Level.WARNING, "Clipboard push failed!", t);
		}
	}

	private String getAddress() {
		if (address == null)
			try {
				address = te.getHomeAddress();
			} catch (CoordRangeError e) {
				address = "Coordinates out of stargate range";
			} catch (DimensionRangeError e) {
				address = "Dimension not reachable by stargate";
			} catch (AddressingError e) {
				LanteaCraft.getLogger().log(Level.INFO, "Addressing error!", e);
				address = "Stargate addressing error; check the log";
			}
		return address;
	}
}
