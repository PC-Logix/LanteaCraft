package com.pclogix.lanteacraft.menu;

import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class StargateMenu extends AbstractContainerMenu {
    private static final int IRIS_SLOT = 0;
    private static final int EIGHTH_CHEVRON_SLOT = 1;
    private static final int PLAYER_INVENTORY_START = 2;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final BlockPos basePos;
    private final StargateVariant variant;
    private final String address;
    private final StargateBaseBlockEntity baseEntity;

    public StargateMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, ClientData.read(buffer));
    }

    public StargateMenu(int containerId, Inventory playerInventory, StargateBaseBlockEntity base, StargateVariant variant, String address) {
        this(containerId, playerInventory, base.getBlockPos(), variant, address, base, base.irisItems(), base.eighthChevronItems());
    }

    private StargateMenu(int containerId, Inventory playerInventory, ClientData data) {
        this(containerId,
                playerInventory,
                data.basePos(),
                data.variant(),
                data.address(),
                playerInventory.player.level().getBlockEntity(data.basePos()) instanceof StargateBaseBlockEntity base ? base : null,
                playerInventory.player.level().getBlockEntity(data.basePos()) instanceof StargateBaseBlockEntity base ? base.irisItems() : new ItemStackHandler(1),
                playerInventory.player.level().getBlockEntity(data.basePos()) instanceof StargateBaseBlockEntity base ? base.eighthChevronItems() : new ItemStackHandler(1));
    }

    private StargateMenu(int containerId, Inventory playerInventory, BlockPos basePos, StargateVariant variant, String address, StargateBaseBlockEntity baseEntity, ItemStackHandler irisItems, ItemStackHandler eighthChevronItems) {
        super(ModMenus.STARGATE.get(), containerId);
        this.basePos = basePos.immutable();
        this.variant = variant;
        this.address = address;
        this.baseEntity = baseEntity;

        addSlot(new SlotItemHandler(irisItems, 0, 48, 99));
        addSlot(new SlotItemHandler(eighthChevronItems, 0, 76, 99));
        addPlayerInventory(playerInventory);
    }

    public BlockPos basePos() {
        return basePos;
    }

    public StargateVariant variant() {
        return variant;
    }

    public String address() {
        return address;
    }

    public StargateBaseBlockEntity baseEntity() {
        return baseEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(basePos.getX() + 0.5D, basePos.getY() + 0.5D, basePos.getZ() + 0.5D) <= 64.0D
                && player.level().getBlockEntity(basePos) instanceof StargateBaseBlockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return moved;
        }

        ItemStack stack = slot.getItem();
        moved = stack.copy();

        if (index == IRIS_SLOT || index == EIGHTH_CHEVRON_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(ModItems.EIGHTH_CHEVRON_CRYSTAL.get())) {
            if (!moveItemStackTo(stack, EIGHTH_CHEVRON_SLOT, EIGHTH_CHEVRON_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slots.get(IRIS_SLOT).mayPlace(stack)) {
            if (!moveItemStackTo(stack, IRIS_SLOT, IRIS_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= HOTBAR_START && index < HOTBAR_END && !moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return moved;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 48 + col * 18, 123 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 48 + col * 18, 181));
        }
    }

    private record ClientData(BlockPos basePos, StargateVariant variant, String address) {
        private static ClientData read(RegistryFriendlyByteBuf buffer) {
            return new ClientData(buffer.readBlockPos(), StargateVariant.valueOf(buffer.readUtf(32)), buffer.readUtf(16));
        }
    }
}
