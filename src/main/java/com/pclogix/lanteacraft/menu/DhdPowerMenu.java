package com.pclogix.lanteacraft.menu;

import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DhdPowerMenu extends AbstractContainerMenu {
    private static final int CRYSTAL_SLOT = 0;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final BlockPos blockPos;
    private final DhdBlockEntity dhd;
    private final DataSlot crystalEnergy;
    private final DataSlot crystalMaxEnergy;
    private final DataSlot runtimeSecondsLow;
    private final DataSlot runtimeSecondsHigh;

    public DhdPowerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, buffer.readBlockPos());
    }

    public DhdPowerMenu(int containerId, Inventory playerInventory, DhdBlockEntity dhd) {
        this(containerId, playerInventory, dhd.getBlockPos(), dhd, dhd.crystalItems());
    }

    private DhdPowerMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId,
                playerInventory,
                blockPos,
                playerInventory.player.level().getBlockEntity(blockPos) instanceof DhdBlockEntity blockEntity ? blockEntity : null,
                playerInventory.player.level().getBlockEntity(blockPos) instanceof DhdBlockEntity blockEntity ? blockEntity.crystalItems() : new ItemStackHandler(1));
    }

    private DhdPowerMenu(int containerId, Inventory playerInventory, BlockPos blockPos, DhdBlockEntity dhd, ItemStackHandler crystalItems) {
        super(ModMenus.DHD_POWER.get(), containerId);
        this.blockPos = blockPos.immutable();
        this.dhd = dhd;
        this.crystalEnergy = DataSlot.standalone();
        this.crystalMaxEnergy = DataSlot.standalone();
        this.runtimeSecondsLow = DataSlot.standalone();
        this.runtimeSecondsHigh = DataSlot.standalone();

        addSlot(new SlotItemHandler(crystalItems, CRYSTAL_SLOT, 81, 4));
        addPlayerInventory(playerInventory);
        addDataSlot(crystalEnergy);
        addDataSlot(crystalMaxEnergy);
        addDataSlot(runtimeSecondsLow);
        addDataSlot(runtimeSecondsHigh);
    }

    @Override
    public void broadcastChanges() {
        if (dhd != null) {
            crystalEnergy.set(dhd.crystalEnergyStored());
            crystalMaxEnergy.set(dhd.crystalMaxEnergyStored());
            long runtime = dhd.estimatedRuntimeSeconds();
            runtimeSecondsLow.set((int)(runtime & 0xFFFF));
            runtimeSecondsHigh.set((int)((runtime >>> 16) & 0xFFFF));
        }
        super.broadcastChanges();
    }

    public int crystalEnergy() {
        return crystalEnergy.get();
    }

    public int crystalMaxEnergy() {
        return Math.max(1, crystalMaxEnergy.get());
    }

    public long runtimeSeconds() {
        return ((long)runtimeSecondsHigh.get() << 16) | (runtimeSecondsLow.get() & 0xFFFFL);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) <= 64.0D
                && player.level().getBlockEntity(blockPos) instanceof DhdBlockEntity;
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

        if (index == CRYSTAL_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (slots.get(CRYSTAL_SLOT).mayPlace(stack)) {
            if (!moveItemStackTo(stack, CRYSTAL_SLOT, CRYSTAL_SLOT + 1, false)) {
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
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 47 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 105));
        }
    }
}
