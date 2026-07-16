package com.pclogix.lanteacraft.enchantment;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public final class ExplodingEnchantmentHandler {
    public static final ResourceKey<Enchantment> EXPLODING = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "exploding"));

    private ExplodingEnchantmentHandler() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ExplodingEnchantmentHandler::onProjectileImpact);
    }

    private static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!(projectile.level() instanceof ServerLevel level) || !Config.ENABLE_EXPLODING_ENCHANTMENT.getAsBoolean()) {
            return;
        }

        Holder<Enchantment> exploding = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(EXPLODING)
                .orElse(null);
        if (exploding == null) {
            return;
        }

        int enchantmentLevel = getEnchantmentLevel(projectile, exploding);
        if (enchantmentLevel <= 0) {
            return;
        }

        float radius = 1.5F + 0.75F * (Math.min(enchantmentLevel, 5) - 1);
        Entity owner = projectile.getOwner();
        Level.ExplosionInteraction interaction = owner instanceof Mob
                ? Level.ExplosionInteraction.MOB
                : Level.ExplosionInteraction.BLOCK;
        level.explode(owner, event.getRayTraceResult().getLocation().x, event.getRayTraceResult().getLocation().y,
                event.getRayTraceResult().getLocation().z, radius, false, interaction);
    }

    private static int getEnchantmentLevel(Projectile projectile, Holder<Enchantment> exploding) {
        ItemStack weapon = projectile.getWeaponItem();
        if (weapon != null && !weapon.isEmpty()) {
            return weapon.getEnchantmentLevel(exploding);
        }

        if (projectile.getOwner() instanceof LivingEntity owner) {
            return Math.max(
                    owner.getMainHandItem().getEnchantmentLevel(exploding),
                    owner.getOffhandItem().getEnchantmentLevel(exploding));
        }
        return 0;
    }
}
