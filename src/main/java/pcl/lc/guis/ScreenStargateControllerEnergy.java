package pcl.lc.guis;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerStargateControllerEnergy;
import pcl.lc.tileentity.TileEntityStargateController;

public class ScreenStargateControllerEnergy extends GenericGlyphGUI {

	public ScreenStargateControllerEnergy(TileEntityStargateController controller, EntityPlayer actor) {
		super(new ContainerStargateControllerEnergy(controller, actor), 256, 208);
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		ResourceLocation slot = LanteaCraft.getResource("textures/gui/dhd_powercrystal_slot.png");
		bindTexture(slot, 60, 60);
		drawTexturedRect(0, 0, 30, 30);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

}
