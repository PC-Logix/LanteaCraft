package lc.client.render.fabs.blocks;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import lc.client.render.fabs.DefaultBlockRenderer;
import lc.common.base.LCBlockRenderer;
import lc.common.configuration.xml.ComponentConfig;

/**
 * Brazier block renderer implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class BlockBrazierRenderer extends LCBlockRenderer {

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		return DefaultBlockRenderer.class;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		Tessellator t = Tessellator.instance;
		IIcon blockicon = block.getIcon(0, metadata);
		t.setBrightness(15);
		t.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		t.startDrawingQuads();
		GL11.glTranslatef(0.0f, -0.25f, 0.0f);
		renderTaperBox(0.33, 0.0, 0.33, 1.0 - 0.66, 1, 1.0 - 0.66, 0.66, blockicon);
		renderBackplane(0.11, 1.0, 0.11, 1.0 - 0.22, 0.22, 1.0 - 0.22, 0.56, blockicon);
		t.draw();
		return true;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		Tessellator t = Tessellator.instance;
		IIcon blockicon = block.getIcon(0, world.getBlockMetadata(x, y, z));
		int lightValue = block.getMixedBrightnessForBlock(world, x, y, z);
		t.setBrightness(lightValue);
		t.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		renderTaperBox(x + 0.33, y, z + 0.33, 1.0 - 0.66, 1, 1.0 - 0.66, 0.66, blockicon);
		renderBackplane(x + 0.11, y + 1, z + 0.11, 1.0 - 0.22, 0.22, 1.0 - 0.22, 0.56, blockicon);
		return true;
	}

	private void renderBackplane(double x, double y, double z, double wi, double hi, double de, double frac, IIcon icon) {
		Tessellator tessellator = Tessellator.instance;

		double u0 = (double) icon.getInterpolatedU(0.0D), u1 = (double) icon.getInterpolatedU(16.0D);
		double v0 = (double) icon.getInterpolatedV(0.0D), v1 = (double) icon.getInterpolatedV(16.0D);

		double x0 = x + 0, x1 = x + wi;
		double y0 = y + 0, y1 = y + hi;
		double z0 = z + 0, z1 = z + de;

		double ifrac = 1.0f - frac;
		double x2 = (wi * ifrac), z2 = (de * ifrac);

		tessellator.addVertexWithUV(x0 + x2, y0, z1 - z2, u0, v1);
		tessellator.addVertexWithUV(x0 + x2, y0, z0 + z2, u0, v0);
		tessellator.addVertexWithUV(x0, y1, z0, u1, v0);
		tessellator.addVertexWithUV(x0, y1, z1, u1, v1);

		tessellator.addVertexWithUV(x0 + x2, y0, z1 - z2, u0, v1);
		tessellator.addVertexWithUV(x0, y1, z1, u1, v1);
		tessellator.addVertexWithUV(x0, y1, z0, u1, v0);
		tessellator.addVertexWithUV(x0 + x2, y0, z0 + z2, u0, v0);

		tessellator.addVertexWithUV(x1, y1, z1, u0, v1);
		tessellator.addVertexWithUV(x1, y1, z0, u0, v0);
		tessellator.addVertexWithUV(x1 - x2, y0, z0 + z2, u1, v0);
		tessellator.addVertexWithUV(x1 - x2, y0, z1 - z2, u1, v1);

		tessellator.addVertexWithUV(x1, y1, z1, u0, v1);
		tessellator.addVertexWithUV(x1 - x2, y0, z1 - z2, u1, v1);
		tessellator.addVertexWithUV(x1 - x2, y0, z0 + z2, u1, v0);
		tessellator.addVertexWithUV(x1, y1, z0, u0, v0);

		tessellator.addVertexWithUV(x0, y1, z1, u0, v0);
		tessellator.addVertexWithUV(x1, y1, z1, u0, v1);
		tessellator.addVertexWithUV(x1 - x2, y0, z1 - z2, u1, v1);
		tessellator.addVertexWithUV(x0 + x2, y0, z1 - z2, u1, v0);

		tessellator.addVertexWithUV(x0, y1, z1, u0, v0);
		tessellator.addVertexWithUV(x0 + x2, y0, z1 - z2, u1, v0);
		tessellator.addVertexWithUV(x1 - x2, y0, z1 - z2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z1, u0, v1);

		tessellator.addVertexWithUV(x0, y1, z0, u0, v0);
		tessellator.addVertexWithUV(x0 + x2, y0, z0 + x2, u0, v1);
		tessellator.addVertexWithUV(x1 - x2, y0, z0 + x2, u1, v1);
		tessellator.addVertexWithUV(x1, y1, z0, u1, v0);

		tessellator.addVertexWithUV(x0, y1, z0, u0, v0);
		tessellator.addVertexWithUV(x1, y1, z0, u1, v0);
		tessellator.addVertexWithUV(x1 - x2, y0, z0 + x2, u1, v1);
		tessellator.addVertexWithUV(x0 + x2, y0, z0 + x2, u0, v1);
	}

	private void renderTaperBox(double x, double y, double z, double wi, double hi, double de, double frac, IIcon icon) {
		Tessellator tessellator = Tessellator.instance;

		double u0 = (double) icon.getInterpolatedU(0.0D), u1 = (double) icon.getInterpolatedU(16.0D);
		double v0 = (double) icon.getInterpolatedV(0.0D), v1 = (double) icon.getInterpolatedV(16.0D);

		double x0 = x + 0, x1 = x + wi;
		double y0 = y + 0, y1 = y + hi;
		double z0 = z + 0, z1 = z + de;

		double ifrac = 1.0f - frac;
		double x2 = (wi * ifrac), z2 = (de * ifrac);

		tessellator.addVertexWithUV(x0 + x2, y1, z1 - z2, u0, v1);
		tessellator.addVertexWithUV(x0 + x2, y1, z0 + z2, u0, v0);
		tessellator.addVertexWithUV(x0, y0, z0, u1, v0);
		tessellator.addVertexWithUV(x0, y0, z1, u1, v1);

		tessellator.addVertexWithUV(x1, y0, z1, u0, v1);
		tessellator.addVertexWithUV(x0, y0, z1, u1, v1);
		tessellator.addVertexWithUV(x0, y0, z0, u1, v0);
		tessellator.addVertexWithUV(x1, y0, z0, u0, v0);

		tessellator.addVertexWithUV(x1 - x2, y1, z1 - z2, u0, v1);
		tessellator.addVertexWithUV(x1 - x2, y1, z0 + z2, u0, v0);
		tessellator.addVertexWithUV(x0 + x2, y1, z0 + z2, u1, v0);
		tessellator.addVertexWithUV(x0 + x2, y1, z1 - z2, u1, v1);

		tessellator.addVertexWithUV(x1, y0, z1, u0, v1);
		tessellator.addVertexWithUV(x1, y0, z0, u0, v0);
		tessellator.addVertexWithUV(x1 - x2, y1, z0 + z2, u1, v0);
		tessellator.addVertexWithUV(x1 - x2, y1, z1 - z2, u1, v1);

		tessellator.addVertexWithUV(x0, y0, z1, u0, v0);
		tessellator.addVertexWithUV(x1, y0, z1, u0, v1);
		tessellator.addVertexWithUV(x1 - x2, y1, z1 - z2, u1, v1);
		tessellator.addVertexWithUV(x0 + x2, y1, z1 - z2, u1, v0);

		tessellator.addVertexWithUV(x0, y0, z0, u0, v0);
		tessellator.addVertexWithUV(x0 + x2, y1, z0 + x2, u0, v1);
		tessellator.addVertexWithUV(x1 - x2, y1, z0 + x2, u1, v1);
		tessellator.addVertexWithUV(x1, y0, z0, u1, v0);
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		return true;
	}

}
