package pcl.lc.guis;

import java.util.logging.Level;

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
	protected void drawForegroundLayer() {
		String address = getAddress();
		int cx = xSize / 2;
		drawFramedSymbols(cx, 22, address);
		textColor = 0x004c66;
		drawCenteredString(screenTitle, cx, 8);
		drawCenteredString(address, cx, 72);
	}

	@Override
	public void drawBackgroundLayer() {
		bindTexture(background, 256, 256);
		drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
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
