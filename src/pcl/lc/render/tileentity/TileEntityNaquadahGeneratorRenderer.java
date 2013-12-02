package pcl.lc.render.tileentity;

import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityNaquadahGeneratorRenderer extends TileEntitySpecialRenderer {
	public ResourceLocation theTexture;
	private float scale = 1;

	public TileEntityNaquadahGeneratorRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		if (LanteaCraft.getProxy().isUsingModels()) {
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
			if (((TileEntityNaquadahGenerator) tileEntity).isActive) {
				theTexture = new ResourceLocation("pcl_lc", "textures/models/naquada_generator_on_"
						+ LanteaCraft.getProxy().getRenderMode() + ".png");
			} else {
				theTexture = new ResourceLocation("pcl_lc", "textures/models/naquada_generator_off_"
						+ LanteaCraft.getProxy().getRenderMode() + ".png");
			}
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(theTexture);
			LanteaCraft.Render.modelNaquadahGenerator.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
}