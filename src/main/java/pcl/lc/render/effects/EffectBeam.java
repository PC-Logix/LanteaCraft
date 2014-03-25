package pcl.lc.render.effects;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import pcl.common.network.StandardModPacket;
import pcl.common.util.Facing3;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;

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

	public static EntityFX fromPacket(StandardModPacket spacket) {
		World world = Minecraft.getMinecraft().theWorld;
		Vector3 o = (Vector3) spacket.getValue("origin");
		Vector3 d = (Vector3) spacket.getValue("destination");
		int duration = (Integer) spacket.getValue("duration");
		if (spacket.hasField("length")) {
			double length = (Double) spacket.getValue("length");
			return new EffectBeam(world, o, d, length, duration);
		} else
			return new EffectBeam(world, o, d, duration);
	}

	protected EffectBeam(World par1World, Vector3 origin, Vector3 destination, int duration) {
		super(par1World, origin.x, origin.y, origin.z);
		this.origin = origin;
		this.destination = destination;
		delta = this.destination.sub(origin);
		deltaVelocity = Vector3.zero;
		length = delta.mag();
		this.duration = duration;
		initialize();
	}

	protected EffectBeam(World par1World, Vector3 origin, Vector3 destination, double length, int duration) {
		super(par1World, origin.x, origin.y, origin.z);
		this.origin = origin;
		this.destination = destination;
		delta = this.destination.sub(origin);
		deltaVelocity = new Vector3(delta.x / duration, delta.y / duration, delta.z / duration);
		this.length = length;
		this.duration = duration;
		initialize();
	}

	public void initialize() {
		LanteaCraft.getLogger().log(Level.INFO, "Spawning EffectBeam!");
		if (0 >= duration)
			LanteaCraft.getLogger().log(Level.WARNING, "Spawned EffectBeam with no duration, this might end badly!");
		if (0 >= duration && length != 0.0D)
			LanteaCraft.getLogger().log(Level.WARNING, "Even worse, an animation was requested but with no duration.");
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
		if (duration > 0 && ticks > duration) {
			LanteaCraft.getLogger().log(Level.INFO, "EffectBeam is now dead.");
			setDead();
			return;
		}
		if (deltaVelocity.mag() > 0)
			position.add(deltaVelocity);
		ticks++;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks, float playerYawC, float playerPitchC,
			float playerYawS, float f4, float f5) {
		tessellator.draw();

		Vector3 renderOrigin = position.copy().add(deltaVelocity.mul(partialTicks));

		GL11.glPushMatrix();
		// GL11.glLoadIdentity();
		// GL11.glTranslated(renderOrigin.x, renderOrigin.y, renderOrigin.z);
		System.out.println(String.format("RenderAt: %s %s %s", renderOrigin.x, renderOrigin.y, renderOrigin.z));
		Minecraft.getMinecraft().renderEngine.bindTexture(LanteaCraft.getResource("textures/models/transport_rings_"
				+ LanteaCraft.getProxy().getRenderMode() + ".png"));

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3d(0, 0, 0);

		GL11.glTexCoord2d(1, 0);
		GL11.glVertex3d(32, 0, 0);

		GL11.glTexCoord2d(1, 1);
		GL11.glVertex3d(32, 32, 0);

		GL11.glTexCoord2d(0, 1);
		GL11.glVertex3d(0, 32, 0);

		GL11.glEnd();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();

		// Minecraft.getMinecraft().renderEngine.bindTexture(UtilsFX.getParticleTexture());

		tessellator.startDrawingQuads();
	}
}
