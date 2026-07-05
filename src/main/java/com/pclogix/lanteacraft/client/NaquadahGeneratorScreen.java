package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.menu.NaquadahGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NaquadahGeneratorScreen extends AbstractContainerScreen<NaquadahGeneratorMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/prefabs/naquadah_generator_128.png");
    private static final int TEXTURE_SIZE = 512;
    private static final int BACKGROUND_SOURCE_WIDTH = 356;
    private static final int BACKGROUND_SOURCE_HEIGHT = 416;
    private static final int TEXT = 0xFFE6E2D0;
    private static final int ENERGY_FILL = 0xFF21D17E;

    public NaquadahGeneratorScreen(NaquadahGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 178;
        imageHeight = 208;
        titleLabelX = 9;
        titleLabelY = 7;
        inventoryLabelX = 8;
        inventoryLabelY = 111;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, imageWidth, imageHeight, 0.0F, 0.0F, BACKGROUND_SOURCE_WIDTH, BACKGROUND_SOURCE_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        int maxEnergy = Math.max(1, menu.maxEnergyStored());
        int energyWidth = Math.min(82, (int)Math.round(82.0D * menu.energyStored() / maxEnergy));
        guiGraphics.fill(leftPos + 48, topPos + 95, leftPos + 48 + energyWidth, topPos + 103, ENERGY_FILL);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, TEXT, false);
        guiGraphics.drawString(font, Component.translatable("screen.lanteacraft.naquadah_generator.energy", menu.energyStored(), menu.maxEnergyStored()), 48, 83, TEXT, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }
}
