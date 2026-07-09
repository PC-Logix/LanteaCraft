package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.menu.ZpmHubMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ZpmHubScreen extends AbstractContainerScreen<ZpmHubMenu> {
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 108;
    private static final int HOTBAR_Y = 166;
    private static final int PANEL = 0xFF13252B;
    private static final int PANEL_EDGE = 0xFF3FA9A7;
    private static final int SLOT_BG = 0xFF071417;
    private static final int SLOT_EDGE = 0xFF7FD6D1;
    private static final int TEXT = 0xFFE6F7F4;
    private static final int ENERGY_BG = 0xFF061012;
    private static final int ENERGY_FILL = 0xFF30E3D0;

    public ZpmHubScreen(ZpmHubMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 196;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 98;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, PANEL);
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, PANEL_EDGE);
        guiGraphics.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, PANEL_EDGE);
        guiGraphics.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, PANEL_EDGE);
        guiGraphics.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, PANEL_EDGE);

        // slot 0 = player slot 1 = top left
        drawSlot(guiGraphics, 62, 18);
        // slot 1 = player slot 2 = top right
        drawSlot(guiGraphics, 98, 18);
        // slot 2 = player slot 3 = bottom middle
        drawSlot(guiGraphics, 80, 50);

        guiGraphics.fill(leftPos + 46, topPos + 84, leftPos + 130, topPos + 91, ENERGY_BG);
        long maxEnergy = menu.maxEnergyStored();
        int energyWidth = maxEnergy > 0
                ? Math.min(82, (int)Math.round(82.0D * menu.energyStored() / maxEnergy))
                : 0;
        guiGraphics.fill(leftPos + 47, topPos + 85, leftPos + 47 + energyWidth, topPos + 90, ENERGY_FILL);

        drawPlayerInventoryGrid(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, TEXT, false);
        Component energy = Component.translatable("screen.lanteacraft.zpm_hub.energy", menu.energyStored(), menu.maxEnergyStored());
        guiGraphics.drawString(font, energy, (imageWidth - font.width(energy)) / 2, 74, TEXT, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }

    private void drawPlayerInventoryGrid(GuiGraphics guiGraphics) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(guiGraphics, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18);
            }
        }

        for (int col = 0; col < 9; col++) {
            drawSlot(guiGraphics, PLAYER_INV_X + col * 18, HOTBAR_Y);
        }
    }

    private void drawSlot(GuiGraphics guiGraphics, int x, int y) {
        int left = leftPos + x - 1;
        int top = topPos + y - 1;
        guiGraphics.fill(left, top, left + 18, top + 18, SLOT_EDGE);
        guiGraphics.fill(left + 1, top + 1, left + 17, top + 17, SLOT_BG);
    }
}
