package com.pclogix.lanteacraft.entity;

import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.registry.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class GoauldSoldierEntity extends Skeleton {
    private static final double RANGED_MOVE_SPEED = 1.0D;
    private static final int RANGED_ATTACK_INTERVAL = 30;
    private static final float RANGED_ATTACK_RANGE = 18.0F;
    private static final float STAFF_BLAST_SPEED = 2.0F;

    public GoauldSoldierEntity(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
        setLeftHanded(false);
        equipStaffWeapon();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        setLeftHanded(false);
        equipStaffWeapon();
        return result;
    }

    @Override
    public void reassessWeaponGoal() {
        // The staff has its own ranged goal and is intentionally not a BowItem.
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, RANGED_MOVE_SPEED, RANGED_ATTACK_INTERVAL, RANGED_ATTACK_RANGE) {
            @Override
            public void start() {
                super.start();
                GoauldSoldierEntity.this.setAggressive(true);
            }

            @Override
            public void stop() {
                super.stop();
                GoauldSoldierEntity.this.setAggressive(false);
            }
        });
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        StaffBlastEntity blast = new StaffBlastEntity(level(), this);
        double x = target.getX() - blast.getX();
        double y = target.getY(0.5D) - blast.getY();
        double z = target.getZ() - blast.getZ();
        float inaccuracy = (float) Math.max(0, 8 - level().getDifficulty().getId() * 2);
        blast.shoot(x, y, z, STAFF_BLAST_SPEED, inaccuracy);
        level().addFreshEntity(blast);
        playSound(ModSounds.STAFF_WEAPON_FIRE.get(), 1.0F, 1.0F);
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable("entity.lanteacraft.goauld_soldier");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    private void equipStaffWeapon() {
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.STAFF_WEAPON.get()));
        setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

}
