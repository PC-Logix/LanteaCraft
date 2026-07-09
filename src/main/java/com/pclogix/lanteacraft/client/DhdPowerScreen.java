package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.menu.DhdPowerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DhdPowerScreen extends AbstractContainerScreen<DhdPowerMenu> {
    private static final ResourceLocation INVENTORY = component("inventory_128.png");
    private static final ResourceLocation CRYSTAL_SLOT = component("slot_128.png");
    private static final int INVENTORY_TEXTURE_SIZE = 512;
    private static final int INVENTORY_SOURCE_WIDTH = 356;
    private static final int INVENTORY_SOURCE_HEIGHT = 180;
    private static final int SLOT_TEXTURE_SIZE = 60;
    private static final int TEXT = 0xFFE6E2D0;
    private static final int PANEL_DARK = 0xFF17171C;
    private static final int BORDER_DARK = 0xFF0A0A0D;
    private static final int ENERGY_FILL = 0xFF21D17E;

    public DhdPowerScreen(DhdPowerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 177;
        imageHeight = 148;
        titleLabelX = 8;
        titleLabelY = 30;
        inventoryLabelX = 8;
        inventoryLabelY = 37;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(INVENTORY, leftPos, topPos + 40, imageWidth, 90, 0.0F, 0.0F,
                INVENTORY_SOURCE_WIDTH, INVENTORY_SOURCE_HEIGHT, INVENTORY_TEXTURE_SIZE, INVENTORY_TEXTURE_SIZE);
        guiGraphics.blit(CRYSTAL_SLOT, leftPos + 77, topPos, 24, 24, 0.0F, 0.0F,
                SLOT_TEXTURE_SIZE, SLOT_TEXTURE_SIZE, SLOT_TEXTURE_SIZE, SLOT_TEXTURE_SIZE);

        int barX = leftPos + 42;
        int barY = topPos + 28;
        guiGraphics.fill(barX - 3, barY - 2, barX + 97, barY + 10, BORDER_DARK);
        guiGraphics.fill(barX, barY, barX + 94, barY + 8, PANEL_DARK);

        int fillWidth = Math.min(94, (int)Math.round(94.0D * menu.crystalEnergy() / menu.crystalMaxEnergy()));
        if (fillWidth > 0) {
            guiGraphics.fill(barX, barY, barX + fillWidth, barY + 8, ENERGY_FILL);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        double percent = 100.0D * menu.crystalEnergy() / menu.crystalMaxEnergy();
        String runtime = formatRuntime(menu.runtimeSeconds());
        String status = String.format("%.2f%%  %s", percent, runtime);
        int color = menu.crystalEnergy() > 0 ? 0xFFFFFFFF : 0xFF9F0101;
        guiGraphics.drawString(font, status, 49 + Math.max(0, (80 - font.width(status)) / 2), 28, color, true);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }

    private static ResourceLocation component(String name) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/components/" + name);
    }

    private String formatRuntime(long seconds) {
        long minutes = seconds / 60L;
        long remainingSeconds = seconds % 60L;
        if (minutes > 0) {
            return minutes + "m " + remainingSeconds + "s.";
        }
        return remainingSeconds + "s.";
    }
}
