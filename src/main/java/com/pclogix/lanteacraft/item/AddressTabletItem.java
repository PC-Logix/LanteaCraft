package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.client.AddressTabletClientActions;
import com.pclogix.lanteacraft.worldgen.FixedDimensionGates;
import com.pclogix.lanteacraft.worldgen.PlannedStargate;
import com.pclogix.lanteacraft.worldgen.PlannedStargateSavedData;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class AddressTabletItem extends Item {
    public static final String TAG_ADDRESS = "stargateAddress";
    public static final String TAG_DIMENSION = "stargateDimension";
    public static final String TAG_GATE_X = "gateX";
    public static final String TAG_GATE_Y = "gateY";
    public static final String TAG_GATE_Z = "gateZ";
    public static final String TAG_VILLAGE_X = "villageX";
    public static final String TAG_VILLAGE_Y = "villageY";
    public static final String TAG_VILLAGE_Z = "villageZ";
    public static final String TAG_FACING = "facing";
    public static final String TAG_VARIANT = "variant";

    public AddressTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        Optional<String> address = addressFromStack(stack);
        if (level.isClientSide) {
            AddressTabletClientActions.copyAddress(address);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static ItemStack forPlan(ItemStack stack, PlannedStargate plan) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_ADDRESS, plan.address());
            tag.putString(TAG_DIMENSION, plan.dimension().toString());
            tag.putInt(TAG_GATE_X, plan.basePos().getX());
            tag.putInt(TAG_GATE_Y, plan.basePos().getY());
            tag.putInt(TAG_GATE_Z, plan.basePos().getZ());
            tag.putInt(TAG_VILLAGE_X, plan.villagePos().getX());
            tag.putInt(TAG_VILLAGE_Y, plan.villagePos().getY());
            tag.putInt(TAG_VILLAGE_Z, plan.villagePos().getZ());
            tag.putString(TAG_FACING, plan.facing().getName());
            tag.putString(TAG_VARIANT, plan.variant().name());
        });
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.lanteacraft.address_tablet.named", plan.address()).withStyle(ChatFormatting.GOLD));
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && level instanceof ServerLevel serverLevel) {
            PlannedStargateSavedData plans = PlannedStargateSavedData.get(serverLevel);
            if (!hasAddress(stack)) {
                nearestDiscoverablePlan(plans, serverLevel, player.blockPosition()).ifPresent(plan -> forPlan(stack, plan));
            } else {
                planFromStack(stack).ifPresent(plans::remember);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (data.contains(TAG_ADDRESS)) {
            tooltip.add(Component.translatable("item.lanteacraft.address_tablet.address", data.getString(TAG_ADDRESS)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(
                    "item.lanteacraft.address_tablet.location",
                    data.getInt(TAG_GATE_X),
                    data.getInt(TAG_GATE_Z)).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("item.lanteacraft.address_tablet.untranslated").withStyle(ChatFormatting.GRAY));
        }
    }

    public static boolean hasAddress(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(TAG_ADDRESS);
    }

    public static Optional<String> addressFromStack(ItemStack stack) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!data.contains(TAG_ADDRESS)) {
            return Optional.empty();
        }

        String address = data.getString(TAG_ADDRESS).trim().toUpperCase();
        return address.isBlank() ? Optional.empty() : Optional.of(address);
    }

    public static Optional<PlannedStargate> findTabletPlan(MinecraftServer server, String address) {
        String normalizedAddress = address == null ? "" : address.trim().toUpperCase();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            for (ItemStack stack : player.getInventory().items) {
                Optional<PlannedStargate> plan = matchingPlan(stack, normalizedAddress);
                if (plan.isPresent()) {
                    return plan;
                }
            }
            for (ItemStack stack : player.getInventory().offhand) {
                Optional<PlannedStargate> plan = matchingPlan(stack, normalizedAddress);
                if (plan.isPresent()) {
                    return plan;
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<PlannedStargate> planFromStack(ItemStack stack) {
        if (!(stack.getItem() instanceof AddressTabletItem)) {
            return Optional.empty();
        }

        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!data.contains(TAG_ADDRESS) || !data.contains(TAG_DIMENSION) || !data.contains(TAG_GATE_X) || !data.contains(TAG_GATE_Z)) {
            return Optional.empty();
        }

        ResourceLocation dimension = ResourceLocation.parse(data.getString(TAG_DIMENSION));
        BlockPos basePos = new BlockPos(data.getInt(TAG_GATE_X), data.contains(TAG_GATE_Y) ? data.getInt(TAG_GATE_Y) : 64, data.getInt(TAG_GATE_Z));
        BlockPos villagePos = data.contains(TAG_VILLAGE_X)
                ? new BlockPos(data.getInt(TAG_VILLAGE_X), data.getInt(TAG_VILLAGE_Y), data.getInt(TAG_VILLAGE_Z))
                : basePos;
        Direction facing = data.contains(TAG_FACING) ? Direction.byName(data.getString(TAG_FACING)) : Direction.SOUTH;
        if (facing == null) {
            facing = Direction.SOUTH;
        }

        com.pclogix.lanteacraft.gate.StargateVariant variant = com.pclogix.lanteacraft.gate.StargateVariant.MILKY_WAY;
        if (data.contains(TAG_VARIANT)) {
            try {
                variant = com.pclogix.lanteacraft.gate.StargateVariant.valueOf(data.getString(TAG_VARIANT));
            } catch (IllegalArgumentException ignored) {
                variant = com.pclogix.lanteacraft.gate.StargateVariant.MILKY_WAY;
            }
        }

        return Optional.of(new PlannedStargate(data.getString(TAG_ADDRESS).trim().toUpperCase(), dimension, villagePos, basePos, facing, variant));
    }

    private static Optional<PlannedStargate> matchingPlan(ItemStack stack, String address) {
        return planFromStack(stack).filter(plan -> plan.address().equals(address));
    }

    private static Optional<PlannedStargate> nearestDiscoverablePlan(PlannedStargateSavedData plans, ServerLevel level, BlockPos origin) {
        return plans.plans(level).stream()
                .filter(plan -> !FixedDimensionGates.isFixedPlan(plan))
                .min(java.util.Comparator.comparingDouble(plan -> plan.basePos().distSqr(origin)));
    }
}
