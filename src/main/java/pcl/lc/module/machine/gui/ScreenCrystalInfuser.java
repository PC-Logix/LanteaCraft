package pcl.lc.module.machine.gui;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericContainerGUI;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.machine.tile.TileCrystalInfuser;
import net.minecraft.entity.player.EntityPlayer;

public class ScreenCrystalInfuser extends GenericContainerGUI {

	private TileCrystalInfuser infuser;
	private ContainerCrystalInfuser container;

	public ScreenCrystalInfuser(TileCrystalInfuser entity, EntityPlayer player) {
		super(new ContainerCrystalInfuser(entity, player));
		infuser = entity;
		container = (ContainerCrystalInfuser) inventorySlots;
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		bindTexture(ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/gui/inventory_gui_${TEX_QUALITY}.png")), 256, 256);
		drawTexturedRect(0, 73, 177, 108, 0, 0);

		bindTexture(ResourceAccess.getNamedResource("textures/gui/dhd_powercrystal_slot.png"), 60, 60);
		drawTexturedRect(46, 0, 24, 24);
		drawTexturedRect(46, 16 + 12, 24, 24);
		drawTexturedRect(96, 12, 24, 24);

		bindTexture(ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/gui/progressbar_gui_${TEX_QUALITY}.png")), 256, 256);
		drawTexturedRectUV(39, 60, 100, 12, 0, 0, 168 / 256d, 28 / 256d);
		drawTexturedRectUV(42, 60 + 2, 94.0d * (container.getCraftingProgress() / 100.0d), 8, 0, 29 / 256d,
				(160.0d * container.getCraftingProgress()) / 25600.0d, 12.0d / 256.0d);

		GL11.glPopMatrix();
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		StringBuilder sg = new StringBuilder().append((int) Math.floor(container.getCraftingProgress()));
		sg.append("%");

		int dw = fontRendererObj.getStringWidth(sg.toString());
		fontRendererObj.drawString(sg.toString(), 49 + ((int) Math.floor((80d - dw) / 2)), 62, 0xFFFFFF, true);

	}

}
