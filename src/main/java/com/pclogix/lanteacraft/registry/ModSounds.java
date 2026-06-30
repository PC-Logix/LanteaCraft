package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, LanteaCraft.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> STARGATE_ROLL = register("stargate_roll");
    public static final DeferredHolder<SoundEvent, SoundEvent> STARGATE_CHEVRON_LOCK = register("stargate_chevron_lock");
    public static final DeferredHolder<SoundEvent, SoundEvent> STARGATE_OPEN = register("stargate_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> STARGATE_CLOSE = register("stargate_close");
    public static final DeferredHolder<SoundEvent, SoundEvent> STARGATE_AMBIENT = register("stargate_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DHD_BUTTON = register("dhd_button");
    public static final DeferredHolder<SoundEvent, SoundEvent> MECHANICAL_IRIS_OPEN = register("mechanical_iris_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> MECHANICAL_IRIS_CLOSE = register("mechanical_iris_close");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENERGY_IRIS_ON = register("energy_iris_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENERGY_IRIS_OFF = register("energy_iris_off");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENERGY_IRIS_ACTIVE = register("energy_iris_active");

    private ModSounds() {
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }
}
