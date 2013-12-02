package pcl.lc.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.container.ContainerStargateBase;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;

public class ScreenStargateBase extends GenericGlyphGUI {

	static String screenTitle = "Stargate Address";
	static final int guiWidth = 256;
	static final int guiHeight = 208;
	static final int fuelGaugeWidth = 16;
	static final int fuelGaugeHeight = 34;
	static final int fuelGaugeX = 214;
	static final int fuelGaugeY = 84;
	static final int fuelGaugeU = 0;
	static final int fuelGaugeV = 208;

	TileEntityStargateBase te;

	public static ScreenStargateBase create(EntityPlayer player, World world, int x, int y, int z) {
		TileEntityStargateBase te = TileEntityStargateBase.at(world, x, y, z);
		if (te != null)
			return new ScreenStargateBase(te, player);
		else
			return null;
	}

	public ScreenStargateBase(TileEntityStargateBase te, EntityPlayer player) {
		super(new ContainerStargateBase(te, player), guiWidth, guiHeight);
		this.te = te;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	// @Override
	// protected void keyTyped(char c, int key) {
	// if (key == Keyboard.KEY_ESCAPE)
	// close();
	// else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE) {
	// int n = te.homeAddress.length();
	// if (n > 0)
	// setAddress(te.homeAddress.substring(0, n - 1));
	// }
	// else {
	// String s = String.valueOf(c).toUpperCase();
	// if (SGBaseTE.isValidSymbolChar(s) && te.homeAddress.length() < 7)
	// setAddress(te.homeAddress + s);
	// }
	// }

	@Override
	public void drawBackgroundLayer() {
		bindTexture(
				LanteaCraft.getInstance().getResource("textures/gui/sg_gui_" + LanteaCraft.getProxy().getRenderMode() + ".png"),
				256, 256);
		drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
		drawFuelGauge();
		String address = getAddress();
		int cx = xSize / 2;
		drawFramedSymbols(cx, 22, address);
		textColor = 0x004c66;
		drawCenteredString(screenTitle, cx, 8);
		drawCenteredString(address, cx, 72);
		drawString("Fuel", 150, 96);
	}

	void drawFuelGauge() {
		int level = fuelGaugeHeight * te.fuelBuffer / TileEntityStargateBase.maxFuelBuffer;
		GL11.glEnable(GL11.GL_BLEND);
		drawTexturedRect(fuelGaugeX, fuelGaugeY + fuelGaugeHeight - level, fuelGaugeWidth, level, fuelGaugeU,
				fuelGaugeV);
		GL11.glDisable(GL11.GL_BLEND);
	}

	String getAddress() {
		// return te.homeAddress;
		// return "TESTING";
		try {
			return te.getHomeAddress();
		} catch (GateAddressHelper.CoordRangeError e) {
			return "Coordinates out of stargate range";
		} catch (GateAddressHelper.DimensionRangeError e) {
			return "Dimension not reachable by stargate";
		} catch (GateAddressHelper.AddressingError e) {
			throw new RuntimeException(e);
		}
	}

	void setAddress(String newAddress) {
		// te.setHomeAddress(newAddress);
	}

}
