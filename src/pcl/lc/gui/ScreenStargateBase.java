package pcl.lc.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.container.ContainerStargateBase;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;

public class ScreenStargateBase extends GenericGlyphGUI {

	private static String screenTitle = "Stargate Address";
	private static final int guiWidth = 256;
	private static final int guiHeight = 208;
	private static final int fuelGaugeWidth = 16;
	private static final int fuelGaugeHeight = 34;
	private static final int fuelGaugeX = 214;
	private static final int fuelGaugeY = 84;
	private static final int fuelGaugeU = 0;
	private static final int fuelGaugeV = 208;

	private TileEntityStargateBase te;

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

	@Override
	protected void drawForegroundLayer() {
		drawFuelGauge();
		String address = getAddress();
		int cx = xSize / 2;
		drawFramedSymbols(cx, 22, address);
		textColor = 0x004c66;
		drawCenteredString(screenTitle, cx, 8);
		drawCenteredString(address, cx, 72);
		drawString("Fuel", 150, 96);
	}

	@Override
	public void drawBackgroundLayer() {
		bindTexture(
				LanteaCraft.getInstance().getResource(
						"textures/gui/sg_gui_" + LanteaCraft.getProxy().getRenderMode() + ".png"), 256, 256);
		drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
	}

	void drawFuelGauge() {
		int level = fuelGaugeHeight * te.fuelBuffer / TileEntityStargateBase.maxFuelBuffer;
		GL11.glEnable(GL11.GL_BLEND);
		drawTexturedRect(fuelGaugeX, fuelGaugeY + fuelGaugeHeight - level, fuelGaugeWidth, level, fuelGaugeU,
				fuelGaugeV);
		GL11.glDisable(GL11.GL_BLEND);
	}

	String getAddress() {
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
