package com.pclogix.lanteacraft.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pclogix.lanteacraft.item.ZpmItem;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.registry.ModLootFunctions;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetZpmEnergyFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetZpmEnergyFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(NumberProviders.CODEC.fieldOf("energy").forGetter(function -> function.energy))
            .apply(instance, SetZpmEnergyFunction::new));

    private final NumberProvider energy;

    private SetZpmEnergyFunction(List<LootItemCondition> conditions, NumberProvider energy) {
        super(conditions);
        this.energy = energy;
    }

    @Override
    public LootItemFunctionType<SetZpmEnergyFunction> getType() {
        return ModLootFunctions.SET_ZPM_ENERGY.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return energy.getReferencedContextParams();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.is(ModItems.ZPM.get())) {
            stack.set(ModDataComponents.ENERGY, Mth.clamp(energy.getInt(context), 0, ZpmItem.capacity()));
        } else if (stack.is(ModItems.ENERGY_CRYSTAL.get())) {
            stack.set(ModDataComponents.ENERGY, Mth.clamp(energy.getInt(context), 0, com.pclogix.lanteacraft.item.EnergyCrystalItem.CAPACITY));
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> setEnergy(NumberProvider energy) {
        return simpleBuilder(conditions -> new SetZpmEnergyFunction(conditions, energy));
    }
}
