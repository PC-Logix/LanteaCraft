package lc.entity;

import java.util.List;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@Definition(name = "staffProjectile", type = ComponentType.CORE, entityClass = EntityStaffProjectile.class)
public class EntityStaffProjectile extends Entity {

	private int blockPosX = -1;
	private int blockPosY = -1;
	private int blockPosZ = -1;
	private boolean inGround;
	private Block hitBlock;
	private int hitBlockMetadata;

	public Entity shootingEntity;
	private int ticksInGround;
	private int ticksInAir;
	private double damage = 2.0D;
	private int knockbackStrength;

	public EntityStaffProjectile(World world, EntityPlayer plr) {
		super(world);
		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(plr.posX, plr.posY + (double) plr.getEyeHeight(), plr.posZ, plr.rotationYaw,
				plr.rotationPitch);
		this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper
				.cos(this.rotationPitch / 180.0F * (float) Math.PI));
		this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper
				.cos(this.rotationPitch / 180.0F * (float) Math.PI));
		this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI));

	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D / Math.PI);
		}

		Block block = this.worldObj.getBlock(this.blockPosX, this.blockPosY, this.blockPosZ);

		if (block.getMaterial() != Material.air) {
			block.setBlockBoundsBasedOnState(this.worldObj, this.blockPosX, this.blockPosY, this.blockPosZ);
			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.blockPosX,
					this.blockPosY, this.blockPosZ);

			if (axisalignedbb != null
					&& axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if (this.inGround) {
			int j = this.worldObj.getBlockMetadata(this.blockPosX, this.blockPosY, this.blockPosZ);

			if (block == this.hitBlock && j == this.hitBlockMetadata) {
				++this.ticksInGround;
				if (this.ticksInGround == 1200)
					this.setDead();
			} else {
				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			Vec3 vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ
					+ this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec3, false, true, false);
			vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			vec3 = Vec3
					.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if (movingobjectposition != null)
				vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord);

			Entity entity = null;
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f1;

			for (i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5)) {
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

					if (movingobjectposition1 != null) {
						double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null)
				movingobjectposition = new MovingObjectPosition(entity);

			if (movingobjectposition != null && movingobjectposition.entityHit != null
					&& movingobjectposition.entityHit instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;
				if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer
						&& !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer))
					movingobjectposition = null;
			}

			float f2;
			float f4;

			if (movingobjectposition != null) {
				if (movingobjectposition.entityHit != null) {
					f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY
							+ this.motionZ * this.motionZ);
					int k = MathHelper.ceiling_double_int((double) f2 * this.damage);

					DamageSource damagesource = new DamageSource("lantean_staff");

					if (this.shootingEntity == null) {
						// damagesource = DamageSource.causeArrowDamage(this,
						// this);
					} else {
						// damagesource = DamageSource.causeArrowDamage(this,
						// this.shootingEntity);
					}

					if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float) k)) {
						if (movingobjectposition.entityHit instanceof EntityLivingBase) {
							EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;

							if (this.knockbackStrength > 0) {
								f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
								if (f4 > 0.0F) {
									movingobjectposition.entityHit.addVelocity(this.motionX
											* (double) this.knockbackStrength * 0.6000000238418579D / (double) f4,
											0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D
													/ (double) f4);
								}
							}

							if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity
									&& movingobjectposition.entityHit instanceof EntityPlayer
									&& this.shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler
										.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
							}
						}

						this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					} else {
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.blockPosX = movingobjectposition.blockX;
					this.blockPosY = movingobjectposition.blockY;
					this.blockPosZ = movingobjectposition.blockZ;
					this.hitBlock = this.worldObj.getBlock(this.blockPosX, this.blockPosY, this.blockPosZ);
					this.hitBlockMetadata = this.worldObj.getBlockMetadata(this.blockPosX, this.blockPosY,
							this.blockPosZ);
					this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
					this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
					this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
					f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY
							+ this.motionZ * this.motionZ);
					this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
					this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
					this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
					this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;

					if (this.hitBlock.getMaterial() != Material.air) {
						this.hitBlock.onEntityCollidedWithBlock(this.worldObj, this.blockPosX, this.blockPosY,
								this.blockPosZ, this);
					}
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

			for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f2) * 180.0D / Math.PI); this.rotationPitch
					- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
				;

			while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
				this.prevRotationPitch += 360.0F;
			while (this.rotationYaw - this.prevRotationYaw < -180.0F)
				this.prevRotationYaw -= 360.0F;
			while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
				this.prevRotationYaw += 360.0F;

			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			f1 = 0.05F;

			if (this.isInWater())
				this.setDead();

			this.motionX *= (double) f3;
			this.motionY *= (double) f3;
			this.motionZ *= (double) f3;
			this.motionY -= (double) f1;
			this.setPosition(this.posX, this.posY, this.posZ);
			this.func_145775_I();
		}
	}
}
