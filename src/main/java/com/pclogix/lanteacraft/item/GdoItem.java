package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateEventDispatcher;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class GdoItem extends Item {
    private static final String TAG_ADDRESS = "gdoAddress";
    private static final String TAG_DIMENSION = "gdoDimension";
    private static final String TAG_CODE = "gdoCode";
    private static final double TRANSMIT_RANGE = 8.0D;

    public GdoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!(level instanceof ServerLevel serverLevel) || player == null) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Optional<StargateEntry> entry = StargateMultiblock.findEntryFrom(serverLevel, pos);
        if (entry.isPresent() && serverLevel.getBlockEntity(entry.get().basePos()) instanceof StargateBaseBlockEntity base) {
            pair(stack, base, entry.get(), player);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        CompoundTag data = data(stack);
        if (!data.contains(TAG_CODE) || !data.contains(TAG_ADDRESS)) {
            player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_unpaired").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        Optional<StargateEntry> transmitter = nearestConnectedGate(serverLevel, player.blockPosition());
        if (transmitter.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_no_wormhole").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        StargateNetworkSavedData network = StargateNetworkSavedData.get(serverLevel);
        Optional<StargateEntry> receiver = network.findConnectedDestination(transmitter.get().address())
                .or(() -> network.findIncomingSource(transmitter.get().address()));
        if (receiver.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_no_wormhole").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        String pairedAddress = data.getString(TAG_ADDRESS);
        if (!pairedAddress.equals(receiver.get().address())) {
            player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_wrong_gate").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        ServerLevel receiverLevel = serverLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, receiver.get().dimension()));
        if (receiverLevel == null || !(receiverLevel.getBlockEntity(receiver.get().basePos()) instanceof StargateBaseBlockEntity base)) {
            player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_wrong_gate").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        String code = data.getString(TAG_CODE);
        boolean accepted = base.authorizeGdo(code);
        StargateEventDispatcher.gdoSignal(receiver.get(), transmitter.get(), player, code, accepted);
        if (accepted) {
            base.openIris();
            player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_accepted").withStyle(ChatFormatting.GREEN), true);
            return InteractionResultHolder.success(stack);
        }

        player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_denied").withStyle(ChatFormatting.RED), true);
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        CompoundTag data = data(stack);
        if (data.contains(TAG_ADDRESS)) {
            tooltip.add(Component.translatable("item.lanteacraft.gdo.paired", data.getString(TAG_ADDRESS)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.lanteacraft.gdo.code", data.getString(TAG_CODE)).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("item.lanteacraft.gdo.unpaired").withStyle(ChatFormatting.GRAY));
        }
    }

    private static void pair(ItemStack stack, StargateBaseBlockEntity base, StargateEntry entry, Player player) {
        String code = generateCode();
        base.setGdoCode(code);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_ADDRESS, entry.address());
            tag.putString(TAG_DIMENSION, entry.dimension().toString());
            tag.putString(TAG_CODE, code);
        });
        player.displayClientMessage(Component.translatable("message.lanteacraft.gdo_paired", entry.address()).withStyle(ChatFormatting.GREEN), true);
    }

    private static Optional<StargateEntry> nearestConnectedGate(ServerLevel level, BlockPos pos) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        StargateEntry nearest = null;
        double nearestDistance = TRANSMIT_RANGE * TRANSMIT_RANGE;
        for (StargateEntry entry : network.entries()) {
            if (!entry.dimension().equals(level.dimension().location())) {
                continue;
            }
            if (network.findConnectedDestination(entry.address()).isEmpty() && network.findIncomingSource(entry.address()).isEmpty()) {
                continue;
            }

            double distance = entry.basePos().distSqr(pos);
            if (distance <= nearestDistance) {
                nearestDistance = distance;
                nearest = entry;
            }
        }

        return Optional.ofNullable(nearest);
    }

    private static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static String generateCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
