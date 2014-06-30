package pcl.lc.render.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import pcl.common.render.GenericBlockRenderer;
import pcl.lc.LanteaCraft;
import pcl.lc.module.ModuleStargates;
import pcl.lc.render.models.StargateControllerModel;
import cpw.mods.fml.client.FMLClientHandler;

public class BlockStargateControllerRenderer extends GenericBlockRenderer {

	private ResourceLocation texture;

	public BlockStargateControllerRenderer() {
		texture = LanteaCraft.getResource("textures/models/dhd_off_" + LanteaCraft.getProxy().getRenderMode() + ".png");
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int renderID, RenderBlocks rb) {
		if (!LanteaCraft.getProxy().isUsingModels())
			super.renderWorldBlock(world, x, y, z, block, renderID, rb);
		return true;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {
		if (!LanteaCraft.getProxy().isUsingModels())
			super.renderInventoryBlock(block, metadata, modelID, rb);
		else {
			StargateControllerModel model = ModuleStargates.Render.modelController;
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glTranslatef(0.5f, 0.5f, 0.0f);
			GL11.glRotatef(45, 0, 1, 0);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			GL11.glTranslatef(-0.25f, -0.5f, 0.0f);
			GL11.glScalef(0.8f, 0.8f, 0.8f);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
			model.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}

	}
}
