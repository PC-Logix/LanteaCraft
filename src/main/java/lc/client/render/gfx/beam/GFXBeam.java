package lc.client.render.gfx.beam;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import lc.client.opengl.LCEntityFX;
import lc.common.resource.ResourceAccess;
import lc.common.util.math.Vector3;

public class GFXBeam extends LCEntityFX {

	private Object from;
	private Object to;

	private Vector3 origin;
	private Vector3 destination;

	private int fins;
	private float scale;

	public GFXBeam(World world, Object src, Object dst, boolean solid, float alpha, int maxAge, int maxFins,
			float beamScale) {
		super(world, 0, 0, 0);
		from = src;
		origin = resolvePosition(from);
		to = dst;
		destination = resolvePosition(to);
		particleRed = particleGreen = particleBlue = 1.0F;
		motionX = motionY = motionZ = 0.0D;
		particleGravity = 0.0F;
		particleAlpha = alpha;
		particleScale = 1.0f;
		particleMaxAge = maxAge;
		noClip = false;
		scale = beamScale;
		fins = maxFins;
		setSize(0.01F, 0.01F);
	}

	@Override
	public ResourceLocation getTextureForRender() {
		return ResourceAccess.getNamedResource("textures/fx/beam/beam.png");
	}

	@Override
	public void renderParticle(Tessellator tessellator, float frame, float rotX, float rotXZ, float rotZ, float rotYZ,
			float rotXY) {
		tessellator.draw();

		GL11.glPushMatrix();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, particleAlpha);
		destination = resolvePosition(to);
		Vector3 brx = destination.sub(origin);
		float length = (float) brx.mag();

		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDepthMask(false);

		float ux = (float) (prevPosX + (posX - prevPosX) * frame - EntityFX.interpPosX);
		float uy = (float) (prevPosY + (posY - prevPosY) * frame - EntityFX.interpPosY);
		float uz = (float) (prevPosZ + (posZ - prevPosZ) * frame - EntityFX.interpPosZ);
		GL11.glTranslated(ux, uy, uz);

		double var44 = -0.15D * scale;
		double var17 = 0.15D * scale;

		double vdxdz = MathHelper.sqrt_double(brx.x * brx.x + brx.z * brx.z);
		float rotYaw = ((float) (Math.atan2(brx.x, brx.z) * 180.0D / 3.141592653589793D));
		float rotPitch = ((float) (Math.atan2(brx.y, vdxdz) * 180.0D / 3.141592653589793D));

		GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rotYaw, 0.0F, 0.0F, -1.0F);
		GL11.glRotatef(-rotPitch, 1.0F, 0.0F, 0.0F);

		for (int t = 0; t < fins; t++) {
			GL11.glRotatef(180.0F / (float) fins, 0.0F, 1.0F, 0.0F);
			tessellator.startDrawingQuads();
			tessellator.setBrightness(200);
			tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 1.0f);
			tessellator.addVertexWithUV(var44, var44, 0.0f, 0.0f, 0.0f);
			tessellator.addVertexWithUV(var44, var17 + length, 0.0f, 0.0f, length);
			tessellator.addVertexWithUV(var17, var17 + length, 0.0f, length, length);
			tessellator.addVertexWithUV(var17, var44, 0.0f, length, 0.0f);
			tessellator.draw();
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		tessellator.startDrawingQuads();
	}

	@Override
	public int getFXLayer() {
		return 2;
	}

	@Override
	public void onUpdate() {
		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}
		origin = resolvePosition(from);
		prevPosX = posX = origin.x;
		prevPosY = posY = origin.y;
		prevPosZ = posZ = origin.z;
	}

	private Vector3 resolvePosition(Object zz) {
		if (zz == null)
			throw new IllegalArgumentException("Can't trace null positional object.");
		if (zz instanceof TileEntity)
			return new Vector3((TileEntity) zz).add(0.5f, 0.5f, 0.5f);
		if (zz instanceof Entity) {
			Entity ee = (Entity) zz;
			return new Vector3((Entity) ee).add(ee.width, ee.height, ee.width);
		}
		if (zz instanceof Vector3)
			return (Vector3) zz;
		throw new IllegalArgumentException("Unknown or unsupported tracer: " + zz.getClass().getName());
	}

}
