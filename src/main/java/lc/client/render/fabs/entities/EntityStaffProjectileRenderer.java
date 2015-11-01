package lc.client.render.fabs.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import lc.api.defs.IDefinitionReference;
import lc.common.base.LCEntityRenderer;
import lc.common.base.pipeline.LCEntityRenderPipeline;

public class EntityStaffProjectileRenderer extends LCEntityRenderer {

	public EntityStaffProjectileRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Class<? extends LCEntityRenderer> getParent() {
		return null;
	}

	@Override
	public boolean doRender(LCEntityRenderPipeline render, Entity e, double rpx, double rpy, double rpz, float yaw,
			float frame) {
		render.useEntityTexture(e);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) rpx, (float) rpy, (float) rpz);
		GL11.glRotatef(e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * frame - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * frame, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		byte b0 = 0;
		float f2 = 0.0F;
		float f3 = 0.5F;
		float f4 = (float) (0 + b0 * 10) / 32.0F;
		float f5 = (float) (5 + b0 * 10) / 32.0F;
		float f6 = 0.0F;
		float f7 = 0.15625F;
		float f8 = (float) (5 + b0 * 10) / 32.0F;
		float f9 = (float) (10 + b0 * 10) / 32.0F;
		float f10 = 0.05625F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float f11 = 0.0f; // (float) e.arrowShake - frame;

		if (f11 > 0.0F) {
			float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
			GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
		}

		GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(f10, f10, f10);
		GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
		GL11.glNormal3f(f10, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) f7, (double) f8);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) f7, (double) f9);
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) f6, (double) f9);
		tessellator.draw();
		GL11.glNormal3f(-f10, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) f6, (double) f8);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) f7, (double) f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) f7, (double) f9);
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f9);
		tessellator.draw();

		for (int i = 0; i < 4; ++i) {
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, f10);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double) f2, (double) f4);
			tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double) f3, (double) f4);
			tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double) f3, (double) f5);
			tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double) f2, (double) f5);
			tessellator.draw();
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		return true;
	}

	@Override
	public ResourceLocation getEntityTexture(LCEntityRenderPipeline render, Entity e) {
		// TODO Auto-generated method stub
		return null;
	}

}
