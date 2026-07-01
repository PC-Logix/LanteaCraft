package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModSounds;
import com.pclogix.lanteacraft.gate.IrisState;
import com.pclogix.lanteacraft.gate.IrisType;
import com.pclogix.lanteacraft.gate.StargateEventDispatcher;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.item.IrisUpgradeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class StargateBaseBlockEntity extends BlockEntity {
    private static final int DEFAULT_DIAL_DURATION_TICKS = 65 * 7;
    private static final int SYMBOL_TICKS = 65;
    private static final int SPIN_TICKS = 55;
    private static final int IRIS_MOVE_TICKS = 40;

    private String dialingAddress = "";
    private long dialingStartGameTime = -1L;
    private int dialingDurationTicks = DEFAULT_DIAL_DURATION_TICKS;
    private String connectedAddress = "";
    private BlockState bottomCamouflage;
    private int lastChevronSound = -1;
    private boolean openSoundPlayed;
    private long nextAmbientSoundTime;
    private IrisType irisType;
    private IrisState irisState = IrisState.NONE;
    private int irisMoveTicks;
    private boolean irisRedstoneEnabled = true;
    private boolean irisRedstoneActive;
    private String gdoCode = "";
    private final ItemStackHandler irisItems = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof IrisUpgradeItem;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            refreshIrisFromSlot();
        }
    };

    public StargateBaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.STARGATE_BASE.get(), pos, blockState);
    }

    public void startDialing(String address) {
        if (level == null) {
            return;
        }

        dialingAddress = address == null ? "" : address.trim().toUpperCase();
        dialingStartGameTime = level.getGameTime();
        dialingDurationTicks = Math.max(1, dialingAddress.length()) * 65;
        lastChevronSound = -1;
        openSoundPlayed = false;
        nextAmbientSoundTime = dialingStartGameTime + dialingDurationTicks + 40L;
        playRollSound();
        sync();
    }

    public void setConnectedAddress(String address) {
        connectedAddress = address == null ? "" : address.trim().toUpperCase();
        sync();
    }

    public void clearConnection() {
        boolean wasConnected = isConnected();
        connectedAddress = "";
        dialingAddress = "";
        dialingStartGameTime = -1L;
        lastChevronSound = -1;
        openSoundPlayed = false;
        nextAmbientSoundTime = 0L;
        if (wasConnected && level != null) {
            level.playSound(null, worldPosition, ModSounds.STARGATE_CLOSE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        sync();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, StargateBaseBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        blockEntity.serverTick();
    }

    private void serverTick() {
        if (level == null) {
            return;
        }

        long gameTime = level.getGameTime();
        if (isDialing(gameTime)) {
            long elapsed = gameTime - dialingStartGameTime;
            if (elapsed >= SPIN_TICKS) {
                int currentChevron = Math.min(dialingAddress.length() - 1, (int)((elapsed - SPIN_TICKS) / SYMBOL_TICKS));
                while (currentChevron > lastChevronSound && lastChevronSound + 1 < dialingAddress.length()) {
                    lastChevronSound++;
                    level.playSound(null, worldPosition, ModSounds.STARGATE_CHEVRON_LOCK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }

        if (isConnected() && dialingStartGameTime >= 0L && gameTime > dialingStartGameTime + dialingDurationTicks && !openSoundPlayed) {
            openSoundPlayed = true;
            level.playSound(null, worldPosition, ModSounds.STARGATE_OPEN.get(), SoundSource.BLOCKS, 1.25F, 1.0F);
            StargateMultiblock.findEntryFrom(level, worldPosition).ifPresent(gate -> StargateEventDispatcher.wormholeOpened((net.minecraft.server.level.ServerLevel)level, gate));
        }

        if (isConnected() && !isDialing(gameTime) && nextAmbientSoundTime == 0L) {
            nextAmbientSoundTime = gameTime + 40L;
        }

        if (isConnected() && !isDialing(gameTime) && nextAmbientSoundTime > 0L && gameTime >= nextAmbientSoundTime) {
            nextAmbientSoundTime = gameTime + 85L;
            level.playSound(null, worldPosition, ModSounds.STARGATE_AMBIENT.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
        }

        tickIris();
    }

    private void playRollSound() {
        if (level != null) {
            level.playSound(null, worldPosition, ModSounds.STARGATE_ROLL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private void sync() {
        setChanged();
        if (level == null) {
            return;
        }

        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public String dialingAddress() {
        return dialingAddress;
    }

    public long dialingStartGameTime() {
        return dialingStartGameTime;
    }

    public int dialingDurationTicks() {
        return dialingDurationTicks;
    }

    public boolean isDialing(long gameTime) {
        return dialingStartGameTime >= 0 && gameTime - dialingStartGameTime <= dialingDurationTicks;
    }

    public String connectedAddress() {
        return connectedAddress;
    }

    public boolean isConnected() {
        return !connectedAddress.isBlank();
    }

    public BlockState bottomCamouflage() {
        return bottomCamouflage;
    }

    public boolean hasBottomCamouflage() {
        return bottomCamouflage != null;
    }

    public void setBottomCamouflage(BlockState camouflage) {
        bottomCamouflage = camouflage == null || camouflage.isAir() ? null : camouflage.getBlock().defaultBlockState();
        sync();
    }

    public void clearBottomCamouflage() {
        bottomCamouflage = null;
        sync();
    }

    public boolean hasIris() {
        return irisType != null;
    }

    public ItemStackHandler irisItems() {
        return irisItems;
    }

    public IrisType irisType() {
        return irisType;
    }

    public IrisState irisState() {
        return hasIris() ? irisState : IrisState.NONE;
    }

    public void installIris(ItemStack stack) {
        if (!(stack.getItem() instanceof IrisUpgradeItem)) {
            return;
        }

        irisItems.setStackInSlot(0, stack.copyWithCount(1));
    }

    public void removeIris() {
        irisItems.setStackInSlot(0, ItemStack.EMPTY);
    }

    public boolean toggleIris() {
        if (isIrisClosedOrClosing()) {
            return openIris();
        } else {
            return closeIris();
        }
    }

    public boolean openIris() {
        if (!hasIris() || irisState == IrisState.OPEN || irisState == IrisState.OPENING) {
            return false;
        }

        if (isIrisRedstoneLocked()) {
            return false;
        }

        irisState = IrisState.OPENING;
        irisMoveTicks = IRIS_MOVE_TICKS;
        playIrisSound(true);
        sync();
        return true;
    }

    public boolean closeIris() {
        if (!hasIris() || irisState == IrisState.CLOSED || irisState == IrisState.CLOSING) {
            return false;
        }

        irisState = IrisState.CLOSING;
        irisMoveTicks = IRIS_MOVE_TICKS;
        playIrisSound(false);
        sync();
        return true;
    }

    public String gdoCode() {
        return gdoCode;
    }

    public void setGdoCode(String code) {
        gdoCode = code == null ? "" : code.trim().toUpperCase();
        sync();
    }

    public boolean authorizeGdo(String code) {
        return hasIris() && !gdoCode.isBlank() && gdoCode.equals(code == null ? "" : code.trim().toUpperCase());
    }

    public boolean isIrisClosedOrClosing() {
        return irisState == IrisState.CLOSED || irisState == IrisState.CLOSING;
    }

    public boolean isIrisObstructing() {
        return hasIris() && irisState != IrisState.OPEN && irisState != IrisState.NONE;
    }

    public boolean isIrisRedstoneEnabled() {
        return irisRedstoneEnabled;
    }

    public boolean isIrisRedstoneLocked() {
        return hasIris() && irisRedstoneEnabled && level != null && level.hasNeighborSignal(worldPosition);
    }

    public void toggleIrisRedstone() {
        setIrisRedstoneEnabled(!irisRedstoneEnabled);
    }

    public void setIrisRedstoneEnabled(boolean enabled) {
        boolean wasRedstoneLocked = irisRedstoneEnabled && irisRedstoneActive;
        irisRedstoneEnabled = enabled;
        if (!irisRedstoneEnabled) {
            irisRedstoneActive = false;
            if (wasRedstoneLocked) {
                openIris();
            }
        }
        sync();
    }

    public double irisProgress(float partialTick) {
        if (!hasIris()) {
            return 0.0D;
        }

        return switch (irisState) {
            case CLOSED -> 1.0D;
            case CLOSING -> clamp((IRIS_MOVE_TICKS - Math.max(0.0D, irisMoveTicks - partialTick)) / IRIS_MOVE_TICKS);
            case OPENING -> clamp(Math.max(0.0D, irisMoveTicks - partialTick) / IRIS_MOVE_TICKS);
            default -> 0.0D;
        };
    }

    private void tickIris() {
        if (!hasIris()) {
            irisState = IrisState.NONE;
            irisMoveTicks = 0;
            irisRedstoneActive = false;
            return;
        }

        if (!irisRedstoneEnabled) {
            irisRedstoneActive = false;
        }

        boolean powered = irisRedstoneEnabled && level != null && level.hasNeighborSignal(worldPosition);
        if (powered && irisState != IrisState.CLOSED && irisState != IrisState.CLOSING) {
            closeIris();
        }

        if (powered != irisRedstoneActive) {
            irisRedstoneActive = powered;
            if (powered) {
                closeIris();
            } else {
                openIris();
            }
        }

        if (irisState != IrisState.OPENING && irisState != IrisState.CLOSING) {
            return;
        }

        irisMoveTicks--;
        if (irisMoveTicks <= 0) {
            irisMoveTicks = 0;
            irisState = irisState == IrisState.OPENING ? IrisState.OPEN : IrisState.CLOSED;
        }
        sync();
    }

    private void playIrisSound(boolean opening) {
        if (level == null) {
            return;
        }

        level.playSound(null, worldPosition, irisSound(opening), SoundSource.BLOCKS, 0.85F, 1.0F);
    }

    private SoundEvent irisSound(boolean opening) {
        if (irisType == IrisType.ENERGY) {
            return opening ? ModSounds.ENERGY_IRIS_OFF.get() : ModSounds.ENERGY_IRIS_ON.get();
        }

        return opening ? ModSounds.MECHANICAL_IRIS_OPEN.get() : ModSounds.MECHANICAL_IRIS_CLOSE.get();
    }

    private void refreshIrisFromSlot() {
        ItemStack stack = irisItems.getStackInSlot(0);
        IrisType previousType = irisType;
        irisType = stack.getItem() instanceof IrisUpgradeItem irisItem ? irisItem.irisType() : null;
        if (irisType == null) {
            irisState = IrisState.NONE;
            irisMoveTicks = 0;
            irisRedstoneActive = false;
        } else if (previousType == null || irisState == IrisState.NONE) {
            irisState = IrisState.OPEN;
            irisMoveTicks = 0;
        }

        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveDialing(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadDialing(tag, registries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveDialing(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void saveDialing(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("dialingAddress", dialingAddress);
        tag.putLong("dialingStartGameTime", dialingStartGameTime);
        tag.putInt("dialingDurationTicks", dialingDurationTicks);
        tag.putString("connectedAddress", connectedAddress);
        tag.putBoolean("openSoundPlayed", openSoundPlayed);
        if (irisType != null) {
            tag.putString("irisType", irisType.serializedName());
        }
        tag.putString("irisState", irisState.serializedName());
        tag.putInt("irisMoveTicks", irisMoveTicks);
        tag.putBoolean("irisRedstoneEnabled", irisRedstoneEnabled);
        tag.putBoolean("irisRedstoneActive", irisRedstoneActive);
        tag.putString("gdoCode", gdoCode);
        tag.put("irisItems", irisItems.serializeNBT(registries));
        if (bottomCamouflage != null) {
            tag.putString("bottomCamouflage", BuiltInRegistries.BLOCK.getKey(bottomCamouflage.getBlock()).toString());
        }
    }

    private void loadDialing(CompoundTag tag, HolderLookup.Provider registries) {
        dialingAddress = tag.getString("dialingAddress");
        dialingStartGameTime = tag.getLong("dialingStartGameTime");
        dialingDurationTicks = tag.contains("dialingDurationTicks") ? tag.getInt("dialingDurationTicks") : DEFAULT_DIAL_DURATION_TICKS;
        connectedAddress = tag.getString("connectedAddress");
        openSoundPlayed = tag.contains("openSoundPlayed") ? tag.getBoolean("openSoundPlayed") : !connectedAddress.isBlank();
        lastChevronSound = dialingStartGameTime >= 0L ? dialingAddress.length() - 1 : -1;
        nextAmbientSoundTime = 0L;
        IrisState savedIrisState = tag.contains("irisState") ? IrisState.byName(tag.getString("irisState")) : IrisState.NONE;
        int savedIrisMoveTicks = tag.contains("irisMoveTicks") ? tag.getInt("irisMoveTicks") : 0;
        irisType = tag.contains("irisType") ? IrisType.byName(tag.getString("irisType")) : null;
        irisState = savedIrisState;
        irisMoveTicks = savedIrisMoveTicks;
        irisRedstoneEnabled = !tag.contains("irisRedstoneEnabled") || tag.getBoolean("irisRedstoneEnabled");
        irisRedstoneActive = tag.contains("irisRedstoneActive") && tag.getBoolean("irisRedstoneActive");
        gdoCode = tag.getString("gdoCode");
        if (tag.contains("irisItems")) {
            irisItems.deserializeNBT(registries, tag.getCompound("irisItems"));
        }
        if (irisType == null) {
            irisState = IrisState.NONE;
            irisMoveTicks = 0;
            irisRedstoneActive = false;
        } else if (savedIrisState != IrisState.NONE) {
            // Deserializing the item handler invokes refreshIrisFromSlot(), so
            // restore the authoritative saved state afterward. Otherwise an
            // installed iris can be reset to the slot-refresh default on load.
            irisState = savedIrisState;
            irisMoveTicks = savedIrisMoveTicks;
        }
        bottomCamouflage = null;
        if (tag.contains("bottomCamouflage")) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("bottomCamouflage")));
            if (block != Blocks.AIR) {
                bottomCamouflage = block.defaultBlockState();
            }
        }
    }

    private static double clamp(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }

        return Math.min(value, 1.0D);
    }
}
