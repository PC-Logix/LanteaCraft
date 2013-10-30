package gcewing.sg;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class SGControllerTERenderer extends TileEntitySpecialRenderer {
	private SGControllerModel model = new SGControllerModel();

	
	public  SGControllerTERenderer() {
		System.out.println("SGControllerTERenderer()");
	}
	
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick)
    {
    	final ResourceLocation SGController = new ResourceLocation("gcewing_sg:textures/tileentity/stargate.png");
        System.out.println("renderTileEntityAt");
        
		float scale = 1;

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);

		// Scale, Translate, Rotate
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef((float)x, (float)y, (float)z);
		// angle, xaxis, yaxis, zaxis
		GL11.glRotatef(0F, 0F, 0F, 0F);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(SGController);

		model.renderPart("sg_controller");

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

    }

}
