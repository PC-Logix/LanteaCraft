package lc.client.opengl;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class LCEntityFX extends EntityFX {

	protected LCEntityFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0.0d, 0.0d, 0.0d);
	}

	protected LCEntityFX(World world, double x, double y, double z, double mx, double my, double mz) {
		super(world, x, y, z, mx, my, mz);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
	}

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
