package lc.client.render.gfx.beam;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import lc.client.opengl.LCEntityFX;
import lc.common.resource.ResourceAccess;
import lc.common.util.math.Vector3;

public class GFXBeam extends LCEntityFX {

	private Object from;
	private Object to;

	private GFXBeam(World world, Object src, Object dst, boolean solid, float alpha, int maxAge) {
		super(world, 0, 0, 0);
		from = src;
		to = dst;
		particleRed = particleGreen = particleBlue = 1.0F;
		motionX = motionY = motionZ = 0.0D;
		particleGravity = 0.0F;
		particleAlpha = alpha;
		particleScale = 1.0f;
		particleMaxAge = maxAge;
		noClip = false;
		setSize(0.01F, 0.01F);
	}

	@Override
	public ResourceLocation getTextureForRender() {
		return ResourceAccess.getNamedResource("textures/fx/beam/beam.png");
	}

	@Override
	public void renderParticle(Tessellator tessellator, float frame, float rotX, float rotXZ, float rotZ, float rotYZ,
			float rotXY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, particleAlpha);
		Vector3 destination = resolvePosition(to);
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
		Vector3 origin = resolvePosition(from);
		posX = origin.fx();
		posY = origin.fy();
		posZ = origin.fz();
	}

	private Vector3 resolvePosition(Object zz) {
		if (zz instanceof TileEntity)
			return new Vector3((TileEntity) zz).add(0.5f, 0.5f, 0.5f);
		if (zz instanceof Entity) {
			Entity ee = (Entity) zz;
			return new Vector3((Entity) ee).add(ee.width, ee.height, ee.width);
		}
		throw new IllegalArgumentException("Unknown or unsupported tracer: " + zz.getClass().getName());
	}

}
