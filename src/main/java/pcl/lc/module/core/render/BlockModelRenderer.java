package pcl.lc.module.core.render;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import pcl.lc.api.internal.HookedModelBase;
import pcl.lc.base.GenericBlockRenderer;

public class BlockModelRenderer extends GenericBlockRenderer {
	private static final HashMap<Block, HookedModelBase> modelMap = new HashMap<Block, HookedModelBase>();
	private static final HashMap<Block, Boolean> mapRenderWorldModel = new HashMap<Block, Boolean>();
	private static final HashMap<Block, Boolean> mapRenderInvModel = new HashMap<Block, Boolean>();

	public static void registerModelForBlock(Block ablock, HookedModelBase amodel, boolean modelInWorld,
			boolean modelInInv) {
		if (modelMap.containsKey(ablock))
			return;
		modelMap.put(ablock, amodel);
		mapRenderWorldModel.put(ablock, modelInWorld);
		mapRenderInvModel.put(ablock, modelInInv);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks rb) {
		if (!modelMap.containsKey(block))
			return;
		if (!mapRenderInvModel.get(block))
			return;
		HookedModelBase model = modelMap.get(block);

		GL11.glPushMatrix();
		model.preInventory(block, metadata, modelID, rb);
		model.render(null, 0, 0, 0, 0, 0, 0.0625f);
		model.postInventory(block, metadata, modelID, rb);
		GL11.glPopMatrix();
		return;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int renderID, RenderBlocks rb) {
		if (!modelMap.containsKey(block))
			return false;
		if (!mapRenderWorldModel.get(block))
			return true;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(0.5f, 0.5f, 0.0f);
		GL11.glRotatef(45, 0, 1, 0);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		GL11.glTranslatef(-0.6f, 0.0f, 0.0f);
		GL11.glScalef(1.5f, 1.5f, 1.5f);
		ModelBase model = modelMap.get(block);
		model.render(null, 0, 0, 0, 0, 0, 0.625f);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
	}

}
