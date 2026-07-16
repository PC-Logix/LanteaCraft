package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.entity.GoauldSoldierEntity;
import com.pclogix.lanteacraft.entity.TokraTraderEntity;
import com.pclogix.lanteacraft.entity.StaffBlastEntity;
import com.pclogix.lanteacraft.entity.P90BulletEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, LanteaCraft.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<TokraTraderEntity>> TOKRA_TRADER = ENTITY_TYPES.register(
            "tokra_trader",
            () -> EntityType.Builder.of(TokraTraderEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("tokra_trader"));

    public static final DeferredHolder<EntityType<?>, EntityType<GoauldSoldierEntity>> GOAULD_SOLDIER = ENTITY_TYPES.register(
            "goauld_soldier",
            () -> EntityType.Builder.of(GoauldSoldierEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("goauld_soldier"));

    public static final DeferredHolder<EntityType<?>, EntityType<StaffBlastEntity>> STAFF_BLAST = ENTITY_TYPES.register(
            "staff_blast",
            () -> EntityType.Builder.<StaffBlastEntity>of(StaffBlastEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("staff_blast"));

    public static final DeferredHolder<EntityType<?>, EntityType<P90BulletEntity>> P90_BULLET = ENTITY_TYPES.register(
            "p90_bullet",
            () -> EntityType.Builder.<P90BulletEntity>of(P90BulletEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .build("p90_bullet"));

    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModEntities::registerAttributes);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TOKRA_TRADER.get(), TokraTraderEntity.createAttributes().build());
        event.put(GOAULD_SOLDIER.get(), GoauldSoldierEntity.createAttributes().build());
    }
}
