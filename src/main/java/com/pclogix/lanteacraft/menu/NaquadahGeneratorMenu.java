package com.pclogix.lanteacraft.menu;

import com.pclogix.lanteacraft.block.entity.NaquadahGeneratorBlockEntity;
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

public class NaquadahGeneratorMenu extends AbstractContainerMenu {
    private static final int POWER_CRYSTAL_SLOT = NaquadahGeneratorBlockEntity.POWER_CRYSTAL_SLOT;
    private static final int FUEL_SLOT_START = NaquadahGeneratorBlockEntity.FUEL_SLOT_START;
    private static final int FUEL_SLOT_END = NaquadahGeneratorBlockEntity.FUEL_SLOT_END;
    private static final int MACHINE_SLOT_END = FUEL_SLOT_END;
    private static final int PLAYER_INVENTORY_START = MACHINE_SLOT_END;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final BlockPos blockPos;
    private final NaquadahGeneratorBlockEntity generator;
    private final DataSlot energyStored;
    private final DataSlot maxEnergyStored;

    public NaquadahGeneratorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, buffer.readBlockPos());
    }

    public NaquadahGeneratorMenu(int containerId, Inventory playerInventory, NaquadahGeneratorBlockEntity generator) {
        this(containerId, playerInventory, generator.getBlockPos(), generator, generator.fuelItems());
    }

    private NaquadahGeneratorMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        this(containerId,
                playerInventory,
                blockPos,
                playerInventory.player.level().getBlockEntity(blockPos) instanceof NaquadahGeneratorBlockEntity blockEntity ? blockEntity : null,
                playerInventory.player.level().getBlockEntity(blockPos) instanceof NaquadahGeneratorBlockEntity blockEntity ? blockEntity.fuelItems() : new ItemStackHandler(MACHINE_SLOT_END));
    }

    private NaquadahGeneratorMenu(int containerId, Inventory playerInventory, BlockPos blockPos, NaquadahGeneratorBlockEntity generator, ItemStackHandler fuelItems) {
        super(ModMenus.NAQUADAH_GENERATOR.get(), containerId);
        this.blockPos = blockPos.immutable();
        this.generator = generator;
        this.energyStored = DataSlot.standalone();
        this.maxEnergyStored = DataSlot.standalone();

        addSlot(new SlotItemHandler(fuelItems, POWER_CRYSTAL_SLOT, 80, 18));
        addSlot(new SlotItemHandler(fuelItems, FUEL_SLOT_START, 8, 94));
        addSlot(new SlotItemHandler(fuelItems, FUEL_SLOT_START + 1, 27, 94));
        addSlot(new SlotItemHandler(fuelItems, FUEL_SLOT_START + 2, 132, 94));
        addSlot(new SlotItemHandler(fuelItems, FUEL_SLOT_START + 3, 151, 94));
        addPlayerInventory(playerInventory);
        addDataSlot(energyStored);
        addDataSlot(maxEnergyStored);
    }

    @Override
    public void broadcastChanges() {
        if (generator != null) {
            energyStored.set(generator.energyStorage().getEnergyStored());
            maxEnergyStored.set(generator.energyStorage().getMaxEnergyStored());
        }
        super.broadcastChanges();
    }

    public int energyStored() {
        return energyStored.get();
    }

    public int maxEnergyStored() {
        return maxEnergyStored.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D) <= 64.0D
                && player.level().getBlockEntity(blockPos) instanceof NaquadahGeneratorBlockEntity;
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

        if (index < MACHINE_SLOT_END) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (slots.get(POWER_CRYSTAL_SLOT).mayPlace(stack)) {
            if (!moveItemStackTo(stack, POWER_CRYSTAL_SLOT, POWER_CRYSTAL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slots.get(FUEL_SLOT_START).mayPlace(stack)) {
            if (!moveItemStackTo(stack, FUEL_SLOT_START, FUEL_SLOT_END, false)) {
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
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 180));
        }
    }
}
