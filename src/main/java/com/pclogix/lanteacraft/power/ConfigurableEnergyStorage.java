package com.pclogix.lanteacraft.power;

import java.util.function.IntSupplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ConfigurableEnergyStorage implements IEnergyStorage, INBTSerializable<Tag> {
    private final IntSupplier capacity;
    private final IntSupplier maxReceive;
    private final IntSupplier maxExtract;
    private final Runnable changed;
    private int energy;

    public ConfigurableEnergyStorage(IntSupplier capacity, IntSupplier maxReceive, IntSupplier maxExtract) {
        this(capacity, maxReceive, maxExtract, () -> {});
    }

    public ConfigurableEnergyStorage(IntSupplier capacity, IntSupplier maxReceive, IntSupplier maxExtract, Runnable changed) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.changed = changed;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive() || toReceive <= 0) {
            return 0;
        }

        int received = Mth.clamp(capacity() - energy, 0, Math.min(maxReceive(), toReceive));
        if (!simulate) {
            energy += received;
            if (received > 0) {
                changed.run();
            }
        }
        return received;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (!canExtract() || toExtract <= 0) {
            return 0;
        }

        int extracted = Math.min(energy, Math.min(maxExtract(), toExtract));
        if (!simulate) {
            energy -= extracted;
            if (extracted > 0) {
                changed.run();
            }
        }
        return extracted;
    }

    public int addInternal(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int accepted = Math.min(capacity() - energy, amount);
        energy += accepted;
        if (accepted > 0) {
            changed.run();
        }
        return accepted;
    }

    public boolean consume(long amount, boolean simulate) {
        if (amount <= 0) {
            return true;
        }
        if (amount > energy) {
            return false;
        }
        if (!simulate) {
            energy -= (int)amount;
            changed.run();
        }
        return true;
    }

    public void setEnergyStored(int amount) {
        energy = Mth.clamp(amount, 0, capacity());
        changed.run();
    }

    @Override
    public int getEnergyStored() {
        return Math.min(energy, capacity());
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity();
    }

    @Override
    public boolean canExtract() {
        return maxExtract() > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive() > 0;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        return IntTag.valueOf(getEnergyStored());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {
        if (tag instanceof IntTag intTag) {
            setEnergyStored(intTag.getAsInt());
        }
    }

    private int capacity() {
        return Math.max(0, capacity.getAsInt());
    }

    private int maxReceive() {
        return Math.max(0, maxReceive.getAsInt());
    }

    private int maxExtract() {
        return Math.max(0, maxExtract.getAsInt());
    }
}
