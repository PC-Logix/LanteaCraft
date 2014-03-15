package pcl.lc.guis;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerStargateControllerEnergy;
import pcl.lc.tileentity.TileEntityStargateController;

public class ScreenStargateControllerEnergy extends GenericGlyphGUI {

	private ContainerStargateControllerEnergy container;

	public ScreenStargateControllerEnergy(TileEntityStargateController controller, EntityPlayer actor) {
		super(new ContainerStargateControllerEnergy(controller, actor), 177, 108);
		this.container = (ContainerStargateControllerEnergy) inventorySlots;
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		bindTexture(LanteaCraft.getResource("textures/gui/inventory.png"), 256, 256);
		drawTexturedRect(0, 40, 177, 108 - 30, 0, 0);

		bindTexture(LanteaCraft.getResource("textures/gui/dhd_powercrystal_slot.png"), 60, 60);
		drawTexturedRect(89 - 12, 0, 24, 24);

		bindTexture(LanteaCraft.getResource("textures/gui/inventory.png"), 256, 256);
		drawTexturedRectUV(49, 26, (80.0d * container.getStoredEnergy() / 100.0d), 12, 0, 0,
				(80.0d * container.getStoredEnergy()) / 25600.0d, 12.0d / 256.0d);

		GL11.glPopMatrix();
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		StringBuilder sg = new StringBuilder().append(container.getStoredEnergy());
		if (sg.substring(sg.indexOf(".") + 1).length() != 2)
			sg.append("0");
		sg.append("%");
		int dw = fontRenderer.getStringWidth(sg.toString());
		fontRenderer.drawString(sg.toString(), 49 + ((int) Math.floor((80d - dw) / 2)), 28,
				(container.getStoredEnergy() > 0.00d) ? 0xFFFFFF : 0x9F0101, true);

	}

}
