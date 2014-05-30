package pcl.lc.guis;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

public class GuiJacksonNotebook extends GuiScreen {

	public GuiJacksonNotebook() {
	}

	public GuiJacksonNotebook(String index) {
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
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		useTexture("noteback");
		drawTexturedRectUV(width / 2 - 150, height / 2 - 90, 300, 180, 0, 0, 1, 1);
		GL11.glPopMatrix();
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);

	}

	private void useTexture(String name) {
		mc.getTextureManager().bindTexture(LanteaCraft.getResource("textures/notebook/" + name + ".png"));
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
