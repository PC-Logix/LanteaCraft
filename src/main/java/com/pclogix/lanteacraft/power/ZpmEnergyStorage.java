package com.pclogix.lanteacraft.power;

import com.pclogix.lanteacraft.item.ZpmItem;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ZpmEnergyStorage implements IEnergyStorage {
    private final ItemStack stack;

    public ZpmEnergyStorage(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        if (toExtract <= 0) {
            return 0;
        }

        int extracted = Math.min(getEnergyStored(), Math.min(ZpmItem.maxTransfer(), toExtract));
        if (extracted > 0 && !simulate) {
            stack.set(ModDataComponents.ENERGY, getEnergyStored() - extracted);
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return Mth.clamp(stack.getOrDefault(ModDataComponents.ENERGY, 0), 0, ZpmItem.capacity());
    }

    @Override
    public int getMaxEnergyStored() {
        return ZpmItem.capacity();
    }

    @Override
    public boolean canExtract() {
        return getEnergyStored() > 0;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
