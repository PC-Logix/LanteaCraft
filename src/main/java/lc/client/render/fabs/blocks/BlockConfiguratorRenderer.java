package lc.client.render.fabs.blocks;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import lc.client.models.ModelLaptop;
import lc.common.base.LCBlockRenderer;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;

public class BlockConfiguratorRenderer extends LCBlockRenderer {

	public BlockConfiguratorRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		if (ModelLaptop.$ == null)
			return false;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0.5f, 0.5f, 0.0f);
		GL11.glRotatef(-65, 0, 1, 0);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(ResourceAccess.getNamedResource("textures/models/laptop.png"));
		GL11.glTranslatef(0.0f, 1.0f, 0.0f);
		GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-0.8f, -0.5f, 0.7f);
		ModelLaptop.$.renderAll(-1.75f);
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		// TODO Auto-generated method stub
		return false;
	}

}
