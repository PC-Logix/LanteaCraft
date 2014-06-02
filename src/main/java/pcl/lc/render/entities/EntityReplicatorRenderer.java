package pcl.lc.render.entities;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;

public class EntityReplicatorRenderer extends RenderLiving {

	public EntityReplicatorRenderer() {
		super(new ModelReplicator(32, 32), 0.5f);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return LanteaCraft.getResource("textures/skins/BipedTetris.png");
	}

	@Override
	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2) {
		float f1 = 0.9375F;
		shadowSize = 0.5F;
		GL11.glScalef(f1, f1, f1);
	}

}
