package lc.client.opengl;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * LanteaCraft GFX entity stub
 * 
 * @author AfterLifeLochie
 *
 */
public abstract class LCEntityFX extends EntityFX {

	/**
	 * Default constructor; create a fixed-point entity effect with no movement.
	 * 
	 * @param world
	 *            The world to add the entity to
	 * @param x
	 *            The x-coordinate of the entity
	 * @param y
	 *            The y-coordinate of the entity
	 * @param z
	 *            The z-coordinate of the entity
	 */
	protected LCEntityFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0.0d, 0.0d, 0.0d);
	}

	/**
	 * Create a moving-point entity with a start origin and movement speed.
	 * 
	 * @param world
	 *            The world to add the entity to
	 * @param x
	 *            The x-coordinate of the entity
	 * @param y
	 *            The y-coordinate of the entity
	 * @param z
	 *            The z-coordinate of the entity
	 * @param mx
	 *            The movement x-speed of the entity
	 * @param my
	 *            The movement y-speed of the entity
	 * @param mz
	 *            The movement z-speed of the entity
	 */
	protected LCEntityFX(World world, double x, double y, double z, double mx, double my, double mz) {
		super(world, x, y, z, mx, my, mz);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
	}

	/**
	 * Get the texture for rendering the entity. The render manager will request
	 * and bind the texture provided here before calling the render methods.
	 * 
	 * @return The texture to bind.
	 */
	public abstract ResourceLocation getTextureForRender();

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.motionY -= 0.04D * (double) this.particleGravity;
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
	}

}
