package pcl.lc.guis;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import pcl.common.base.GenericContainerGUI;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerStargateControllerEnergy;
import pcl.lc.tileentity.TileEntityStargateController;

public class ScreenStargateControllerEnergy extends GenericContainerGUI {

	private ContainerStargateControllerEnergy container;

	public ScreenStargateControllerEnergy(TileEntityStargateController controller, EntityPlayer actor) {
		super(new ContainerStargateControllerEnergy(controller, actor), 177, 108);
		container = (ContainerStargateControllerEnergy) inventorySlots;
	}

	@Override
	public void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		bindTexture(LanteaCraft.getResource(String.format("textures/gui/inventory_gui_%s.png", LanteaCraft.getProxy()
				.getRenderMode())), 256, 256);
		drawTexturedRect(0, 40, 177, 108, 0, 0);

		bindTexture(LanteaCraft.getResource("textures/gui/dhd_powercrystal_slot.png"), 60, 60);
		drawTexturedRect(89 - 12, 0, 24, 24);

		bindTexture(LanteaCraft.getResource(String.format("textures/gui/progressbar_gui_%s.png", LanteaCraft.getProxy()
				.getRenderMode())), 256, 256);
		drawTexturedRectUV(39, 26, 100, 12, 0, 0, 168 / 256d, 28 / 256d);
		drawTexturedRectUV(42, 28, 94.0d * (container.getStoredEnergy() / 100.0d), 8, 0, 29 / 256d,
				(160.0d * container.getStoredEnergy()) / 25600.0d, 12.0d / 256.0d);

		GL11.glPopMatrix();
	}

	@Override
	public void drawForegroundLayer(int mouseX, int mouseY) {
		StringBuilder sg = new StringBuilder().append(container.getStoredEnergy());
		if (sg.substring(sg.indexOf(".") + 1).length() != 2)
			sg.append("0");
		sg.append("%");
		sg.append(" ");

		int min, sec;
		sec = (int) Math.floor(container.getStoredEnergy() * 2.55);
		min = (int) Math.floor(sec / 60);
		sec = sec % 60;
		if (min > 0)
			sg.append(min).append("m ");
		sg.append(sec).append("s.");

		int dw = fontRenderer.getStringWidth(sg.toString());
		fontRenderer.drawString(sg.toString(), 49 + ((int) Math.floor((80d - dw) / 2)), 28,
				(container.getStoredEnergy() > 0.00d) ? 0xFFFFFF : 0x9F0101, true);

	}

}
