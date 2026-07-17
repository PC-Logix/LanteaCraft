package com.pclogix.lanteacraft.compat.bluemap;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;
import org.jetbrains.annotations.Nullable;

public final class BlueMapStargateBlockEntity extends MCABlockEntity {
    @NBTName("bottomCamouflage")
    private @Nullable String bottomCamouflage;

    public BlueMapStargateBlockEntity() {
    }

    public @Nullable String getBottomCamouflage() {
        return bottomCamouflage;
    }
}
