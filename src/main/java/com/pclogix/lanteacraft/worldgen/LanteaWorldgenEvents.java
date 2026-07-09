package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.item.AddressTabletItem;
import com.pclogix.lanteacraft.loot.SetZpmEnergyFunction;
import com.pclogix.lanteacraft.registry.ModItems;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.event.LootTableLoadEvent;

public final class LanteaWorldgenEvents {
    private static final int DEBUG_TABLET_COUNT = 6;

    private static final Set<ResourceLocation> DUNGEON_LIKE_TABLES = Set.of(
            BuiltInLootTables.SIMPLE_DUNGEON.location(),
            BuiltInLootTables.ABANDONED_MINESHAFT.location(),
            BuiltInLootTables.STRONGHOLD_CORRIDOR.location(),
            BuiltInLootTables.STRONGHOLD_CROSSING.location(),
            BuiltInLootTables.STRONGHOLD_LIBRARY.location());

    private LanteaWorldgenEvents() {
    }

    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        if (name.equals(BuiltInLootTables.DESERT_PYRAMID.location())) {
            event.getTable().addPool(lanteaPool("desert_pyramid", 1.0F, 3.0F, 7.0F));
            event.getTable().addPool(tabletPool("desert_pyramid_tablets", 1.0F));
            event.getTable().addPool(expeditionTabletPool("desert_pyramid_expeditions", 0.18F));
            event.getTable().addPool(fixedTabletPool("desert_pyramid_abydos_tablet", 0.035F, true));
            event.getTable().addPool(fixedTabletPool("desert_pyramid_atlantis_tablet", 0.01F, false));
            event.getTable().addPool(zpmPool("desert_pyramid_zpm", 0.02F));
            return;
        }

        if (DUNGEON_LIKE_TABLES.contains(name)) {
            event.getTable().addPool(lanteaPool("dungeon", 0.55F, 1.0F, 3.0F));
            event.getTable().addPool(tabletPool("dungeon_tablets", 0.25F));
            event.getTable().addPool(expeditionTabletPool("dungeon_expeditions", 0.12F));
            event.getTable().addPool(fixedTabletPool("dungeon_abydos_tablet", 0.006F, true));
            event.getTable().addPool(fixedTabletPool("dungeon_atlantis_tablet", 0.0025F, false));
            event.getTable().addPool(zpmPool("dungeon_zpm", 0.005F));
            return;
        }

        if (name.getPath().startsWith("chests/")) {
            event.getTable().addPool(lanteaPool("world_chest", 0.16F, 1.0F, 2.0F));
            event.getTable().addPool(tabletPool("world_chest_tablets", 0.08F));
            event.getTable().addPool(expeditionTabletPool("world_chest_expeditions", 0.035F));
            event.getTable().addPool(fixedTabletPool("world_chest_abydos_tablet", 0.0025F, true));
            event.getTable().addPool(fixedTabletPool("world_chest_atlantis_tablet", 0.001F, false));
            event.getTable().addPool(zpmPool("world_chest_zpm", 0.001F));
        }
    }

    public static void fillDebugLootChest(ServerLevel level, ChestBlockEntity chest) {
        int slot = 0;
        for (PlannedStargate plan : tabletPlans(level, chest).stream().limit(DEBUG_TABLET_COUNT).toList()) {
            ItemStack tablet = new ItemStack(ModItems.ADDRESS_TABLET.get());
            chest.setItem(slot++, AddressTabletItem.forPlan(tablet, plan));
        }
        if (slot == 0) {
            PlannedStargateSavedData.get(level).nearest(level, chest.getBlockPos()).ifPresent(plan -> {
                ItemStack tablet = new ItemStack(ModItems.ADDRESS_TABLET.get());
                chest.setItem(0, AddressTabletItem.forPlan(tablet, plan));
            });
        }
        chest.setItem(slot++, new ItemStack(ModItems.STARGATE_BASE.get()));
        chest.setItem(slot++, new ItemStack(ModItems.STARGATE_RING.get(), 6));
        chest.setItem(slot++, new ItemStack(ModItems.STARGATE_CHEVRON.get(), 3));
        chest.setItem(slot++, new ItemStack(ModItems.DHD.get()));
        chest.setItem(slot++, new ItemStack(ModItems.EXPEDITION_ADDRESS_TABLET.get()));
        chest.setItem(slot++, new ItemStack(ModItems.CORE_CRYSTAL.get()));
        chest.setItem(slot++, new ItemStack(ModItems.CONTROL_CRYSTAL.get(), 2));
        chest.setChanged();
    }

    private static List<PlannedStargate> tabletPlans(ServerLevel level, ChestBlockEntity chest) {
        long salt = level.getSeed() ^ level.getGameTime() ^ chest.getBlockPos().asLong();
        return PlannedStargateSavedData.get(level).plans(level).stream()
                .filter(plan -> !FixedDimensionGates.isFixedPlan(plan))
                .sorted(Comparator.comparingLong(plan -> mix(salt, plan.address().hashCode())))
                .toList();
    }

    private static LootPool lanteaPool(String name, float chance, float minCount, float maxCount) {
        return LootPool.lootPool()
                .name(LanteaCraft.MODID + "_" + name)
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(ModItems.STARGATE_RING.get()).setWeight(7).apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount))))
                .add(LootItem.lootTableItem(ModItems.STARGATE_CHEVRON.get()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                .add(LootItem.lootTableItem(ModItems.STARGATE_BASE.get()).setWeight(2))
                .add(LootItem.lootTableItem(ModItems.DHD.get()).setWeight(2))
                .add(LootItem.lootTableItem(ModItems.BLANK_CRYSTAL.get()).setWeight(6).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                .add(LootItem.lootTableItem(ModItems.CONTROL_CRYSTAL.get()).setWeight(4))
                .add(LootItem.lootTableItem(ModItems.CORE_CRYSTAL.get()).setWeight(2))
                .add(LootItem.lootTableItem(ModItems.EIGHTH_CHEVRON_CRYSTAL.get()).setWeight(1))
                .build();
    }

    private static LootPool tabletPool(String name, float chance) {
        return LootPool.lootPool()
                .name(LanteaCraft.MODID + "_" + name)
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(ModItems.ADDRESS_TABLET.get()))
                .build();
    }

    private static LootPool expeditionTabletPool(String name, float chance) {
        return LootPool.lootPool()
                .name(LanteaCraft.MODID + "_" + name)
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(ModItems.EXPEDITION_ADDRESS_TABLET.get()))
                .build();
    }

    private static LootPool fixedTabletPool(String name, float chance, boolean abydos) {
        return LootPool.lootPool()
                .name(LanteaCraft.MODID + "_" + name)
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(abydos ? ModItems.ABYDOS_ADDRESS_TABLET.get() : ModItems.ATLANTIS_ADDRESS_TABLET.get()))
                .build();
    }

    private static LootPool zpmPool(String name, float chance) {
        return LootPool.lootPool()
                .name(LanteaCraft.MODID + "_" + name)
                .setRolls(ConstantValue.exactly(1.0F))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(zpmEntry(35, 5_000_000.0F, 25_000_000.0F))
                .add(zpmEntry(25, 25_000_000.0F, 100_000_000.0F))
                .add(zpmEntry(10, 100_000_000.0F, 300_000_000.0F))
                .add(zpmEntry(3, 300_000_000.0F, 600_000_000.0F))
                .add(zpmEntry(1, 600_000_000.0F, 850_000_000.0F))
                .build();
    }

    private static LootItem.Builder<?> zpmEntry(int weight, float minEnergy, float maxEnergy) {
        return LootItem.lootTableItem(ModItems.ZPM.get())
                .setWeight(weight)
                .apply(SetZpmEnergyFunction.setEnergy(UniformGenerator.between(minEnergy, maxEnergy)));
    }

    private static long mix(long seed, int value) {
        long state = seed ^ ((long)value * 0x9E3779B97F4A7C15L);
        state ^= state >>> 33;
        state *= 0xff51afd7ed558ccdL;
        state ^= state >>> 33;
        state *= 0xc4ceb9fe1a85ec53L;
        state ^= state >>> 33;
        return state;
    }
}
