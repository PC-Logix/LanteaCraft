package pcl.lc.module.core.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResource;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import pcl.lc.base.render.font.WrittenFontRenderer;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleCore.Render;

public class GuiJacksonNotebook extends GuiScreen {

	private WrittenFontRenderer fontRenderer;
	private WrittenFontRenderer.PageBox[] pages;
	private int currentPage = 0;

	public GuiJacksonNotebook() {
		this(null);
	}

	public GuiJacksonNotebook(String index) {
		this.fontRenderer = Render.danielFontRenderer;
		StringBuffer fileData = new StringBuffer();
		fileData.append("Here lies lorum ipsum.\r\n\r\n");
		try {
			IResource resource = Minecraft.getMinecraft().getResourceManager()
					.getResource(ResourceAccess.getNamedResource("books/lipsum.book"));
			InputStream stream = resource.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			char[] buf = new char[1024];
			int len = 0;
			while ((len = reader.read(buf)) != -1)
				fileData.append(buf, 0, len);
			reader.close();
			this.pages = fontRenderer.boxParagraph(fileData.toString(), 350, 450, 2, 2);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		useTexture("noteback");
		drawTexturedRectUV(width / 2 - 200, height / 2 - 110, 400, 220, 0, 0, 1, 1);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		if (this.pages != null) {
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			if (this.pages.length > currentPage) {
				fontRenderer.renderPages(this.pages[currentPage], width / 2 - 185, height / 2 - 99, zLevel);
			}
			if (this.pages.length > currentPage + 1) {
				fontRenderer.renderPages(this.pages[currentPage + 1], width / 2 + 12, height / 2 - 98, zLevel);
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);

		if (par2 == Keyboard.KEY_LEFT) {
			currentPage -= 2;
			if (0 > currentPage)
				currentPage = 0;
		}

		if (par2 == Keyboard.KEY_RIGHT) {
			currentPage += 2;
			if (currentPage >= pages.length)
				currentPage = pages.length;
		}

	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);

	}

	private void useTexture(String name) {
		mc.getTextureManager().bindTexture(ResourceAccess.getNamedResource("textures/notebook/" + name + ".png"));
	}

	private void pushVertex(float x, float y, float z, float u, float v) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(x, y, z);
	}

	public void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs) {
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		tess.addVertexWithUV(x, y + h, zLevel, u, v + vs);
		tess.addVertexWithUV(x + w, y + h, zLevel, u + us, v + vs);
		tess.addVertexWithUV(x + w, y, zLevel, u + us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
	}
}
