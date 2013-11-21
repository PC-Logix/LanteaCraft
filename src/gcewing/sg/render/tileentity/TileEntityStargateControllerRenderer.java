package gcewing.sg.render.tileentity;

import gcewing.sg.SGCraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityStargateControllerRenderer extends TileEntitySpecialRenderer {
	private ResourceLocation theTexture;
	private float scale = 1;

	public TileEntityStargateControllerRenderer() {
		theTexture = new ResourceLocation("gcewing_sg", "textures/models/dhd_off_"
				+ SGCraft.getProxy().getRenderMode() + ".png");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		if (SGCraft.getProxy().isUsingModels()) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslated(x, y, z);
			int dir = tileEntity.getBlockMetadata();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (dir == 1 || dir == 3)
				GL11.glRotatef(dir * 90F, 0F, 1F, 0F);
			else if (dir == 0)
				GL11.glRotatef(-180F, 0F, 1F, 0F);
			else
				GL11.glRotatef(dir * 180F, 0F, 1F, 0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(theTexture);
			SGCraft.Render.modelController.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
}
