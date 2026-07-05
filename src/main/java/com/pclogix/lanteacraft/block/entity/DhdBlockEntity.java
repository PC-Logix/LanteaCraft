package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.item.EnergyCrystalItem;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DhdBlockEntity extends BlockEntity {
    private static final int CRYSTAL_SLOT = 0;

    private final ItemStackHandler crystalItems = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isEnergyCrystal(stack);
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
    private final IEnergyStorage energyInput = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int toReceive, boolean simulate) {
            IEnergyStorage crystal = crystalStorage();
            if (crystal == null || !crystal.canReceive() || toReceive <= 0) {
                return 0;
            }

            int accepted = crystal.receiveEnergy(toReceive, simulate);
            if (accepted > 0 && !simulate) {
                sync();
            }
            return accepted;
        }

        @Override
        public int extractEnergy(int toExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return crystalEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return crystalMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            IEnergyStorage crystal = crystalStorage();
            return crystal != null && crystal.canReceive();
        }
    };

    public DhdBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DHD.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DhdBlockEntity blockEntity) {
    }

    public ItemStackHandler crystalItems() {
        return crystalItems;
    }

    public IEnergyStorage energyInput() {
        return energyInput;
    }

    public void installChargedCrystal() {
        if (crystalItems.getStackInSlot(CRYSTAL_SLOT).isEmpty()) {
            crystalItems.setStackInSlot(CRYSTAL_SLOT, chargedCrystal(EnergyCrystalItem.CAPACITY));
        }
    }

    public static ItemStack chargedCrystal(int energy) {
        ItemStack stack = new ItemStack(ModItems.ENERGY_CRYSTAL.get());
        stack.set(ModDataComponents.ENERGY, Mth.clamp(energy, 0, EnergyCrystalItem.CAPACITY));
        return stack;
    }

    public int crystalEnergyStored() {
        IEnergyStorage crystal = crystalStorage();
        return crystal == null ? 0 : crystal.getEnergyStored();
    }

    public int crystalMaxEnergyStored() {
        IEnergyStorage crystal = crystalStorage();
        return crystal == null ? EnergyCrystalItem.CAPACITY : crystal.getMaxEnergyStored();
    }

    public long estimatedRuntimeSeconds() {
        long costPerTick = Math.max(1L, Config.ACTIVE_COST_PER_TICK.get());
        return crystalEnergyStored() / costPerTick / 20L;
    }

    public int extractCrystalEnergy(int amount, boolean simulate) {
        IEnergyStorage crystal = crystalStorage();
        if (crystal == null || !crystal.canExtract() || amount <= 0) {
            return 0;
        }

        int extracted = crystal.extractEnergy(amount, simulate);
        if (extracted > 0 && !simulate) {
            sync();
        }
        return extracted;
    }

    public boolean insertCrystal(ItemStack stack) {
        if (!isEnergyCrystal(stack) || !crystalItems.getStackInSlot(CRYSTAL_SLOT).isEmpty()) {
            return false;
        }

        crystalItems.setStackInSlot(CRYSTAL_SLOT, stack.copyWithCount(1));
        return true;
    }

    public ItemStack removeCrystal() {
        return crystalItems.extractItem(CRYSTAL_SLOT, crystalItems.getStackInSlot(CRYSTAL_SLOT).getCount(), false);
    }

    private IEnergyStorage crystalStorage() {
        ItemStack stack = crystalItems.getStackInSlot(CRYSTAL_SLOT);
        if (!isEnergyCrystal(stack)) {
            return null;
        }

        return stack.getCapability(Capabilities.EnergyStorage.ITEM);
    }

    private boolean isEnergyCrystal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.ENERGY_CRYSTAL.get();
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
        tag.put("crystalItems", crystalItems.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("crystalItems")) {
            crystalItems.deserializeNBT(registries, tag.getCompound("crystalItems"));
        }
    }
}
