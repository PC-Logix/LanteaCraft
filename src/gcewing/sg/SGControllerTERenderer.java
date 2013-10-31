package gcewing.sg;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class SGControllerTERenderer extends TileEntitySpecialRenderer {
	//private IModelCustom model;
	private ResourceLocation theTexture;
	
	public  SGControllerTERenderer() {
		System.out.println("SGControllerTERenderer()");
	}
	
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
    {    

		if (SGControllerModel.INSTANCE == null) {
			SGControllerModel.loadSGControllerModel();
		}
		if (SGControllerModel.INSTANCE != null){
			theTexture = new ResourceLocation("gcewing_sg", "textures/blocks/controller_bottom.png");
			float scale = 1;

			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);

			// Scale, Translate, Rotate
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslatef((float)x, (float)y, (float)z);
			// angle, xaxis, yaxis, zaxis
			GL11.glRotatef(0F, 0F, 0F, 0F);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(theTexture);

			//model.renderPart("sg_controller");
			SGControllerModel.INSTANCE.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
    }

}
