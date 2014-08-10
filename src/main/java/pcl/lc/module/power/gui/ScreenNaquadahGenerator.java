package pcl.lc.module.power.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pcl.lc.base.GenericContainerGUI;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.power.tile.TileNaquadahGenerator;

public class ScreenNaquadahGenerator extends GenericContainerGUI {

	private static final int guiWidth = 177;
	private static final int guiHeight = 208;

	private final TileNaquadahGenerator tileEntity;
	private final ContainerNaquadahGenerator container;
	private ResourceLocation background;

	public ScreenNaquadahGenerator(TileNaquadahGenerator te, EntityPlayer player) {
		super(new ContainerNaquadahGenerator(te, player), guiWidth, guiHeight);
		this.container = (ContainerNaquadahGenerator) inventorySlots;
		this.tileEntity = te;
		background = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/gui/naquadah_generator_${TEX_QUALITY}.png"));
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		bindTexture(background, 256, 256);
		drawTexturedRectUV(0, 0, 177, 208, 0, 0, 177d / 256d, 208d / 256d);
		drawTexturedRectUV(48, 96, (80.0d * container.energyFract), 11, 176d / 256d, 0,
				(80d * container.energyFract) / 256d, 11d / 256d);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		String energyLabel = String.format("%.2f / %.2f", container.energyStored, container.energyMax);
		int dw = fontRendererObj.getStringWidth(energyLabel);
		fontRendererObj.drawString(energyLabel, 48 + ((int) Math.floor((80d - dw) / 2)), 98,
				(container.energyStored > 0.00d) ? 0xFFFFFF : 0x9F0101, true);

		String tankLabel = I18n.format("tank.text",
				String.format("%.2f / %.2f", container.tankStored, container.tankMax));
		int dm = fontRendererObj.getStringWidth(tankLabel);
		fontRendererObj.drawString(tankLabel, 48 + ((int) Math.floor((80d - dm) / 2)), 113, 0xFFFFFF);

		ItemStack naquadah = new ItemStack(ModuleCore.Items.lanteaOreItem, 1, 0);

		if (container.burnProgress > 0.0d) {
			itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), naquadah, 80, 50);
			String progressLabel = String.format("%.0f%%", 10.0d * container.burnProgress);
			int dp = fontRendererObj.getStringWidth(progressLabel);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			fontRendererObj.drawString(progressLabel, (int) Math.floor(90d - (dp / 2)), 65, 0xFFFFFF);
		}

	}

}
