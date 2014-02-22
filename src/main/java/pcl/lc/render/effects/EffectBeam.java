package pcl.lc.render.effects;

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
	private final int duration;

	private int ticks;

	protected EffectBeam(World par1World, Vector3 origin, Vector3 destination, double length, int duration) {
		super(par1World, origin.x, origin.y, origin.z);
		this.origin = origin;
		this.destination = destination;
		this.delta = destination.sub(origin);
		if (length > 0)
			this.deltaVelocity = new Vector3(delta.x / duration, delta.y / duration, delta.z / duration);
		else
			this.deltaVelocity = Vector3.zero;
		this.length = length;
		this.duration = duration;
	}

	@Override
	public void onUpdate() {
		if (ticks > duration)
			setDead();
		this.posX = origin.x + (ticks * deltaVelocity.x);
		this.posY = origin.y + (ticks * deltaVelocity.y);
		this.posZ = origin.z + (ticks * deltaVelocity.z);
		ticks++;
	}

	private Vector3 partialDeltaVector(float factor) {
		return new Vector3(posX + deltaVelocity.x * factor, posY + deltaVelocity.y * factor, posZ + deltaVelocity.z
				* factor);
	}

	public void renderParticle(Tessellator tessellator, float partialTicks, float playerYawC, float playerPitchC,
			float playerYawS, float f4, float f5) {
		Vector3 renderOrigin = partialDeltaVector(partialTicks);
	}
}
