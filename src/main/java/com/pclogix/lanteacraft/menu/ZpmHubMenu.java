package com.pclogix.lanteacraft.menu;

import com.pclogix.lanteacraft.block.entity.ZpmHubBlockEntity;
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

public class ZpmHubMenu extends AbstractContainerMenu {
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 108;
    private static final int HOTBAR_Y = 166;
    private static final int ZPM_SLOT_START = 0;
    private static final int ZPM_SLOT_END = ZpmHubBlockEntity.ZPM_SLOTS;
    private static final int PLAYER_INVENTORY_START = ZPM_SLOT_END;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final BlockPos blockPos;
    private final ZpmHubBlockEntity hub;
    private final DataSlot energyStoredWord0;
    private final DataSlot energyStoredWord1;
    private final DataSlot energyStoredWord2;
    private final DataSlot energyStoredWord3;
    private final DataSlot maxEnergyStoredWord0;
    private final DataSlot maxEnergyStoredWord1;
    private final DataSlot maxEnergyStoredWord2;
    private final DataSlot maxEnergyStoredWord3;

    public ZpmHubMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, buffer.readBlockPos());
    }

    public ZpmHubMenu(int containerId, Inventory playerInventory, ZpmHubBlockEntity hub) {
        this(containerId, playerInventory, hub.getBlockPos(), hub, hub.zpmItems());
    }

    private ZpmHubMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId,
                playerInventory,
                blockPos,
                playerInventory.player.level().getBlockEntity(blockPos) instanceof ZpmHubBlockEntity blockEntity ? blockEntity : null,
                playerInventory.player.level().getBlockEntity(blockPos) instanceof ZpmHubBlockEntity blockEntity ? blockEntity.zpmItems() : new ItemStackHandler(ZPM_SLOT_END));
    }

    private ZpmHubMenu(int containerId, Inventory playerInventory, BlockPos blockPos, ZpmHubBlockEntity hub, ItemStackHandler zpmItems) {
        super(ModMenus.ZPM_HUB.get(), containerId);
        this.blockPos = blockPos.immutable();
        this.hub = hub;
        this.energyStoredWord0 = DataSlot.standalone();
        this.energyStoredWord1 = DataSlot.standalone();
        this.energyStoredWord2 = DataSlot.standalone();
        this.energyStoredWord3 = DataSlot.standalone();
        this.maxEnergyStoredWord0 = DataSlot.standalone();
        this.maxEnergyStoredWord1 = DataSlot.standalone();
        this.maxEnergyStoredWord2 = DataSlot.standalone();
        this.maxEnergyStoredWord3 = DataSlot.standalone();

        // slot 0 = player slot 1 = top left
        addSlot(new SlotItemHandler(zpmItems, 0, 62, 18));
        // slot 1 = player slot 2 = top right
        addSlot(new SlotItemHandler(zpmItems, 1, 98, 18));
        // slot 2 = player slot 3 = bottom middle
        addSlot(new SlotItemHandler(zpmItems, 2, 80, 50));
        addPlayerInventory(playerInventory);
        addDataSlot(energyStoredWord0);
        addDataSlot(energyStoredWord1);
        addDataSlot(energyStoredWord2);
        addDataSlot(energyStoredWord3);
        addDataSlot(maxEnergyStoredWord0);
        addDataSlot(maxEnergyStoredWord1);
        addDataSlot(maxEnergyStoredWord2);
        addDataSlot(maxEnergyStoredWord3);
    }

    @Override
    public void broadcastChanges() {
        if (hub != null) {
            setSplit(energyStoredWord0, energyStoredWord1, energyStoredWord2, energyStoredWord3, hub.zpmEnergyStored());
            setSplit(maxEnergyStoredWord0, maxEnergyStoredWord1, maxEnergyStoredWord2, maxEnergyStoredWord3, hub.zpmMaxEnergyStored());
        }
        super.broadcastChanges();
    }

    public long energyStored() {
        return joinSplit(energyStoredWord0, energyStoredWord1, energyStoredWord2, energyStoredWord3);
    }

    public long maxEnergyStored() {
        return Math.max(1L, joinSplit(maxEnergyStoredWord0, maxEnergyStoredWord1, maxEnergyStoredWord2, maxEnergyStoredWord3));
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) <= 64.0D
                && player.level().getBlockEntity(blockPos) instanceof ZpmHubBlockEntity;
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

        if (index >= ZPM_SLOT_START && index < ZPM_SLOT_END) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (slots.get(ZPM_SLOT_START).mayPlace(stack)) {
            if (!moveItemStackTo(stack, ZPM_SLOT_START, ZPM_SLOT_END, false)) {
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

    private static void setSplit(DataSlot word0, DataSlot word1, DataSlot word2, DataSlot word3, long value) {
        word0.set((int)(value & 0xFFFFL));
        word1.set((int)((value >>> 16) & 0xFFFFL));
        word2.set((int)((value >>> 32) & 0xFFFFL));
        word3.set((int)((value >>> 48) & 0xFFFFL));
    }

    private static long joinSplit(DataSlot word0, DataSlot word1, DataSlot word2, DataSlot word3) {
        return (word0.get() & 0xFFFFL)
                | ((word1.get() & 0xFFFFL) << 16)
                | ((word2.get() & 0xFFFFL) << 32)
                | ((word3.get() & 0xFFFFL) << 48);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * 18, HOTBAR_Y));
        }
    }
}
