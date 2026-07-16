package com.pclogix.lanteacraft.entity;

import com.pclogix.lanteacraft.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class StaffBlastEntity extends ThrowableProjectile {
    private static final float DAMAGE = 8.0F;
    private static final float BURN_SECONDS = 5.0F;
    private static final int MAX_LIFETIME = 80;

    public StaffBlastEntity(EntityType<? extends StaffBlastEntity> type, Level level) {
        super(type, level);
    }

    public StaffBlastEntity(Level level, LivingEntity shooter) {
        super(ModEntities.STAFF_BLAST.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }
        if (level().isClientSide) {
            if (hasEnchantment("flame")) {
                level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
            level().addParticle(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level() instanceof ServerLevel) {
            Entity target = result.getEntity();
            if (hasEnchantment("flame")) {
                target.igniteForSeconds(BURN_SECONDS);
            }
            target.hurt(damageSources().indirectMagic(this, getOwner()), getDamage());
        }
    }

    @Override
    public ItemStack getWeaponItem() {
        return getOwner() instanceof LivingEntity owner ? owner.getMainHandItem() : null;
    }

    private float getDamage() {
        ItemStack weapon = getWeaponItem();
        if (weapon == null || weapon.isEmpty()) {
            return DAMAGE;
        }

        int powerLevel = getEnchantmentLevel("power");
        return powerLevel > 0 ? DAMAGE + 0.5F + 0.5F * powerLevel : DAMAGE;
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

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide && hasEnchantment("flame")) {
            Entity owner = getOwner();
            if (!(owner instanceof Mob) || net.neoforged.neoforge.event.EventHooks.canEntityGrief(level(), owner)) {
                BlockPos firePos = result.getBlockPos().relative(result.getDirection());
                if (level().isEmptyBlock(firePos)) {
                    level().setBlockAndUpdate(firePos, BaseFireBlock.getState(level(), firePos));
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 18; i++) {
                double speed = 0.12D;
                level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), random.nextGaussian() * speed, random.nextGaussian() * speed, random.nextGaussian() * speed);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }
}
