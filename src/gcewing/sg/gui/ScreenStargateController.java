//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate controller gui screen
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.gui;

import gcewing.sg.SGCraft;
import gcewing.sg.core.GateAddressHelper;
import gcewing.sg.core.StargateNetworkChannel;
import gcewing.sg.core.EnumStargateState;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ScreenStargateController extends ScreenGlyphRenderer {

	final static int dhdWidth = 320;
	final static int dhdHeight = 120;
	final static double dhdRadius1 = dhdWidth * 0.1;
	final static double dhdRadius2 = dhdWidth * 0.275;
	final static double dhdRadius3 = dhdWidth * 0.45;

	World world;
	int x, y, z;
	int dhdTop, dhdCentreX, dhdCentreY;
	String enteredAddress = "";
	int closingDelay = 0;

	public ScreenStargateController(TileEntityStargateController controller, EntityPlayer actor) {
		this.world = controller.getWorldObj();
		this.x = controller.xCoord;
		this.y = controller.yCoord;
		this.z = controller.zCoord;
	}

	TileEntityStargateBase getStargateTE() {
		TileEntityStargateController cte = getControllerTE();
		if (cte != null)
			return cte.getLinkedStargateTE();
		else
			return null;
	}

	TileEntityStargateController getControllerTE() {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityStargateController)
			return (TileEntityStargateController) te;
		else
			return null;
	}

	@Override
	public void initGui() {
		dhdTop = height - dhdHeight;
		dhdCentreX = width / 2;
		dhdCentreY = dhdTop + dhdHeight / 2;
	}

	// @Override
	// public void onGuiClosed() {
	// }

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (closingDelay > 0)
			if (--closingDelay == 0)
				close();
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		// System.out.printf("SGControllerScreen.mouseClicked: %d, %d, %d\n", x,
		// y, mouseButton);
		if (mouseButton == 0) {
			int i = findDHDButton(x, y);
			if (i >= 0) {
				dhdButtonPressed(i);
				return;
			}
		}
		super.mouseClicked(x, y, mouseButton);
	}

	void closeAfterDelay(int ticks) {
		closingDelay = ticks;
	}

	int findDHDButton(int mx, int my) {
		// System.out.printf("SGControllerScreen.findDHDButton: mx = %d, my = %d, cx = %d, cy = %d\n",
		// mx, my, dhdCentreX, dhdCentreY);
		int x = -(mx - dhdCentreX);
		int y = -(my - dhdCentreY) * dhdWidth / dhdHeight;
		// System.out.printf("SGControllerScreen.findDHDButton: x = %d, y = %d\n",
		// x, y);
		double r = Math.hypot(x, y);
		if (r > dhdRadius3)
			return -1;
		if (r <= dhdRadius1)
			return 0;
		double a = Math.toDegrees(Math.atan2(y, x));
		// System.out.printf("SGControllerScreen.findDHDButton: a = %s\n", a);
		if (a < 0)
			a += 360;
		int i0 = r > dhdRadius2 ? 1 : 15;
		return i0 + (int) Math.floor(a * 14 / 360);
	}

	void dhdButtonPressed(int i) {
		// System.out.printf("SGControllerScreen.dhdButtonPressed: %d\n", i);
		buttonSound();
		if (i == 0)
			orangeButtonPressed(false);
		else if (i >= 27)
			backspace();
		else
			enterCharacter(TileEntityStargateBase.symbolToChar(i - 1));
	}

	void buttonSound() {
		mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
	}

	@Override
	protected void keyTyped(char c, int key) {
		if (key == Keyboard.KEY_ESCAPE)
			close();
		else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE)
			backspace();
		else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER)
			orangeButtonPressed(true);
		else {
			String C = String.valueOf(c).toUpperCase();
			if (GateAddressHelper.isValidSymbolChar(C))
				enterCharacter(C.charAt(0));
		}
	}

	void orangeButtonPressed(boolean connectOnly) {
		TileEntityStargateBase te = getStargateTE();
		if (te != null)
			if (!connectOnly || !te.isConnected()) {
				StargateNetworkChannel.sendConnectOrDisconnectToServer(te, enteredAddress);
				closeAfterDelay(10);
			}
	}

	void backspace() {
		buttonSound();
		int n = enteredAddress.length();
		if (n > 0)
			enteredAddress = enteredAddress.substring(0, n - 1);
	}

	void enterCharacter(char c) {
		buttonSound();
		int n = enteredAddress.length();
		if (n < 7)
			enteredAddress = enteredAddress + c;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		TileEntityStargateBase te = getStargateTE();
		drawBackgroundImage();
		drawOrangeButton();
		if (te != null)
			if (te.state == EnumStargateState.Idle) {
				drawEnteredSymbols();
				drawEnteredString();
			}
	}

	void drawBackgroundImage() {
		bindTexture(SGCraft.getInstance().getResource("textures/gui/dhd_gui.png"));
		drawTexturedRect((width - dhdWidth) / 2, height - dhdHeight, dhdWidth, dhdHeight);
	}

	void drawOrangeButton() {
		bindTexture(SGCraft.getInstance().getResource("textures/gui/dhd_centre.png"), 128, 64);
		GL11.glEnable(GL11.GL_BLEND);
		TileEntityStargateBase te = getStargateTE();
		boolean connected = te != null && te.state != EnumStargateState.Idle
				&& te.state != EnumStargateState.Disconnecting;
		if (te == null || !te.isMerged)
			setColor(0.2, 0.2, 0.2);
		else if (connected)
			setColor(1.0, 0.5, 0.0);
		else
			setColor(0.5, 0.25, 0.0);
		double rx = dhdWidth * 48 / 512.0;
		double ry = dhdHeight * 48 / 256.0;
		Tessellator.instance.disableColor();
		drawTexturedRect(dhdCentreX - rx, dhdCentreY - ry, 2 * rx, 1.5 * ry, 64, 0, 64, 48);
		resetColor();
		if (connected) {
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			double d = 5;
			drawTexturedRect(dhdCentreX - rx - d, dhdCentreY - ry - d, 2 * (rx + d), ry + d, 0, 0, 64, 32);
			drawTexturedRect(dhdCentreX - rx - d, dhdCentreY, 2 * (rx + d), 0.5 * ry + d, 0, 32, 64, 32);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	void drawEnteredSymbols() {
		drawAddressSymbols(width / 2, dhdTop - 80, enteredAddress);
	}

	void drawEnteredString() {
		drawAddressString(width / 2, dhdTop - 20, enteredAddress, "|");
	}

}
