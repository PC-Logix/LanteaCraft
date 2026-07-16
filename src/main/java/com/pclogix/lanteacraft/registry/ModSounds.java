package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.StargateVariant;
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
    public static final DeferredHolder<SoundEvent, SoundEvent> NOX_STARGATE_ROLL = register("stargate_roll.nox");
    public static final DeferredHolder<SoundEvent, SoundEvent> NOX_STARGATE_CHEVRON_LOCK = register("stargate_chevron_lock.nox");
    public static final DeferredHolder<SoundEvent, SoundEvent> NOX_DHD_BUTTON = register("dhd_button.nox");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_STARGATE_ROLL = register("stargate_roll.wraith");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_STARGATE_CHEVRON_LOCK = register("stargate_chevron_lock.wraith");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_DHD_BUTTON = register("dhd_button.wraith");
    public static final DeferredHolder<SoundEvent, SoundEvent> PEGASUS_STARGATE_ROLL = register("stargate_roll.pegasus");
    public static final DeferredHolder<SoundEvent, SoundEvent> PEGASUS_STARGATE_CHEVRON_LOCK = register("stargate_chevron_lock.pegasus");
    public static final DeferredHolder<SoundEvent, SoundEvent> PEGASUS_DHD_BUTTON = register("dhd_button.pegasus");
    public static final DeferredHolder<SoundEvent, SoundEvent> MECHANICAL_IRIS_OPEN = register("mechanical_iris_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> MECHANICAL_IRIS_CLOSE = register("mechanical_iris_close");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENERGY_IRIS_ON = register("energy_iris_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENERGY_IRIS_OFF = register("energy_iris_off");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENERGY_IRIS_ACTIVE = register("energy_iris_active");
    public static final DeferredHolder<SoundEvent, SoundEvent> STAFF_WEAPON_FIRE = register("staff_weapon_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> P90_FIRE_SINGLE = register("p90_fire_single");
    public static final DeferredHolder<SoundEvent, SoundEvent> P90_FIRE_AUTO = register("p90_fire_auto");
    public static final DeferredHolder<SoundEvent, SoundEvent> P90_RELOAD = register("p90_reload");
    public static final DeferredHolder<SoundEvent, SoundEvent> P90_EMPTY = register("p90_empty");
    public static final DeferredHolder<SoundEvent, SoundEvent> ABYDOS_MUSIC = register("music.abydos");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXPEDITION_MUSIC = register("music.expeditions");

    private ModSounds() {
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }

    public static SoundEvent stargateRoll(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> STARGATE_ROLL.get();
            case NOX -> NOX_STARGATE_ROLL.get();
            case WRAITH -> WRAITH_STARGATE_ROLL.get();
            case PEGASUS -> PEGASUS_STARGATE_ROLL.get();
        };
    }

    public static SoundEvent stargateChevronLock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> STARGATE_CHEVRON_LOCK.get();
            case NOX -> NOX_STARGATE_CHEVRON_LOCK.get();
            case WRAITH -> WRAITH_STARGATE_CHEVRON_LOCK.get();
            case PEGASUS -> PEGASUS_STARGATE_CHEVRON_LOCK.get();
        };
    }

    public static SoundEvent dhdButton(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> DHD_BUTTON.get();
            case NOX -> NOX_DHD_BUTTON.get();
            case WRAITH -> WRAITH_DHD_BUTTON.get();
            case PEGASUS -> PEGASUS_DHD_BUTTON.get();
        };
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }
}
