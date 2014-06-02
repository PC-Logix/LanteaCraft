package pcl.lc.render.entities;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.entity.EntityTokra;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityTokraRenderer extends RenderLiving {
	private static final ResourceLocation villagerTextures = LanteaCraft.getResource("textures/skins/BipedTetris.png");

	protected ModelBiped model;

	public EntityTokraRenderer() {
		super(new ModelBiped(), 0.5F);
		model = (ModelBiped) mainModel;
	}

	protected ResourceLocation func_110902_a(EntityTokra par1EntityVillager) {
		return villagerTextures;
	}

	@Override
	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8,
			float par9) {
		super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before
	 * the model is rendered. Args: entityLiving, partialTickTime
	 */
	@Override
	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		float f1 = 0.9375F;
		shadowSize = 0.5F;
		GL11.glScalef(f1, f1, f1);
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	@Override
	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return -1;
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		super.renderEquippedItems(par1EntityLivingBase, par2);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return func_110902_a((EntityTokra) par1Entity);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity) and this method has signature public
	 * void doRender(T entity, double d, double d1, double d2, float f, float
	 * f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving((EntityTokra) par1Entity, par2, par4, par6, par8, par9);
	}
}