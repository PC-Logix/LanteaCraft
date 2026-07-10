package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.item.ZpmItem;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ZpmHubBlockEntity extends BlockEntity {
    public static final int ZPM_SLOTS = 3;

    private ItemStackHandler zpmItems = createZpmItemHandler();
    private final IEnergyStorage energyOutput = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return extractZpmEnergy(Math.min(toExtract, Config.ZPM_HUB_MAX_EXTRACT.get()), simulate);
        }

        @Override
        public int getEnergyStored() {
            return clampToInt(zpmEnergyStored());
        }

        @Override
        public int getMaxEnergyStored() {
            return clampToInt(zpmMaxEnergyStored());
        }

        @Override
        public boolean canExtract() {
            return zpmEnergyStored() > 0;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    private ItemStackHandler createZpmItemHandler() {
        return new ItemStackHandler(ZPM_SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isZpm(stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            sync();
        }
        };
    }

    public ZpmHubBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ZPM_HUB.get(), pos, blockState);
    }

    public ItemStackHandler zpmItems() {
        return zpmItems;
    }

    public IEnergyStorage energyOutput() {
        return energyOutput;
    }

    public static ItemStack chargedZpm(int energy) {
        ItemStack stack = new ItemStack(ModItems.ZPM.get());
        stack.set(ModDataComponents.ENERGY, Mth.clamp(energy, 0, ZpmItem.capacity()));
        return stack;
    }

    public long zpmEnergyStored() {
        long energy = 0L;
        for (int slot = 0; slot < ZPM_SLOTS; slot++) {
            IEnergyStorage zpm = zpmStorage(slot);
            if (zpm != null) {
                energy += zpm.getEnergyStored();
                if (energy >= Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return energy;
    }

    public long zpmMaxEnergyStored() {
        return Integer.MAX_VALUE;
    }

    public int extractZpmEnergy(int amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }

        int remaining = amount;
        int extracted = 0;
        for (int slot = 0; slot < ZPM_SLOTS && remaining > 0; slot++) {
            IEnergyStorage zpm = zpmStorage(slot);
            if (zpm == null || !zpm.canExtract()) {
                continue;
            }

            while (remaining > 0) {
                int step = zpm.extractEnergy(remaining, simulate);
                if (step <= 0) {
                    break;
                }
                extracted += step;
                remaining -= step;
            }
        }

        if (extracted > 0 && !simulate) {
            sync();
        }
        return extracted;
    }

    public boolean insertZpm(ItemStack stack) {
        if (!isZpm(stack)) {
            return false;
        }

        for (int slot = 0; slot < ZPM_SLOTS; slot++) {
            if (slot < zpmItems.getSlots() && zpmItems.getStackInSlot(slot).isEmpty()) {
                ItemStack inserted = stack.copyWithCount(1);
                ZpmItem.ensureEnergyComponent(inserted);
                zpmItems.setStackInSlot(slot, inserted);
                return true;
            }
        }
        return false;
    }

    public ItemStack removeZpm() {
        for (int slot = ZPM_SLOTS - 1; slot >= 0; slot--) {
            if (slot >= zpmItems.getSlots()) {
                continue;
            }
            ItemStack stack = zpmItems.extractItem(slot, zpmItems.getStackInSlot(slot).getCount(), false);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack zpmStack(int slot) {
        return slot >= 0 && slot < zpmItems.getSlots() ? zpmItems.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    private IEnergyStorage zpmStorage(int slot) {
        if (slot < 0 || slot >= zpmItems.getSlots()) {
            return null;
        }

        ItemStack stack = zpmItems.getStackInSlot(slot);
        if (!isZpm(stack)) {
            return null;
        }

        ZpmItem.ensureEnergyComponent(stack);
        return stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }

    private boolean isZpm(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.ZPM.get());
    }

    private static int clampToInt(long value) {
        return (int)Mth.clamp(value, 0L, Integer.MAX_VALUE);
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("zpmItems", zpmItems.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("zpmItems")) {
            zpmItems.deserializeNBT(registries, tag.getCompound("zpmItems"));
            repairZpmItemHandler();
        }
    }

    private void repairZpmItemHandler() {
        if (zpmItems.getSlots() == ZPM_SLOTS) {
            return;
        }

        ItemStackHandler repaired = createZpmItemHandler();
        int slotsToCopy = Math.min(zpmItems.getSlots(), ZPM_SLOTS);
        for (int slot = 0; slot < slotsToCopy; slot++) {
            repaired.setStackInSlot(slot, zpmItems.getStackInSlot(slot).copy());
        }
        zpmItems = repaired;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("zpmItems", zpmItems.serializeNBT(registries));
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
