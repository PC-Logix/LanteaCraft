package pcl.lc.render.entities;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityTokraRenderer extends RenderLiving {
	private static final ResourceLocation villagerTextures = new ResourceLocation(
			"textures/entity/villager/villager.png");

	/** Model of the villager. */
	protected ModelVillager villagerModel;

	public EntityTokraRenderer() {
		super(new ModelVillager(0.0F), 0.5F);
		villagerModel = (ModelVillager) mainModel;
	}

	protected int shouldVillagerRenderPass(EntityVillager par1EntityVillager, int par2, float par3) {
		return -1;
	}

	public void renderVillager(EntityVillager par1EntityVillager, double par2, double par4, double par6, float par8,
			float par9) {
		super.doRenderLiving(par1EntityVillager, par2, par4, par6, par8, par9);
	}

	protected ResourceLocation func_110902_a(EntityVillager par1EntityVillager) {
		return villagerTextures;
	}

	protected void renderVillagerEquipedItems(EntityVillager par1EntityVillager, float par2) {
		super.renderEquippedItems(par1EntityVillager, par2);
	}

	protected void preRenderVillager(EntityVillager par1EntityVillager, float par2) {
		float f1 = 0.9375F;

		if (par1EntityVillager.getGrowingAge() < 0) {
			f1 = (float) (f1 * 0.5D);
			shadowSize = 0.25F;
		} else
			shadowSize = 0.5F;

		GL11.glScalef(f1, f1, f1);
	}

	@Override
	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8,
			float par9) {
		renderVillager((EntityVillager) par1EntityLiving, par2, par4, par6, par8, par9);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before
	 * the model is rendered. Args: entityLiving, partialTickTime
	 */
	@Override
	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		preRenderVillager((EntityVillager) par1EntityLivingBase, par2);
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	@Override
	protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
		return shouldVillagerRenderPass((EntityVillager) par1EntityLivingBase, par2, par3);
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
		renderVillagerEquipedItems((EntityVillager) par1EntityLivingBase, par2);
	}

	public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8,
			float par9) {
		renderVillager((EntityVillager) par1EntityLivingBase, par2, par4, par6, par8, par9);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
		return func_110902_a((EntityVillager) par1Entity);
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
		renderVillager((EntityVillager) par1Entity, par2, par4, par6, par8, par9);
	}
}