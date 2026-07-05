package com.pclogix.lanteacraft.entity;

import com.pclogix.lanteacraft.item.AddressTabletItem;
import com.pclogix.lanteacraft.item.EnergyCrystalItem;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.worldgen.PlannedStargate;
import com.pclogix.lanteacraft.worldgen.PlannedStargateSavedData;
import com.pclogix.lanteacraft.worldgen.StargateVillageLocator;
import com.pclogix.lanteacraft.worldgen.StargateVillagePlanner;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TokraTraderEntity extends Villager {
    private static final int TABLET_DISCOVERY_RADIUS_CHUNKS = 64;
    private static final int TABLET_DISCOVERY_STEP_BLOCKS = 256;

    public TokraTraderEntity(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        equipDefenderGear();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        this.setVillagerData(new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, this.getVillagerData().getLevel()));
        equipDefenderGear();
        this.setPersistenceRequired();
        return result;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable("entity.lanteacraft.tokra_trader");
    }

    @Override
    public MerchantOffers getOffers() {
        MerchantOffers offers = super.getOffers();
        if (!this.level().isClientSide) {
            refreshAddressTabletOffers(offers, false);
        }
        return offers;
    }

    @Override
    public void restock() {
        super.restock();
        if (!this.level().isClientSide) {
            refreshAddressTabletOffers(super.getOffers(), true);
        }
    }

    @Override
    protected void updateTrades() {
        VillagerTrades.ItemListing[] trades = switch (this.getVillagerData().getLevel()) {
            case 2 -> new VillagerTrades.ItemListing[]{
                    sell(ModItems.CORE_CRYSTAL.get(), 12, 1, 10, 10),
                    chargedCrystal(35, 55, 1.25D, 30, 3, 12),
                    sell(ModItems.GDO.get(), 16, 1, 8, 15),
                    addressTablet(18, 1, 15)
            };
            case 3 -> new VillagerTrades.ItemListing[]{
                    sell(ModItems.MECHANICAL_IRIS.get(), 24, 1, 6, 20),
                    sell(ModItems.ENERGY_IRIS.get(), 36, 1, 4, 30),
                    chargedCrystal(45, 70, 1.6D, 40, 3, 20),
                    sell(ModItems.DECORATOR.get(), 20, 1, 6, 20)
            };
            case 4 -> new VillagerTrades.ItemListing[]{
                    sell(ModItems.STARGATE_RING.get(), 12, 1, 8, 20),
                    sell(ModItems.STARGATE_CHEVRON.get(), 16, 1, 6, 25),
                    chargedCrystal(60, 85, 2.0D, 52, 2, 30),
                    sell(ModItems.STARGATE_BASE.get(), 32, 1, 4, 30)
            };
            case 5 -> new VillagerTrades.ItemListing[]{
                    sell(ModItems.DHD.get(), 48, 1, 3, 50),
                    chargedCrystal(70, 95, 2.6D, 64, 2, 50),
                    sell(ModItems.TRANSPORT_RING.get(), 64, 1, 2, 60),
                    sell(ModItems.NOX_STARGATE_BASE.get(), 56, 1, 2, 50),
                    sell(ModItems.PEGASUS_STARGATE_BASE.get(), 56, 1, 2, 50),
                    sell(ModItems.WRAITH_STARGATE_BASE.get(), 56, 1, 2, 50)
            };
            default -> new VillagerTrades.ItemListing[]{
                    sell(ModItems.CONTROL_CRYSTAL.get(), 4, 1, 12, 5),
                    sell(ModItems.BLANK_CRYSTAL.get(), 8, 2, 12, 5),
                    chargedCrystal(30, 45, 1.0D, 24, 4, 8),
                    buy(ModItems.NAQUADAH.get(), 12, 1, 12, 5),
                    buy(ModItems.NAQUADAH_INGOT.get(), 4, 1, 12, 5)
            };
        };

        MerchantOffers offers = this.getOffers();
        addOffersFromItemListings(offers, trades, trades.length);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Villager.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    private void equipDefenderGear() {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    private static VillagerTrades.ItemListing sell(ItemLike item, int emeraldCost, int count, int maxUses, int xp) {
        return (trader, random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                new ItemStack(item, count),
                maxUses,
                xp,
                0.05F);
    }

    private static VillagerTrades.ItemListing buy(ItemLike item, int count, int emeralds, int maxUses, int xp) {
        return (trader, random) -> new MerchantOffer(
                new ItemCost(item, count),
                new ItemStack(Items.EMERALD, emeralds),
                maxUses,
                xp,
                0.05F);
    }

    private static VillagerTrades.ItemListing chargedCrystal(int minPercent, int maxPercent, double highChargeBias, int emeraldCost, int maxUses, int xp) {
        return (trader, random) -> {
            int percent = chargedCrystalPercent(random, minPercent, maxPercent, highChargeBias);
            int energy = Math.round(EnergyCrystalItem.CAPACITY * percent / 100.0F);
            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, emeraldCost),
                    DhdBlockEntity.chargedCrystal(energy),
                    maxUses,
                    xp,
                    0.05F);
        };
    }

    private static int chargedCrystalPercent(RandomSource random, int minPercent, int maxPercent, double highChargeBias) {
        int clampedMin = Math.max(30, Math.min(minPercent, maxPercent));
        int clampedMax = Math.min(99, Math.max(minPercent, maxPercent));
        double roll = Math.pow(random.nextDouble(), 1.0D / Math.max(1.0D, highChargeBias));
        return clampedMin + (int)Math.round((clampedMax - clampedMin) * roll);
    }

    private static VillagerTrades.ItemListing addressTablet(int emeraldCost, int maxUses, int xp) {
        return (trader, random) -> remoteTabletOffer(trader, random, emeraldCost, maxUses, xp).orElse(null);
    }

    private static Optional<MerchantOffer> remoteTabletOffer(Entity trader, RandomSource random, int emeraldCost, int maxUses, int xp) {
        if (!(trader.level() instanceof ServerLevel level)) {
            return Optional.empty();
        }

        Optional<PlannedStargate> plan = remoteTabletPlan(level, trader.blockPosition(), random);
        if (plan.isEmpty()) {
            discoverNearbyPlans(level, trader.blockPosition());
            plan = remoteTabletPlan(level, trader.blockPosition(), random);
        }
        if (plan.isEmpty()) {
            return Optional.empty();
        }

        ItemStack tablet = AddressTabletItem.forPlan(new ItemStack(ModItems.ADDRESS_TABLET.get()), plan.get());
        return Optional.of(new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                tablet,
                maxUses,
                xp,
                0.05F));
    }

    private static Optional<PlannedStargate> remoteTabletPlan(ServerLevel level, BlockPos traderPos, RandomSource random) {
        List<PlannedStargate> plans = PlannedStargateSavedData.get(level).plans(level).stream()
                .sorted(Comparator.comparingDouble(plan -> plan.basePos().distSqr(traderPos)))
                .skip(1)
                .toList();
        if (plans.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(plans.get(random.nextInt(plans.size())));
    }

    private void refreshAddressTabletOffers(MerchantOffers offers, boolean replaceAnyTablet) {
        if (this.getVillagerData().getLevel() < 2) {
            return;
        }

        boolean removed = offers.removeIf(offer -> isAddressTabletOffer(offer) && (replaceAnyTablet || isLocalTabletOffer(offer)));
        if (removed) {
            remoteTabletOffer(this, this.random, 18, 1, 15).ifPresent(offers::add);
        }
    }

    private boolean isLocalTabletOffer(MerchantOffer offer) {
        Optional<PlannedStargate> tabletPlan = AddressTabletItem.planFromStack(offer.getResult());
        if (tabletPlan.isEmpty() || !(this.level() instanceof ServerLevel level)) {
            return true;
        }

        Optional<PlannedStargate> nearestPlan = PlannedStargateSavedData.get(level).plans(level).stream()
                .min(Comparator.comparingDouble(plan -> plan.basePos().distSqr(this.blockPosition())));
        return nearestPlan.isPresent() && nearestPlan.get().address().equals(tabletPlan.get().address());
    }

    private static boolean isAddressTabletOffer(MerchantOffer offer) {
        return offer.getResult().is(ModItems.ADDRESS_TABLET.get());
    }

    private static void discoverNearbyPlans(ServerLevel level, BlockPos origin) {
        Set<BlockPos> villages = new LinkedHashSet<>();
        int blockRadius = TABLET_DISCOVERY_RADIUS_CHUNKS * 16;
        for (int x = -blockRadius; x <= blockRadius; x += TABLET_DISCOVERY_STEP_BLOCKS) {
            for (int z = -blockRadius; z <= blockRadius; z += TABLET_DISCOVERY_STEP_BLOCKS) {
                StargateVillageLocator.nearestVillage(level, origin.offset(x, 0, z), TABLET_DISCOVERY_RADIUS_CHUNKS, false)
                        .ifPresent(villages::add);
            }
        }

        PlannedStargateSavedData data = PlannedStargateSavedData.get(level);
        for (BlockPos village : villages) {
            if (StargateVillagePlanner.shouldHaveGate(level, village)) {
                data.remember(StargateVillagePlanner.plan(level, village));
            }
        }
    }
}
