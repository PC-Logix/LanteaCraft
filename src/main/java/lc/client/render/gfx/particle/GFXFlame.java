package lc.client.render.gfx.particle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import lc.client.opengl.LCEntityFX;
import lc.common.resource.ResourceAccess;

public class GFXFlame extends LCEntityFX {

	public GFXFlame(World world, double x, double y, double z, float scale, float gravity, float drift) {
		super(world, x, y, z, 0.0f, drift * world.rand.nextFloat(), 0.0f);
		particleRed = particleGreen = particleBlue = 1.0F;
		particleGravity = 0.0f;
		particleAlpha = 0.66f + (0.20f * world.rand.nextFloat());
		particleScale = scale;
		particleMaxAge = 15 + world.rand.nextInt(11);
		noClip = false;
		setSize(0.01F, 0.01F);
	}

	@Override
	public void renderParticle(Tessellator tessellator, float frame, float rotX, float rotXZ, float rotZ, float rotYZ,
			float rotXY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, particleAlpha);
		float u0 = 0.0f, u1 = 0.5f;
		float v0 = 0.0f, v1 = 1.0f;

		float s = particleScale;
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
		return ResourceAccess.getNamedResource("textures/fx/particle/flame.png");
	}

}
