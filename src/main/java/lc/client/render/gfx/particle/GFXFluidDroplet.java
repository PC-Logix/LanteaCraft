package lc.client.render.gfx.particle;

import lc.client.opengl.LCEntityFX;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GFXFluidDroplet extends LCEntityFX {

	private Material materialType;
	private int bobTimer;

	public GFXFluidDroplet(World world, double x, double y, double z, Material material) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.motionX = this.motionY = this.motionZ = 0.0D;

		if (material == Material.water) {
			this.particleRed = 0.0F;
			this.particleGreen = 0.0F;
			this.particleBlue = 1.0F;
		} else {
			this.particleRed = 1.0F;
			this.particleGreen = 0.0F;
			this.particleBlue = 0.0F;
		}

		this.setParticleTextureIndex(113);
		this.setSize(0.01F, 0.01F);
		this.particleGravity = 0.06F;
		this.materialType = material;
		this.bobTimer = 40;
		this.particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
		this.motionX = this.motionY = this.motionZ = 0.0D;
	}

	public int getBrightnessForRender(float p_70070_1_) {
		return this.materialType == Material.water ? super.getBrightnessForRender(p_70070_1_) : 257;
	}

	public float getBrightness(float p_70013_1_) {
		return this.materialType == Material.water ? super.getBrightness(p_70013_1_) : 1.0F;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.materialType == Material.water) {
			this.particleRed = 0.2F;
			this.particleGreen = 0.3F;
			this.particleBlue = 1.0F;
		} else {
			this.particleRed = 1.0F;
			this.particleGreen = 16.0F / (float) (40 - this.bobTimer + 16);
			this.particleBlue = 4.0F / (float) (40 - this.bobTimer + 8);
		}

		this.motionY -= (double) this.particleGravity;

		if (this.bobTimer-- > 0) {
			this.motionX *= 0.02D;
			this.motionY *= 0.02D;
			this.motionZ *= 0.02D;
			this.setParticleTextureIndex(113);
		} else {
			this.setParticleTextureIndex(112);
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.particleMaxAge-- <= 0) {
			this.setDead();
		}

		if (this.onGround) {
			if (this.materialType == Material.water) {
				this.setDead();
				this.worldObj.spawnParticle("splash", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			} else {
				this.setParticleTextureIndex(114);
			}

			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}

		Material material = this.worldObj.getBlock(MathHelper.floor_double(this.posX),
				MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial();

		if (material.isLiquid() || material.isSolid()) {
			double d0 = (double) ((float) (MathHelper.floor_double(this.posY) + 1) - BlockLiquid
					.getLiquidHeightPercent(this.worldObj.getBlockMetadata(MathHelper.floor_double(this.posX),
							MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))));

			if (this.posY < d0) {
				this.setDead();
			}
		}
	}

	@Override
	public ResourceLocation getTextureForRender() {
		// TODO Auto-generated method stub
		return null;
	}
}
