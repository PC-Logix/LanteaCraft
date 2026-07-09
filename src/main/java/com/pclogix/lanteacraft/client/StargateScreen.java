package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.IrisState;
import com.pclogix.lanteacraft.gate.StargateAddress;
import com.pclogix.lanteacraft.item.IrisUpgradeItem;
import com.pclogix.lanteacraft.menu.StargateMenu;
import com.pclogix.lanteacraft.network.ToggleIrisPayload;
import com.pclogix.lanteacraft.network.ToggleIrisRedstonePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class StargateScreen extends AbstractContainerScreen<StargateMenu> {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/prefabs/sg_gui.png");
    private static final ResourceLocation COPY_ICON = icon("copy_32.png");
    private static final ResourceLocation IRIS_ICON = icon("icon_iris_32.png");
    private static final ResourceLocation REDSTONE_ON_ICON = icon("icon_rstorchon_32.png");
    private static final ResourceLocation REDSTONE_OFF_ICON = icon("icon_rstorchoff_32.png");
    private static final ResourceLocation TICK_ICON = icon("tick_32.png");
    private static final ResourceLocation CROSS_ICON = icon("cross_32.png");
    private static final int BACKGROUND_TEXTURE_SIZE = 512;
    private static final int BACKGROUND_SOURCE_WIDTH = 512;
    private static final int BACKGROUND_SOURCE_HEIGHT = 416;
    private static final int SYMBOL_FRAME_WIDTH = 236;
    private static final int SYMBOL_FRAME_HEIGHT = 44;
    private static final int SYMBOL_FRAME_SOURCE_WIDTH = 472;
    private static final int SYMBOL_FRAME_SOURCE_HEIGHT = 88;
    private static final int SYMBOL_CELL_SIZE = 32;
    private static final int EXTENDED_SYMBOL_CELL_SIZE = 24;
    private static final int SYMBOL_SOURCE_CELL_SIZE = 64;
    private static final int ICON_BUTTON_SIZE = 18;
    private static final int ICON_SIZE = 14;
    private static final int ICON_TEXTURE_SIZE = 32;
    private static final String GLYPHS = StargateAddress.LEGACY_GLYPHS;
    private boolean redstoneEnabled;
    private Button copyButton;
    private Button irisButton;
    private Button redstoneButton;

    public StargateScreen(StargateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 256;
        imageHeight = 208;
        inventoryLabelX = 48;
        inventoryLabelY = 112;
        titleLabelX = 48;
        titleLabelY = 88;
    }

    @Override
    protected void init() {
        super.init();
        redstoneEnabled = menu.baseEntity() == null || menu.baseEntity().isIrisRedstoneEnabled();
        copyButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            minecraft.keyboardHandler.setClipboard(menu.address());
        }).bounds(leftPos + 230, topPos + 54, ICON_BUTTON_SIZE, ICON_BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("screen.lanteacraft.stargate.copy")))
                .build());

        irisButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            PacketDistributor.sendToServer(new ToggleIrisPayload(menu.basePos()));
        }).bounds(leftPos + 230, topPos + 96, ICON_BUTTON_SIZE, ICON_BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("screen.lanteacraft.stargate.iris_toggle")))
                .build());

        redstoneButton = addRenderableWidget(Button.builder(Component.empty(), button -> {
            redstoneEnabled = !redstoneEnabled;
            PacketDistributor.sendToServer(new ToggleIrisRedstonePayload(menu.basePos()));
        }).bounds(leftPos + 230, topPos + 118, ICON_BUTTON_SIZE, ICON_BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("screen.lanteacraft.stargate.redstone.tooltip")))
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        drawPanel(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderButtonIcons(guiGraphics);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Keep the gate panel crisp; the default screen blur makes this UI look like it is behind glass.
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    private void drawPanel(GuiGraphics guiGraphics) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, imageWidth, imageHeight, 0.0F, 0.0F, BACKGROUND_SOURCE_WIDTH, BACKGROUND_SOURCE_HEIGHT, BACKGROUND_TEXTURE_SIZE, BACKGROUND_TEXTURE_SIZE);
        drawAddressGlyphs(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(font, menu.address(), imageWidth / 2, 56, 0xE8E8E8);
        guiGraphics.drawString(font, Component.translatable("screen.lanteacraft.stargate.iris_slot"), 47, 88, 0xAEB7B8, false);
        guiGraphics.drawString(font, Component.translatable("screen.lanteacraft.stargate.eighth_chevron_slot"), 72, 88, eighthChevronColor(), false);
        guiGraphics.drawString(font, eighthChevronLabel(), 103, 102, eighthChevronColor(), false);
    }

    private Component irisLabel() {
        ItemStack stack = menu.getSlot(0).getItem();
        if (!(stack.getItem() instanceof IrisUpgradeItem irisItem)) {
            return Component.translatable("screen.lanteacraft.stargate.iris.none");
        }

        IrisState state = menu.baseEntity() == null ? IrisState.NONE : menu.baseEntity().irisState();
        return Component.translatable("screen.lanteacraft.stargate.iris.value",
                Component.translatable("item.lanteacraft.iris_upgrade.type." + irisItem.irisType().serializedName()),
                Component.translatable("screen.lanteacraft.stargate.iris.state." + state.serializedName()));
    }

    private Component eighthChevronLabel() {
        return Component.translatable(menu.getSlot(1).hasItem()
                ? "screen.lanteacraft.stargate.eighth_chevron.installed"
                : "screen.lanteacraft.stargate.eighth_chevron.locked");
    }

    private int eighthChevronColor() {
        return menu.getSlot(1).hasItem() ? 0x66E6FF : 0x6F7C83;
    }

    private void renderButtonIcons(GuiGraphics guiGraphics) {
        renderIcon(guiGraphics, copyButton, COPY_ICON);
        renderIcon(guiGraphics, irisButton, IRIS_ICON);
        renderIcon(guiGraphics, redstoneButton, redstoneEnabled ? REDSTONE_ON_ICON : REDSTONE_OFF_ICON);

        ResourceLocation irisStateIcon = menu.baseEntity() != null && menu.baseEntity().isIrisClosedOrClosing() ? CROSS_ICON : TICK_ICON;
        guiGraphics.blit(irisStateIcon, irisButton.getX() + 10, irisButton.getY() + 10, 7, 7, 0.0F, 0.0F, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
    }

    private void renderIcon(GuiGraphics guiGraphics, Button button, ResourceLocation icon) {
        int x = button.getX() + (button.getWidth() - ICON_SIZE) / 2;
        int y = button.getY() + (button.getHeight() - ICON_SIZE) / 2;
        guiGraphics.blit(icon, x, y, ICON_SIZE, ICON_SIZE, 0.0F, 0.0F, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE, ICON_TEXTURE_SIZE);
    }

    private void drawAddressGlyphs(GuiGraphics guiGraphics) {
        int addressLength = menu.address().length();
        int cellSize = addressLength > StargateAddress.ADDRESS_LENGTH ? EXTENDED_SYMBOL_CELL_SIZE : SYMBOL_CELL_SIZE;
        int glyphWidth = addressLength * cellSize;
        int startX = leftPos + (imageWidth - SYMBOL_FRAME_WIDTH) / 2;
        int y = topPos + 8;
        guiGraphics.blit(symbolFrameTexture(), startX, y, SYMBOL_FRAME_WIDTH, SYMBOL_FRAME_HEIGHT, 0.0F, 0.0F, SYMBOL_FRAME_SOURCE_WIDTH, SYMBOL_FRAME_SOURCE_HEIGHT, 512, 128);
        for (int i = 0; i < addressLength; i++) {
            drawGlyph(guiGraphics, menu.address().charAt(i), startX + (SYMBOL_FRAME_WIDTH - glyphWidth) / 2 + i * cellSize, y + 6, cellSize);
        }
    }

    private void drawGlyph(GuiGraphics guiGraphics, char glyph, int x, int y, int size) {
        int index = Math.max(0, GLYPHS.indexOf(glyph));
        int u = (index % 8) * SYMBOL_SOURCE_CELL_SIZE;
        int v = (index / 8) * SYMBOL_SOURCE_CELL_SIZE;
        guiGraphics.blit(symbolTexture(), x, y, size, size, (float)u, (float)v, SYMBOL_SOURCE_CELL_SIZE, SYMBOL_SOURCE_CELL_SIZE, 512, 320);
    }

    private ResourceLocation symbolFrameTexture() {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/symbols/symbol_frame" + menu.variant().textureSuffix() + ".png");
    }

    private ResourceLocation symbolTexture() {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/symbols/symbols" + menu.variant().textureSuffix() + ".png");
    }

    private static ResourceLocation icon(String name) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/icons/" + name);
    }
}
