package lc.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import lc.api.stargate.StargateType;
import lc.client.models.ModelDHD;
import lc.common.base.LCBlockRenderer;

public class BlockDHDRenderer extends LCBlockRenderer {

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		return null;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		if (ModelDHD.$ == null)
			return false;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0.5f, 0.5f, 0.0f);
		GL11.glRotatef(45, 0, 1, 0);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		GL11.glTranslatef(-0.25f, -0.5f, 0.0f);
		GL11.glScalef(0.8f, 0.8f, 0.8f);
		ModelDHD.$.prepareAndRender(StargateType.fromOrdinal(metadata), false);
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
		return true;
	}

}
