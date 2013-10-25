//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic GUI Screen
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.util.*;

import net.minecraftforge.client.*;

//------------------------------------------------------------------------------------------------

public class BaseGuiContainer extends GuiContainer {

	final static int defaultTextColor = 0x404040;

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;
	int textColor = defaultTextColor;
	boolean textShadow = false;

//	public BaseGuiContainer() {
//		super(new BaseContainer());
//	}
	
	public BaseGuiContainer(Container container, int width, int height) {
		super(container);
		xSize = width;
		ySize = height;
	}
	
	public BaseGuiContainer(BaseContainer container) {
		this(container, container.xSize, container.ySize);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		resetColor();
		textColor = defaultTextColor;
		textShadow = false;
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		drawBackgroundLayer();
		GL11.glPopMatrix();
	}
	
	void drawBackgroundLayer() {
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		drawForegroundLayer();
	}
	
	void drawForegroundLayer() {
	}

	void close() {
		mc.thePlayer.closeScreen();
	}
	
	void bindTexture(ResourceLocation rsrc) {
		bindTexture(rsrc, 1, 1);
	}
	
	void bindTexture(ResourceLocation rsrc, int usize, int vsize) {
		//System.out.printf("BaseGuiContainer.bindTexture: %s size (%s, %s)\n", rsrc, usize, vsize);
		//mc.renderEngine.bindTexture(path);
		mc.getTextureManager().bindTexture(rsrc);
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	void drawTexturedRect(double x, double y, double w, double h) {
		drawTexturedRectUV(x, y, w, h, 0, 0, 1, 1);
	}
	
	void drawTexturedRect(double x, double y, double w, double h, double u, double v) {
		drawTexturedRect(x, y, w, h, u, v, w, h);
	}

	void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs) {
		drawTexturedRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale);
	}
	
	void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs) {
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque_F(red, green, blue);
		tess.addVertexWithUV(x, y+h, zLevel, u, v+vs);
		tess.addVertexWithUV(x+w, y+h, zLevel, u+us, v+vs);
		tess.addVertexWithUV(x+w, y, zLevel, u+us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
	}
	
	public void setColor(int hex) {
		setColor((hex >> 16) / 255.0, ((hex >> 8) & 0xff) / 255.0, (hex & 0xff) / 255.0);
	}
	
	public void setColor(double r, double g, double b) {
		red = (float)r;
		green = (float)g;
		blue = (float)b;
	}
	
	public void resetColor() {
		setColor(1, 1, 1);
	}

	public void drawString(String s, int x, int y) {
		fontRenderer.drawString(s, x, y, textColor, textShadow);
	}

	public void drawCenteredString(String s, int x, int y) {
		fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y, textColor, textShadow);
	}
	
	public void drawInventoryName(IInventory inv, int x, int y) {
		drawString(inventoryName(inv), x, y);
	}
	
	public void drawPlayerInventoryName() {
		drawString(playerInventoryName(), 8, ySize - 96 + 2);
	}
	
	public static String inventoryName(IInventory inv) {
		String name = inv.getInvName();
		if (!inv.isInvNameLocalized())
			name = StatCollector.translateToLocal(name);
		return name;
	}
	
	public static String playerInventoryName() {
		return StatCollector.translateToLocal("container.inventory");
	}

}

//------------------------------------------------------------------------------------------------

//class DummyContainer extends Container {
//
//	public boolean canInteractWith(EntityPlayer var1) {
//		return true;
//	}
//
//}
