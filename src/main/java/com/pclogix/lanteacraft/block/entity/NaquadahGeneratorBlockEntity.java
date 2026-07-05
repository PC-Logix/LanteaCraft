package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.NaquadahGeneratorBlock;
import com.pclogix.lanteacraft.item.EnergyCrystalItem;
import com.pclogix.lanteacraft.power.ConfigurableEnergyStorage;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModBlocks;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;

public class NaquadahGeneratorBlockEntity extends BlockEntity {
    public static final int POWER_CRYSTAL_SLOT = 0;
    public static final int FUEL_SLOT_START = 1;
    public static final int FUEL_SLOT_END = 5;

    private final ConfigurableEnergyStorage energy = new ConfigurableEnergyStorage(
            () -> Config.NAQUADAH_GENERATOR_CAPACITY.get().intValue(),
            () -> 0,
            () -> Config.NAQUADAH_GENERATOR_ENABLED.getAsBoolean() ? Config.NAQUADAH_GENERATOR_MAX_OUTPUT.get() : 0,
            this::sync);
    private final ItemStackHandler fuelItems = new ItemStackHandler(FUEL_SLOT_END) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == POWER_CRYSTAL_SLOT) {
                return isPowerCrystal(stack);
            }
            return isFuel(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            sync();
        }
    };
    private long fuelEnergyRemaining;
    private int burnTicksRemaining;

    public NaquadahGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.NAQUADAH_GENERATOR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, NaquadahGeneratorBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        blockEntity.serverTick();
    }

    public ConfigurableEnergyStorage energyStorage() {
        return energy;
    }

    public ItemStackHandler fuelItems() {
        normalizeInventorySlots();
        return fuelItems;
    }

    public boolean isFuel(ItemStack stack) {
        return fuelValue(stack) > 0L;
    }

    public boolean isPowerCrystal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.ENERGY_CRYSTAL.get();
    }

    public boolean insertFuel(ItemStack stack) {
        normalizeInventorySlots();
        if (isPowerCrystal(stack)) {
            if (!fuelItems.getStackInSlot(POWER_CRYSTAL_SLOT).isEmpty()) {
                return false;
            }

            fuelItems.setStackInSlot(POWER_CRYSTAL_SLOT, stack.copyWithCount(1));
            return true;
        }

        if (!isFuel(stack)) {
            return false;
        }

        for (int slot = FUEL_SLOT_START; slot < FUEL_SLOT_END; slot++) {
            if (fuelItems.getStackInSlot(slot).isEmpty()) {
                fuelItems.setStackInSlot(slot, stack.copyWithCount(1));
                return true;
            }
        }
        return false;
    }

    public ItemStack removeItem(int slot) {
        normalizeInventorySlots();
        return fuelItems.extractItem(slot, fuelItems.getStackInSlot(slot).getCount(), false);
    }

    public boolean isBurning() {
        return energy.getEnergyStored() > 0;
    }

    private void serverTick() {
        normalizeInventorySlots();
        if (!Config.NAQUADAH_GENERATOR_ENABLED.getAsBoolean()) {
            setActive(false);
            return;
        }

        boolean changed = false;

        changed |= consumeFuelIntoBuffer();
        changed |= chargeEnergyCrystal();
        outputEnergy();
        setActive(energy.getEnergyStored() > 0);
        if (changed) {
            sync();
        }
    }

    private boolean consumeFuelIntoBuffer() {
        for (int slot = FUEL_SLOT_START; slot < FUEL_SLOT_END; slot++) {
            ItemStack stack = fuelItems.getStackInSlot(slot);
            long fuelValue = fuelValue(stack);
            if (fuelValue <= 0L) {
                continue;
            }

            if (energy.getMaxEnergyStored() - energy.getEnergyStored() < fuelValue) {
                continue;
            }

            fuelItems.extractItem(slot, 1, false);
            energy.addInternal((int)Math.min(Integer.MAX_VALUE, fuelValue));
            fuelEnergyRemaining = 0L;
            burnTicksRemaining = 0;
            return true;
        }
        return false;
    }

    private boolean chargeEnergyCrystal() {
        if (energy.getEnergyStored() <= 0) {
            return false;
        }

        ItemStack stack = fuelItems.getStackInSlot(POWER_CRYSTAL_SLOT);
        if (!isPowerCrystal(stack)) {
            return false;
        }

        IEnergyStorage crystal = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (crystal == null || !crystal.canReceive()) {
            return false;
        }

        int offered = Math.min(Config.NAQUADAH_GENERATOR_FE_PER_TICK.get(), energy.getEnergyStored());
        int accepted = crystal.receiveEnergy(offered, false);
        if (accepted <= 0) {
            return false;
        }

        energy.extractEnergy(accepted, false);
        return true;
    }

    private void outputEnergy() {
        if (level == null || energy.getEnergyStored() <= 0 || Config.NAQUADAH_GENERATOR_MAX_OUTPUT.get() <= 0) {
            return;
        }

        int remaining = Math.min(Config.NAQUADAH_GENERATOR_MAX_OUTPUT.get(), energy.getEnergyStored());
        for (Direction direction : Direction.values()) {
            if (remaining <= 0) {
                break;
            }

            BlockPos targetPos = worldPosition.relative(direction);
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, direction.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }

            int accepted = target.receiveEnergy(remaining, false);
            if (accepted > 0) {
                energy.extractEnergy(accepted, false);
                remaining -= accepted;
            }
        }
    }

    private void setActive(boolean active) {
        if (level == null || !getBlockState().hasProperty(NaquadahGeneratorBlock.ACTIVE) || getBlockState().getValue(NaquadahGeneratorBlock.ACTIVE) == active) {
            return;
        }

        level.setBlock(worldPosition, getBlockState().setValue(NaquadahGeneratorBlock.ACTIVE, active), Block.UPDATE_ALL);
    }

    private long fuelValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0L;
        }

        Item item = stack.getItem();
        if (item == ModItems.NAQUADAH.get()) {
            return Config.NAQUADAH_GENERATOR_SHARD_FE.get();
        }
        if (item == ModItems.NAQUADAH_INGOT.get()) {
            return Config.NAQUADAH_GENERATOR_INGOT_FE.get();
        }
        if (item == ModBlocks.NAQUADAH_BLOCK.get().asItem()) {
            return Config.NAQUADAH_GENERATOR_BLOCK_FE.get();
        }
        return 0L;
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
        tag.put("energy", energy.serializeNBT(registries));
        tag.put("fuelItems", fuelItems.serializeNBT(registries));
        tag.putLong("fuelEnergyRemaining", fuelEnergyRemaining);
        tag.putInt("burnTicksRemaining", burnTicksRemaining);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("energy")) {
            energy.deserializeNBT(registries, tag.get("energy"));
        }
        if (tag.contains("fuelItems")) {
            fuelItems.deserializeNBT(registries, tag.getCompound("fuelItems"));
            normalizeInventorySlots();
        }
        fuelEnergyRemaining = tag.getLong("fuelEnergyRemaining");
        burnTicksRemaining = tag.getInt("burnTicksRemaining");
    }

    private void normalizeInventorySlots() {
        if (fuelItems.getSlots() == FUEL_SLOT_END) {
            return;
        }

        ItemStack oldStack = fuelItems.getSlots() > 0 ? fuelItems.getStackInSlot(0).copy() : ItemStack.EMPTY;
        fuelItems.setSize(FUEL_SLOT_END);
        if (oldStack.isEmpty()) {
            return;
        }

        int slot = isPowerCrystal(oldStack) ? POWER_CRYSTAL_SLOT : FUEL_SLOT_START;
        fuelItems.setStackInSlot(slot, oldStack);
    }
}
