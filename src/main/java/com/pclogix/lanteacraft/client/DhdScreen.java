package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.StargateAddress;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.pclogix.lanteacraft.network.DialStargatePayload;
import com.pclogix.lanteacraft.network.ToggleDhdIrisPayload;
import com.pclogix.lanteacraft.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class DhdScreen extends Screen {
    private static final String GLYPHS = StargateAddress.LEGACY_GLYPHS;

    private static final int DHD_WIDTH = 260;
    private static final int DHD_HEIGHT = 180;
    private static final int PANEL_TOP_PADDING = 48;
    private static final int MAX_ADDRESS_LENGTH = StargateAddress.MAX_ADDRESS_LENGTH;

    private final BlockPos dhdPos;
    private final ResourceLocation guiTexture;
    private final ResourceLocation centreTexture;
    private final StringBuilder address = new StringBuilder();
    private int left;
    private int top;
    private int centreX;
    private int centreY;
    private int cursorTicks;
    private Button irisButton;

    public DhdScreen(BlockPos dhdPos, StargateVariant variant) {
        super(Component.literal("Dial Home Device"));
        this.dhdPos = dhdPos.immutable();
        this.guiTexture = dhdTexture("dhd_gui", variant);
        this.centreTexture = dhdTexture("dhd_centre", variant);
    }

    @Override
    protected void init() {
        left = (width - DHD_WIDTH) / 2;
        top = (height - DHD_HEIGHT - PANEL_TOP_PADDING) / 2 + PANEL_TOP_PADDING;
        centreX = left + DHD_WIDTH / 2;
        centreY = top + DHD_HEIGHT / 2;
        irisButton = addRenderableWidget(Button.builder(Component.translatable("screen.lanteacraft.dhd.iris"), button -> {
            playButtonSound();
            PacketDistributor.sendToServer(new ToggleDhdIrisPayload(dhdPos));
        }).bounds(left + DHD_WIDTH - 62, top - 28, 54, 20).build());
    }

    @Override
    public void tick() {
        cursorTicks = (cursorTicks + 1) % 20;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(guiTexture, left, top, DHD_WIDTH, DHD_HEIGHT, 0.0F, 0.0F, 512, 512, 512, 512);
        drawCentreButton(guiGraphics);

        String display = address + (cursorTicks > 10 && address.length() < MAX_ADDRESS_LENGTH ? "_" : " ");
        guiGraphics.drawCenteredString(font, display, width / 2, top - 18, 0x66FFFF);
        guiGraphics.drawCenteredString(font, title, width / 2, top - 34, 0xD0D0D0);
        irisButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int dhdButton = findDhdButton((int)mouseX, (int)mouseY);
            if (dhdButton >= 0) {
                pressDhdButton(dhdButton);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        char glyph = Character.toUpperCase(codePoint);
        if (GLYPHS.indexOf(glyph) >= 0) {
            appendGlyph(glyph);
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            orangeButtonPressed();
            return true;
        }

        if (Screen.isPaste(keyCode)) {
            pasteClipboard();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
            backspace();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawCentreButton(GuiGraphics guiGraphics) {
        int rx = (int)(DHD_WIDTH * 48.0D / 512.0D);
        int ry = (int)(DHD_HEIGHT * 48.0D / 346.0D);
        int buttonLeft = centreX - rx;
        int buttonTop = centreY - ry + 8;
        int buttonWidth = rx * 2;
        int buttonHeight = (int)(ry * 1.5D);

        guiGraphics.setColor(address.length() > 0 ? 1.0F : 0.75F, address.length() > 0 ? 0.75F : 0.45F, address.length() > 0 ? 0.25F : 0.08F, 1.0F);
        guiGraphics.blit(centreTexture, buttonLeft, buttonTop, buttonWidth, buttonHeight, 128.0F, 0.0F, 128, 96, 256, 128);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static ResourceLocation dhdTexture(String name, StargateVariant variant) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/gui/dhd/" + name + variant.textureSuffix() + ".png");
    }

    private int findDhdButton(int mouseX, int mouseY) {
        int localX = mouseX - left;
        int localY = mouseY - top;
        double x = -(localX - DHD_WIDTH / 2.0D);
        double y = -(localY - DHD_HEIGHT / 2.0D) * DHD_WIDTH / (double)DHD_HEIGHT;
        double r = Math.hypot(x, y);
        double radius1 = DHD_WIDTH * 0.1D;
        double radius2 = DHD_WIDTH * 0.275D;
        double radius3 = DHD_WIDTH * 0.45D;

        if (r > radius3) {
            return -1;
        }

        if (r <= radius1) {
            return 0;
        }

        double angle = Math.toDegrees(Math.atan2(y, x));
        if (angle < 0.0D) {
            angle += 360.0D;
        }

        int first = r <= radius2 ? 20 : 1;
        return first + (int)Math.floor(angle * 19.0D / 360.0D);
    }

    private void pressDhdButton(int button) {
        if (button == 0) {
            orangeButtonPressed();
        } else if (button > 38) {
            backspace();
        } else {
            appendGlyph(GLYPHS.charAt(button - 1));
        }
    }

    private void appendGlyph(char glyph) {
        if (address.length() < MAX_ADDRESS_LENGTH) {
            playButtonSound();
            address.append(glyph);
        }
    }

    private void backspace() {
        if (address.length() > 0) {
            playButtonSound();
            address.deleteCharAt(address.length() - 1);
        }
    }

    private void pasteClipboard() {
        String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
        for (int i = 0; i < clipboard.length() && address.length() < MAX_ADDRESS_LENGTH; i++) {
            char glyph = Character.toUpperCase(clipboard.charAt(i));
            if (GLYPHS.indexOf(glyph) >= 0) {
                address.append(glyph);
            }
        }
    }

    private void orangeButtonPressed() {
        playButtonSound();
        PacketDistributor.sendToServer(new DialStargatePayload(dhdPos, address.toString()));
        onClose();
    }

    private void playButtonSound() {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(ModSounds.DHD_BUTTON.get(), 0.75F, 1.0F);
        }
    }
}
