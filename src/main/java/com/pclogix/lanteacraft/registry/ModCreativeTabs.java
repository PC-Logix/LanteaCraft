package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LanteaCraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LANTEACRAFT = CREATIVE_TABS.register(
            "lanteacraft",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lanteacraft"))
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() -> ModItems.STARGATE_RING.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STARGATE_RING);
                        output.accept(ModItems.STARGATE_CHEVRON);
                        output.accept(ModItems.STARGATE_BASE);
                        output.accept(ModItems.DHD);
                        output.accept(ModItems.NOX_STARGATE_RING);
                        output.accept(ModItems.NOX_STARGATE_CHEVRON);
                        output.accept(ModItems.NOX_STARGATE_BASE);
                        output.accept(ModItems.NOX_DHD);
                        output.accept(ModItems.WRAITH_STARGATE_RING);
                        output.accept(ModItems.WRAITH_STARGATE_CHEVRON);
                        output.accept(ModItems.WRAITH_STARGATE_BASE);
                        output.accept(ModItems.WRAITH_DHD);
                        output.accept(ModItems.PEGASUS_STARGATE_RING);
                        output.accept(ModItems.PEGASUS_STARGATE_CHEVRON);
                        output.accept(ModItems.PEGASUS_STARGATE_BASE);
                        output.accept(ModItems.PEGASUS_DHD);
                        output.accept(ModItems.TRANSPORT_RING);
                        output.accept(ModItems.BLANK_CRYSTAL);
                        output.accept(ModItems.CORE_CRYSTAL);
                        output.accept(ModItems.CONTROL_CRYSTAL);
                        output.accept(ModItems.MECHANICAL_IRIS);
                        output.accept(ModItems.ENERGY_IRIS);
                        output.accept(ModItems.GDO);
                        output.accept(ModItems.DECORATOR);
                        output.accept(ModItems.ADDRESS_TABLET);
                        output.accept(ModItems.NAQUADAH_ORE);
                        output.accept(ModItems.TRINIUM_ORE);
                        output.accept(ModItems.NAQUADAH);
                        output.accept(ModItems.TRINIUM);
                        output.accept(ModItems.NAQUADAH_INGOT);
                        output.accept(ModItems.TRINIUM_INGOT);
                        output.accept(ModItems.NAQUADAH_BLOCK);
                        output.accept(ModItems.TRINIUM_BLOCK);
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_TABS.register(modEventBus);
    }
}
