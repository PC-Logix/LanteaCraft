package pcl.lc.module.stargate.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericContainerGUI;
import pcl.lc.base.render.IconButtonRenderer;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.stargate.AddressingError;
import pcl.lc.module.stargate.AddressingError.CoordRangeError;
import pcl.lc.module.stargate.AddressingError.DimensionRangeError;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.util.ImmutablePair;

public class ScreenStargateBase extends GenericContainerGUI {
	private static final int guiWidth = 256;
	private static final int guiHeight = 208;

	private ResourceLocation background;
	private TileStargateBase te;
	private String address;

	private boolean mouseDown = false;
	private int clipboardAction = 0;

	public ScreenStargateBase(TileStargateBase entity, EntityPlayer player) {
		super(new ContainerStargateBase(entity, player));
		te = entity;
		background = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/gui/sg_gui_${TEX_QUALITY}.png"));
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
		drawCenteredString(I18n.format("screen.stargate_base.title"), cx, 8);
		drawCenteredString(address, cx, 72);
		ImmutablePair<Integer, Integer> coord = transformMouseCoordinates(x, y);
		IconButtonRenderer.drawButton(Minecraft.getMinecraft(), (clipboardAction > 0) ? "tick" : "copy",
				(coord.getA() >= 232 && coord.getA() <= 232 + 12 && coord.getB() >= 70 && coord.getB() <= 70 + 12),
				mouseDown, 232, 70, 0.5, zLevel);
		if (coord.getA() >= 232 && coord.getA() <= 232 + 12 && coord.getB() >= 70 && coord.getB() <= 70 + 12) {
			ArrayList list = new ArrayList();
			if (clipboardAction > 0)
				list.add("lc.clipboard.put_success.text");
			else
				list.add(I18n.format("lc.clipboard.put_address.text"));
			drawHoveringText(list, coord.getA() - 2, coord.getB() - 2, fontRendererObj);
		}
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
		if (which == 0 || which == 1)
			if (mouseDown) {
				ImmutablePair<Integer, Integer> coord = transformMouseCoordinates(x, y);
				if (coord.getA() >= 232 && coord.getA() <= 232 + 12 && coord.getB() >= 70 && coord.getB() <= 70 + 12)
					addressToClipboard();
				mouseDown = false;
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
			Clipboard destination = Toolkit.getDefaultToolkit().getSystemClipboard();
			destination.setContents(new StringSelection(address), null);
			clipboardAction = 60;
		} catch (Throwable t) {
			LanteaCraft.getLogger().log(Level.WARN, "Clipboard push failed!", t);
		}
	}

	private String getAddress() {
		if (address == null)
			try {
				address = te.getHomeAddress();
			} catch (CoordRangeError e) {
				address = I18n.format("lc.stargate.coord_out_of_range.text");
			} catch (DimensionRangeError e) {
				address = I18n.format("lc.stargate.dimension_out_of_range.text");
			} catch (AddressingError e) {
				LanteaCraft.getLogger().log(Level.INFO, "Addressing error!", e);
				address = I18n.format("lc.stargate.general_address_error.text");
			}
		return address;
	}
}
