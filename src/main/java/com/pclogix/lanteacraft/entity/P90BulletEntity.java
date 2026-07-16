package com.pclogix.lanteacraft.entity;

import com.pclogix.lanteacraft.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class P90BulletEntity extends ThrowableProjectile {
    private static final float DAMAGE = 6.0F;
    private static final float BURN_SECONDS = 5.0F;
    private static final int MAX_LIFETIME = 40;

    public P90BulletEntity(EntityType<? extends P90BulletEntity> type, Level level) {
        super(type, level);
    }

    public P90BulletEntity(Level level, LivingEntity shooter) {
        super(ModEntities.P90_BULLET.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > MAX_LIFETIME) {
            discard();
        }
        if (level().isClientSide && hasEnchantment("flame")) {
            level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.01D;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level() instanceof ServerLevel) {
            Entity target = result.getEntity();
            // Automatic fire arrives much faster than vanilla's general-purpose
            // hurt cooldown. Each distinct bullet still needs to count as a hit.
            target.invulnerableTime = 0;
            if (hasEnchantment("flame")) {
                target.igniteForSeconds(BURN_SECONDS);
            }
            boolean hurt = target.hurt(
                    damageSources().mobProjectile(this, getOwner() instanceof LivingEntity living ? living : null),
                    getDamage());
            int punchLevel = getEnchantmentLevel("punch");
            if (hurt && punchLevel > 0) {
                Vec3 horizontal = getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize();
                if (horizontal.lengthSqr() > 0.0D) {
                    target.push(horizontal.x * 0.6D * punchLevel, 0.1D, horizontal.z * 0.6D * punchLevel);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide && hasEnchantment("flame")) {
            BlockPos firePos = result.getBlockPos().relative(result.getDirection());
            if (level().isEmptyBlock(firePos)) {
                level().setBlockAndUpdate(firePos, BaseFireBlock.getState(level(), firePos));
            }
        }
    }

    @Override
    public ItemStack getWeaponItem() {
        return getOwner() instanceof LivingEntity owner ? owner.getMainHandItem() : null;
    }

    private boolean hasEnchantment(String name) {
        return getEnchantmentLevel(name) > 0;
    }

    private int getEnchantmentLevel(String name) {
        ItemStack weapon = getWeaponItem();
        if (weapon == null || weapon.isEmpty()) {
            return 0;
        }
        ResourceKey<Enchantment> key = ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.withDefaultNamespace(name));
        Holder<Enchantment> enchantment = level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(key)
                .orElse(null);
        return enchantment == null ? 0 : weapon.getEnchantmentLevel(enchantment);
    }

    private float getDamage() {
        int powerLevel = getEnchantmentLevel("power");
        return powerLevel > 0 ? DAMAGE + 0.5F + 0.5F * powerLevel : DAMAGE;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
