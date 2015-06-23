package lc.client.render.gfx.particle;

import lc.client.opengl.LCEntityFX;
import lc.common.resource.ResourceAccess;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GFXDust extends LCEntityFX {

	private int particle = 0;
	private boolean flip = false;

	public GFXDust(World world, double x, double y, double z, float scale, float gravity, float drift) {
		super(world, x, y, z, drift * (world.rand.nextFloat() - 0.5f), drift * (world.rand.nextFloat() - 0.5f), drift
				* (world.rand.nextFloat() - 0.5f));
		particleRed = particleGreen = particleBlue = 1.0F;
		motionX = motionY = motionZ = 0.0D;
		particleGravity = gravity;
		particleAlpha = 0.22f + (0.25f * world.rand.nextFloat());
		particleScale = scale;
		particleMaxAge = 15 + world.rand.nextInt(11);
		noClip = false;
		setSize(0.01F, 0.01F);
		particle = world.rand.nextInt(3) * 8;
		flip = world.rand.nextBoolean();
	}

	@Override
	public void renderParticle(Tessellator tessellator, float frame, float rotX, float rotXZ, float rotZ, float rotYZ,
			float rotXY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, particleAlpha);
		int part = particle + (int) (particleAge / particleMaxAge * 7.0F);

		float u0 = part % 8 / 8.0F;
		float u1 = u0 + 0.125F;
		float v0 = part / 8 / 8.0F;
		float v1 = v0 + 0.125F;
		float s = particleScale;
		if (flip) {
			float ud = u0;
			u0 = u1;
			u1 = ud;
		}
		float ux = (float) (prevPosX + (posX - prevPosX) * frame - EntityFX.interpPosX);
		float uy = (float) (prevPosY + (posY - prevPosY) * frame - EntityFX.interpPosY);
		float uz = (float) (prevPosZ + (posZ - prevPosZ) * frame - EntityFX.interpPosZ);

		tessellator.setBrightness(getBrightnessForRender(frame));
		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
		tessellator.addVertexWithUV(ux - rotX * s - rotYZ * s, uy - rotXZ * s, uz - rotZ * s - rotXY * s, u1, v1);
		tessellator.addVertexWithUV(ux - rotX * s + rotYZ * s, uy + rotXZ * s, uz - rotZ * s + rotXY * s, u1, v0);
		tessellator.addVertexWithUV(ux + rotX * s + rotYZ * s, uy + rotXZ * s, uz + rotZ * s + rotXY * s, u0, v0);
		tessellator.addVertexWithUV(ux + rotX * s - rotYZ * s, uy - rotXZ * s, uz + rotZ * s - rotXY * s, u0, v1);
	}

	@Override
	public int getFXLayer() {
		return 2;
	}

	@Override
	public ResourceLocation getTextureForRender() {
		return ResourceAccess.getNamedResource("textures/fx/particle/dust.png");
	}

}
