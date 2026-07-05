package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.menu.DhdPowerMenu;
import com.pclogix.lanteacraft.menu.NaquadahGeneratorMenu;
import com.pclogix.lanteacraft.menu.StargateMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, LanteaCraft.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<StargateMenu>> STARGATE = MENUS.register(
            "stargate",
            () -> IMenuTypeExtension.create(StargateMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<NaquadahGeneratorMenu>> NAQUADAH_GENERATOR = MENUS.register(
            "naquadah_generator",
            () -> IMenuTypeExtension.create(NaquadahGeneratorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<DhdPowerMenu>> DHD_POWER = MENUS.register(
            "dhd_power",
            () -> IMenuTypeExtension.create(DhdPowerMenu::new));

    private ModMenus() {
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
