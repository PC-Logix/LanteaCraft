package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class OfferingAltarBlockEntity extends BlockEntity {
    private final ItemStackHandler displayedItem = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            sync();
        }
    };

    public OfferingAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OFFERING_ALTAR.get(), pos, state);
    }

    public ItemStack displayedItem() {
        return displayedItem.getStackInSlot(0);
    }

    public void setDisplayedItem(ItemStack stack) {
        displayedItem.setStackInSlot(0, stack);
    }

    public ItemStack removeDisplayedItem() {
        return displayedItem.extractItem(0, 1, false);
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
        tag.put("displayedItem", displayedItem.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("displayedItem")) {
            displayedItem.deserializeNBT(registries, tag.getCompound("displayedItem"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("displayedItem", displayedItem.serializeNBT(registries));
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
