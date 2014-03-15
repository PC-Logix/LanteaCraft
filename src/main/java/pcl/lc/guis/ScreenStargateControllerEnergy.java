package pcl.lc.guis;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerStargateControllerEnergy;
import pcl.lc.tileentity.TileEntityStargateController;

public class ScreenStargateControllerEnergy extends GenericGlyphGUI {

	public ScreenStargateControllerEnergy(TileEntityStargateController controller, EntityPlayer actor) {
		super(new ContainerStargateControllerEnergy(controller, actor), 177, 108);
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		bindTexture(LanteaCraft.getResource("textures/gui/dhd_inventory.png"), 256, 256);
		drawTexturedRect(0, 40, 177, 108 - 30, 0, 0);
		bindTexture(LanteaCraft.getResource("textures/gui/dhd_powercrystal_slot.png"), 60, 60);
		drawTexturedRect(89 - 12, 0, 24, 24);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

}
