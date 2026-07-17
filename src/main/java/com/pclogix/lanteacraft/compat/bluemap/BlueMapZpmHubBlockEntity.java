package com.pclogix.lanteacraft.compat.bluemap;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

import java.util.List;

public final class BlueMapZpmHubBlockEntity extends MCABlockEntity {
    @NBTName("zpmItems")
    private ZpmItems zpmItems = new ZpmItems();

    public BlueMapZpmHubBlockEntity() {
    }

    public boolean hasZpm(int slot) {
        return zpmItems != null && zpmItems.items.stream().anyMatch(item -> item.slot == slot);
    }

    public static final class ZpmItems {
        @NBTName("Items")
        private List<ItemEntry> items = List.of();

        public ZpmItems() {
        }
    }

    public static final class ItemEntry {
        @NBTName("Slot")
        private int slot = -1;

        public ItemEntry() {
        }
    }
}
