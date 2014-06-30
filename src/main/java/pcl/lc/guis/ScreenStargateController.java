package pcl.lc.guis;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import pcl.common.network.StandardModPacket;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumStargateState;
import pcl.lc.base.GenericContainerGUI;
import pcl.lc.client.audio.AudioPosition;
import pcl.lc.client.audio.SoundHost;
import pcl.lc.containers.ContainerStargateController;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;

public class ScreenStargateController extends GenericContainerGUI {

	final static int dhdWidth = 260;
	final static int dhdHeight = 180;
	final static double dhdRadius1 = dhdWidth * 0.1;
	final static double dhdRadius2 = dhdWidth * 0.275;
	final static double dhdRadius3 = dhdWidth * 0.45;

	private TileEntityStargateController controller;

	private ResourceLocation dhdLayer;
	private ResourceLocation dhdButtonLayer;

	private int dhdTop, dhdCentreX, dhdCentreY;
	private String enteredAddress = "", warningMessage = "";
	private int ticks = 0, ticksWarning = 0;

	private SoundHost soundHost;
	private AudioPosition soundHostPosition;

	public ScreenStargateController(TileEntityStargateController controller, EntityPlayer actor) {
		super(new ContainerStargateController(controller, actor), dhdWidth, dhdHeight);
		this.controller = controller;
		dhdLayer = LanteaCraft.getResource("textures/gui/dhd_gui.png");
		dhdButtonLayer = LanteaCraft.getResource("textures/gui/dhd_centre.png");
		soundHost = new SoundHost(controller);
		soundHostPosition = new AudioPosition(controller.getWorldObj(), new Vector3(controller));
		soundHost.addChannel("click", "stargate/milkyway/milkyway_dhd_button.ogg", soundHostPosition, 1.0f, 0);
	}

	TileEntityStargateBase getStargateTE() {
		return controller.getLinkedStargateTE();
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = 0;
		guiTop = 0;
		dhdTop = height - dhdHeight;
		dhdCentreX = width / 2;
		dhdCentreY = dhdTop + dhdHeight / 2;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		ticks++;
		if (ticks > 20)
			ticks = 0;
		if (ticksWarning > 0)
			ticksWarning--;
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		if (mouseButton == 0) {
			int i = findDHDButton(x, y);
			if (i >= 0) {
				soundHost.playChannel("click");
				dhdButtonPressed(i);
				return;
			}
		}
		super.mouseClicked(x, y, mouseButton);
	}

	int findDHDButton(int mx, int my) {
		int x = -(mx - dhdCentreX);
		int y = -(my - dhdCentreY) * dhdWidth / dhdHeight;
		double r = Math.hypot(x, y);
		if (r > dhdRadius3)
			return -1;
		if (r <= dhdRadius1)
			return 0;
		double a = Math.toDegrees(Math.atan2(y, x));
		if (a < 0)
			a += 360;
		int i0 = r > dhdRadius2 ? 1 : 15;
		return i0 + (int) Math.floor(a * 14 / 360);
	}

	private void dhdButtonPressed(int i) {
		if (i == 0)
			orangeButtonPressed(false);
		else if (i >= 27)
			backspace();
		else
			enterCharacter(GateAddressHelper.singleton().index(i - 1));
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
			if (GateAddressHelper.singleton().isLegal(C)) {
				soundHost.playChannel("click");
				enterCharacter(C.charAt(0));
			}
		}

		if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))
			if (key == Keyboard.KEY_V)
				try {
					String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
							.getData(DataFlavor.stringFlavor);
					for (char c1 : data.toCharArray())
						if (GateAddressHelper.singleton().isLegal(c1))
							enterCharacter(c1);
				} catch (Throwable t) {
					LanteaCraft.getLogger().log(Level.WARN, "Clipboard pull failed!", t);
					warningMessage = "Couldn't read the clipboard!";
					ticksWarning = 20 * 10;
				}
	}

	void orangeButtonPressed(boolean connectOnly) {
		TileEntityStargateBase te = getStargateTE();
		if (te != null)
			if (!connectOnly || !te.isConnected()) {
				StandardModPacket packet = new StandardModPacket(new WorldLocation(te));
				packet.setIsForServer(true);
				packet.setType("LanteaPacket.DialRequest");
				packet.setValue("Address", enteredAddress);
				LanteaCraft.getNetPipeline().sendToServer(packet);
				close();
			}
	}

	private void backspace() {
		int n = enteredAddress.length();
		if (n > 0)
			enteredAddress = enteredAddress.substring(0, n - 1);
	}

	private void enterCharacter(char c) {
		if (enteredAddress.length() < 9)
			enteredAddress = enteredAddress + c;
	}

	void drawEnteredSymbols() {
		drawFramedSymbols(width / 2, dhdTop - 60, enteredAddress);
	}

	void drawEnteredString() {
		drawAddressString(width / 2, dhdTop - 12, enteredAddress, 9, " ", (ticks > 10) ? "_" : " ");
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		bindTexture(dhdLayer);
		drawTexturedRect((width - dhdWidth) / 2, height - dhdHeight, dhdWidth, dhdHeight);

		bindTexture(dhdButtonLayer, 128, 64);
		GL11.glEnable(GL11.GL_BLEND);
		TileEntityStargateBase te = getStargateTE();
		boolean connected = te != null && te.isConnected();
		if (te == null || !te.getAsStructure().isValid())
			setColor(0.2, 0.2, 0.2);
		else if (connected)
			setColor(1.0, 0.5, 0.0);
		else
			setColor(0.5, 0.25, 0.0);
		double rx = dhdWidth * 48 / 512.0;
		double ry = dhdHeight * 48 / (32.0 + 256.0);
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

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		TileEntityStargateBase te = getStargateTE();
		if (te != null)
			if (te.getState() == EnumStargateState.Idle) {
				drawEnteredSymbols();
				drawEnteredString();
			}
		if (ticksWarning > 0 && warningMessage != null)
			drawCenteredString(fontRendererObj, warningMessage, width / 2, dhdTop - 3, 0xffaaaa);
	}

}
