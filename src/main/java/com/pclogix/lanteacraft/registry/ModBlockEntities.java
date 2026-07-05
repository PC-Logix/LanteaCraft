package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.block.entity.NaquadahGeneratorBlockEntity;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.block.entity.TransportRingBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LanteaCraft.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StargateBaseBlockEntity>> STARGATE_BASE = BLOCK_ENTITIES.register(
            "stargate_base",
            () -> BlockEntityType.Builder.of(
                    StargateBaseBlockEntity::new,
                    ModBlocks.STARGATE_BASE.get(),
                    ModBlocks.NOX_STARGATE_BASE.get(),
                    ModBlocks.WRAITH_STARGATE_BASE.get(),
                    ModBlocks.PEGASUS_STARGATE_BASE.get())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DhdBlockEntity>> DHD = BLOCK_ENTITIES.register(
            "dhd",
            () -> BlockEntityType.Builder.of(
                    DhdBlockEntity::new,
                    ModBlocks.DHD.get(),
                    ModBlocks.NOX_DHD.get(),
                    ModBlocks.WRAITH_DHD.get(),
                    ModBlocks.PEGASUS_DHD.get())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransportRingBlockEntity>> TRANSPORT_RING = BLOCK_ENTITIES.register(
            "transport_ring",
            () -> BlockEntityType.Builder.of(TransportRingBlockEntity::new, ModBlocks.TRANSPORT_RING.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NaquadahGeneratorBlockEntity>> NAQUADAH_GENERATOR = BLOCK_ENTITIES.register(
            "naquadah_generator",
            () -> BlockEntityType.Builder.of(NaquadahGeneratorBlockEntity::new, ModBlocks.NAQUADAH_GENERATOR.get()).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
