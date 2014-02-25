package pcl.lc.render.effects;

import org.lwjgl.opengl.GL11;

import pcl.common.util.Facing3;
import pcl.common.util.Vector3;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EffectBeam extends EntityFX {

	private final Vector3 origin;
	private final Vector3 destination;

	private final Vector3 delta;
	private final Vector3 deltaVelocity;

	private final double length;
	private Vector3 position;
	private Facing3 rotation;

	private final int duration;

	private int ticks;

	protected EffectBeam(World par1World, Vector3 origin, Vector3 destination, int duration) {
		super(par1World, origin.x, origin.y, origin.z);
		this.origin = origin;
		this.destination = destination;
		this.delta = this.destination.sub(origin);
		this.deltaVelocity = Vector3.zero;
		this.length = delta.mag();
		this.duration = duration;
	}

	protected EffectBeam(World par1World, Vector3 origin, Vector3 destination, double length, int duration) {
		super(par1World, origin.x, origin.y, origin.z);
		this.origin = origin;
		this.destination = destination;
		this.delta = this.destination.sub(origin);
		this.deltaVelocity = new Vector3(delta.x / duration, delta.y / duration, delta.z / duration);
		this.length = length;
		this.duration = duration;
	}

	public void initialize() {
		position = origin;
		rotation = new Facing3(0, 0);
		Vector3 d0, d1;
		if (deltaVelocity.equals(Vector3.zero))
			d0 = delta.mul(0.5d);
		else
			d0 = deltaVelocity.mul(0.5d);
		position.add(d0);
		d1 = d0.unitV();
		double dxdz = Math.sqrt(Math.pow(d1.x, 2) + Math.pow(d1.z, 2));
		rotation.yaw = Math.toDegrees(Math.atan2(dxdz, d1.y));
		rotation.pitch = Math.toDegrees(Math.atan2(d1.z, d1.x));
	}

	@Override
	public void onUpdate() {
		if (ticks > duration)
			setDead();
		if (deltaVelocity.mag() > 0)
			position.add(deltaVelocity);
		ticks++;
	}

	public void renderParticle(Tessellator tessellator, float partialTicks, float playerYawC, float playerPitchC,
			float playerYawS, float f4, float f5) {
		Vector3 renderOrigin = position.copy().add(deltaVelocity.mul(partialTicks));
		GL11.glPushMatrix();
		GL11.glTranslated(renderOrigin.x, renderOrigin.y, renderOrigin.z);
		GL11.glRotated(rotation.yaw, 1, 0, 0);
		GL11.glRotated(rotation.pitch, 0, 1, 0);

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glEnd();
		GL11.glPopMatrix();
	}
}
