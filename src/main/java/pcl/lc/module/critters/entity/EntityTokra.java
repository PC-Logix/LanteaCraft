package pcl.lc.module.critters.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import pcl.common.util.Vector3;
import pcl.lc.base.ai.EntityAICreep;
import pcl.lc.base.ai.EntityAIFleeLowHealth;
import pcl.lc.base.ai.EntityAIHurtByTargetExcept;
import pcl.lc.base.ai.EntityAIRememberHome;
import pcl.lc.base.ai.IHomingPigeon;

public class EntityTokra extends EntityCreature implements INpc, IRangedAttackMob, IHomingPigeon {

	private static Class<?>[] opponents = { EntitySlime.class, EntityCreeper.class, EntityBlaze.class,
			EntityEnderman.class, EntitySilverfish.class, EntitySkeleton.class, EntityCaveSpider.class,
			EntitySpider.class, EntityWitch.class, EntityZombie.class };

	private boolean hasHomeProperties = false;
	private int dimensionHome;
	private Vector3 locationHome;

	public EntityTokra(World par1World) {
		super(par1World);
		getNavigator().setBreakDoors(true);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));

		tasks.addTask(1, new EntityAIRememberHome(this, 30.0D, 1.1D));
		tasks.addTask(1, new EntityAIFleeLowHealth(this, 0.25, 1.1D));

		tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 15, 20.0F));

		tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.4D));

		tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(9, new EntityAIWatchClosest2(this, EntityTokra.class, 5.0F, 0.02F));
		tasks.addTask(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
		tasks.addTask(9, new EntityAIWander(this, 0.6D));
		tasks.addTask(9, new EntityAICreep(this, EntityPlayer.class, 5.0D, 5.0D, 0.6D));

		tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(1, new EntityAIHurtByTargetExcept(this, true, this.getClass()));

		for (Class<?> opponentType : opponents) {
			tasks.addTask(2, new EntityAIAttackOnCollide(this, opponentType, 0.6D, false));
			targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, opponentType, 0, true));
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
	}

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!hasHomeProperties) {
			hasHomeProperties = !hasHomeProperties;
			dimensionHome = dimension;
		}
		if (locationHome == null)
			locationHome = new Vector3(this);
		if (getRNG().nextInt(100) == 0)
			if (getMaxHealth() > getHealth())
				setHealth(getHealth() + 0.1f);
	}

	/**
	 * main AI tick function, replaces updateEntityActionState
	 */
	@Override
	protected void updateAITick() {
		super.updateAITick();
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow,
	 * gets into the saddle on a pig.
	 */
	@Override
	public boolean interact(EntityPlayer par1EntityPlayer) {
		return true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("dimensionHome", dimensionHome);
		if (locationHome != null)
			par1NBTTagCompound.setTag("locationHome", locationHome.toNBT());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.hasKey("dimensionHome"))
			dimensionHome = par1NBTTagCompound.getInteger("dimensionHome");
		hasHomeProperties = true;
		if (par1NBTTagCompound.hasKey("locationHome"))
			locationHome = new Vector3(par1NBTTagCompound.getCompoundTag("locationHome"));
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	@Override
	protected boolean canDespawn() {
		return false;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	@Override
	protected String getLivingSound() {
		return "mob.villager.idle";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected String getHurtSound() {
		return "mob.villager.hit";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected String getDeathSound() {
		return "mob.villager.death";
	}

	/**
	 * Called when the mob's health reaches 0.
	 */
	@Override
	public void onDeath(DamageSource par1DamageSource) {
		super.onDeath(par1DamageSource);
	}

	@Override
	public boolean allowLeashing() {
		return true;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
		EntityArrow entityarrow = new EntityArrow(worldObj, this, par1EntityLivingBase, 1.6F,
				14 - worldObj.difficultySetting.ordinal() * 4);
		entityarrow.setDamage(par2 * 2.0F + rand.nextGaussian() * 0.25D + worldObj.difficultySetting.ordinal() * 0.11F);
		playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(entityarrow);
	}

	@Override
	public int getHomeDimension() {
		return dimensionHome;
	}

	@Override
	public Vector3 getHomeLocation() {
		return locationHome;
	}
}
