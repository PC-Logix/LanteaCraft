package lc.common.base;

import org.lwjgl.opengl.GL11;

import lc.api.defs.IDefinitionReference;
import lc.api.defs.ILanteaCraftRenderer;
import lc.common.configuration.IConfigure;
import lc.common.impl.registry.DefinitionReference;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Internal block renderer base class.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCBlockRenderer implements ILanteaCraftRenderer, IConfigure {

	/**
	 * Get the parent renderer. Called when a render function cannot be
	 * completed by the current renderer.
	 *
	 * @return The parent renderer.
	 */
	public abstract Class<? extends LCBlockRenderer> getParent();

	/**
	 * Render a block in the player's hasInventory. If this operation cannot be
	 * completed by the renderer, it must return <code>false</code>. If the
	 * rendering can be completed, the renderer must return <code>true</code>.
	 * If the rendering is not completed, the parent renderer will be called to
	 * render the block.
	 *
	 * @param block
	 *            The block
	 * @param renderer
	 *            The RenderBlocks instance
	 * @param metadata
	 *            The block's metadata
	 * @return If the rendering was completed
	 * @see LCBlockRenderer#getParent()
	 */
	public abstract boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata);

	/**
	 * Render a block in the world. If this operation cannot be completed by the
	 * renderer, it must return <code>false</code>. If the rendering can be
	 * completed, the renderer must return <code>true</code>. If the rendering
	 * is not completed, the parent renderer will be called to render the block.
	 *
	 * @param block
	 *            The block
	 * @param renderer
	 *            The RenderBlocks instance
	 * @param world
	 *            The world access instance
	 * @param x
	 *            The x-coordinate of the block
	 * @param y
	 *            The y-coordinate of the block
	 * @param z
	 *            The z-coordinate of the block
	 * @return If the rendering was completed
	 * @see LCBlockRenderer#getParent()
	 */
	public abstract boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z);

	/**
	 * Asks if this block renderer renders blocks in 3D or 2D inside
	 * inventories.
	 *
	 * @return If this block renderer renders blocks in 3D or 2D inside
	 *         inventories.
	 */
	public abstract boolean renderInventoryItemAs3d();

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

	private double u0, v0, u1, v1, us, vs;
	private boolean textureOverridden;
	private float cmr, cmg, cmb;

	/**
	 * Render a default inventory block
	 *
	 * @param block
	 *            The block
	 * @param metadata
	 *            The metadata
	 * @param rb
	 *            The renderblocks instance
	 */
	public void renderDefaultInventoryBlock(Block block, int metadata, RenderBlocks rb) {
		renderDefaultInventoryBlock(block, metadata, new Trans3(0, 0, 0), rb);
	}

	/**
	 * Render a default inventory block
	 *
	 * @param block
	 *            The block
	 * @param metadata
	 *            The metadata
	 * @param trans
	 *            The local transformation matrix
	 * @param rb
	 *            The renderblocks instance
	 */
	public void renderDefaultInventoryBlock(Block block, int metadata, Trans3 trans, RenderBlocks rb) {
		setUpTextureOverride(rb);
		setColorMultiplier(0xffffff);
		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1, 1, 1);
		tess.startDrawingQuads();
		GL11.glTranslatef(0.0f, -0.1f, 0.0f);
		renderCube(tess, trans, null, block, 0, 0, 0, metadata, 0xf000f0);
		tess.draw();
	}

	/**
	 * Render a default world block
	 *
	 * @param world
	 *            The world
	 * @param x
	 *            The x-coord
	 * @param y
	 *            The y-coord
	 * @param z
	 *            The z-coord
	 * @param block
	 *            The block
	 * @param rb
	 *            The renderblocks instance
	 * @return If rendering was a success
	 */
	public boolean renderDefaultWorldBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks rb) {
		return renderDefaultWorldBlock(world, x, y, z, block, new Trans3(x + 0.5, y + 0.5, z + 0.5), rb);
	}

	/**
	 * Render a default world block
	 *
	 * @param world
	 *            The world
	 * @param x
	 *            The x-coord
	 * @param y
	 *            The y-coord
	 * @param z
	 *            The z-coord
	 * @param block
	 *            The block
	 * @param trans
	 *            The world render transformation matrix
	 * @param rb
	 *            The renderblocks instance
	 * @return If rendering was a success
	 */
	public boolean renderDefaultWorldBlock(IBlockAccess world, int x, int y, int z, Block block, Trans3 trans,
			RenderBlocks rb) {
		setUpTextureOverride(rb);
		setColorMultiplier(block.colorMultiplier(world, x, y, z));
		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1, 1, 1);
		renderCube(tess, trans, world, block, x, y, z, world.getBlockMetadata(x, y, z),
				block.getMixedBrightnessForBlock(world, x, y, z));
		return true;
	}

	/**
	 * Render a default item
	 * 
	 * @param stack
	 *            The itemstack to render
	 */
	public void renderDefaultItem(ItemStack stack) {
		IIcon iicon = stack.getItem().getIconFromDamage(stack.getItemDamage());
		if (iicon == null)
			return;
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
		TextureUtil.func_152777_a(false, false, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		float u0 = iicon.getMinU(), u1 = iicon.getMaxU();
		float v0 = iicon.getMinV(), v1 = iicon.getMaxV();
		float f4 = 0.0F;
		float f5 = 0.3F;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(-f4, -f5, 0.0F);
		float f6 = 1.25F;
		GL11.glScalef(f6, f6, f6);
		GL11.glTranslatef(-0.5f, -0.25f, 0.0f);
		renderItemIn2D(tessellator, u1, v0, u0, v1, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
		GL11.glEnable(GL11.GL_LIGHTING);
		TextureUtil.func_147945_b();
		GL11.glPopMatrix();
	}

	private void renderItemIn2D(Tessellator t, float u1, float v0, float u0, float v1, int w, int h, float p_78439_7_) {
		t.startDrawingQuads();
		t.setNormal(0.0F, 0.0F, 1.0F);
		t.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) u1, (double) v1);
		t.addVertexWithUV(1.0D, 0.0D, 0.0D, (double) u0, (double) v1);
		t.addVertexWithUV(1.0D, 1.0D, 0.0D, (double) u0, (double) v0);
		t.addVertexWithUV(0.0D, 1.0D, 0.0D, (double) u1, (double) v0);
		t.draw();
		t.startDrawingQuads();
		t.setNormal(0.0F, 0.0F, -1.0F);
		t.addVertexWithUV(0.0D, 1.0D, (double) (0.0F - p_78439_7_), (double) u1, (double) v0);
		t.addVertexWithUV(1.0D, 1.0D, (double) (0.0F - p_78439_7_), (double) u0, (double) v0);
		t.addVertexWithUV(1.0D, 0.0D, (double) (0.0F - p_78439_7_), (double) u0, (double) v1);
		t.addVertexWithUV(0.0D, 0.0D, (double) (0.0F - p_78439_7_), (double) u1, (double) v1);
		t.draw();
		float f5 = 0.5F * (u1 - u0) / (float) w;
		float f6 = 0.5F * (v1 - v0) / (float) h;
		t.startDrawingQuads();
		t.setNormal(-1.0F, 0.0F, 0.0F);
		int k;
		float f7;
		float f8;

		for (k = 0; k < w; ++k) {
			f7 = (float) k / (float) w;
			f8 = u1 + (u0 - u1) * f7 - f5;
			t.addVertexWithUV((double) f7, 0.0D, (double) (0.0F - p_78439_7_), (double) f8, (double) v1);
			t.addVertexWithUV((double) f7, 0.0D, 0.0D, (double) f8, (double) v1);
			t.addVertexWithUV((double) f7, 1.0D, 0.0D, (double) f8, (double) v0);
			t.addVertexWithUV((double) f7, 1.0D, (double) (0.0F - p_78439_7_), (double) f8, (double) v0);
		}

		t.draw();
		t.startDrawingQuads();
		t.setNormal(1.0F, 0.0F, 0.0F);
		float f9;

		for (k = 0; k < w; ++k) {
			f7 = (float) k / (float) w;
			f8 = u1 + (u0 - u1) * f7 - f5;
			f9 = f7 + 1.0F / (float) w;
			t.addVertexWithUV((double) f9, 1.0D, (double) (0.0F - p_78439_7_), (double) f8, (double) v0);
			t.addVertexWithUV((double) f9, 1.0D, 0.0D, (double) f8, (double) v0);
			t.addVertexWithUV((double) f9, 0.0D, 0.0D, (double) f8, (double) v1);
			t.addVertexWithUV((double) f9, 0.0D, (double) (0.0F - p_78439_7_), (double) f8, (double) v1);
		}

		t.draw();
		t.startDrawingQuads();
		t.setNormal(0.0F, 1.0F, 0.0F);

		for (k = 0; k < h; ++k) {
			f7 = (float) k / (float) h;
			f8 = v1 + (v0 - v1) * f7 - f6;
			f9 = f7 + 1.0F / (float) h;
			t.addVertexWithUV(0.0D, (double) f9, 0.0D, (double) u1, (double) f8);
			t.addVertexWithUV(1.0D, (double) f9, 0.0D, (double) u0, (double) f8);
			t.addVertexWithUV(1.0D, (double) f9, (double) (0.0F - p_78439_7_), (double) u0, (double) f8);
			t.addVertexWithUV(0.0D, (double) f9, (double) (0.0F - p_78439_7_), (double) u1, (double) f8);
		}

		t.draw();
		t.startDrawingQuads();
		t.setNormal(0.0F, -1.0F, 0.0F);

		for (k = 0; k < h; ++k) {
			f7 = (float) k / (float) h;
			f8 = v1 + (v0 - v1) * f7 - f6;
			t.addVertexWithUV(1.0D, (double) f7, 0.0D, (double) u0, (double) f8);
			t.addVertexWithUV(0.0D, (double) f7, 0.0D, (double) u1, (double) f8);
			t.addVertexWithUV(0.0D, (double) f7, (double) (0.0F - p_78439_7_), (double) u1, (double) f8);
			t.addVertexWithUV(1.0D, (double) f7, (double) (0.0F - p_78439_7_), (double) u0, (double) f8);
		}

		t.draw();
	}

	private void setColorMultiplier(int color) {
		cmr = (color >> 16 & 0xff) / 255.0F;
		cmg = (color >> 8 & 0xff) / 255.0F;
		cmb = (color & 0xff) / 255.0F;
	}

	private void setUpTextureOverride(RenderBlocks rb) {
		textureOverridden = false;
		if (rb != null) {
			IIcon icon = rb.overrideBlockTexture;
			if (icon != null) {
				useIcon(icon);
				textureOverridden = true;
			}
		}
	}

	private void selectTile(IIcon icon) {
		if (!textureOverridden)
			useIcon(icon);
		us = (u1 - u0) / 16;
		vs = (v1 - v0) / 16;
	}

	private void useIcon(IIcon icon) {
		u0 = icon.getMinU();
		v0 = icon.getMinV();
		u1 = icon.getMaxU();
		v1 = icon.getMaxV();
	}

	/** The cube face map */
	protected static double cubeMap[][] = { { -0.5, -0.5, 0.5, 0, 0, -1, 1, 0, 0, 0, -1, 0 }, // DOWN
			{ -0.5, 0.5, -0.5, 0, 0, 1, 1, 0, 0, 0, 1, 0 }, // UP
			{ 0.5, 0.5, -0.5, 0, -1, 0, -1, 0, 0, 0, 0, -1 }, // NORTH
			{ -0.5, 0.5, 0.5, 0, -1, 0, 1, 0, 0, 0, 0, 1 }, // SOUTH
			{ -0.5, 0.5, -0.5, 0, -1, 0, 0, 0, 1, -1, 0, 0 }, // WEST
			{ 0.5, 0.5, 0.5, 0, -1, 0, 0, 0, -1, 1, 0, 0 }, // EAST
	};

	/**
	 * Render a cube on screen
	 *
	 * @param tess
	 *            The tesselator
	 * @param t
	 *            The rotation transformation element
	 * @param world
	 *            The world
	 * @param block
	 *            The block
	 * @param x
	 *            The x-coord
	 * @param y
	 *            The y-coord
	 * @param z
	 *            The z-coord
	 * @param data
	 *            The block data
	 * @param brightness
	 *            The block brightness
	 */
	protected void renderCube(Tessellator tess, Trans3 t, IBlockAccess world, Block block, int x, int y, int z,
			int data, int brightness) {
		for (int i = 0; i < 6; i++) {
			selectTile(block.getIcon(i, data));
			if (world != null) {
				ForgeDirection d = ForgeDirection.getOrientation(i);
				Vector3 p = t.p(d.offsetX, d.offsetY, d.offsetZ);
				tess.setBrightness(block.getMixedBrightnessForBlock(world, p.fx(), p.fy(), p.fz()));
			} else
				tess.setBrightness(brightness);
			cubeFace(tess, t.translate(0.5d, 0.5d, 0.5d), cubeMap[i]);
		}
	}

	private void setNormal(Tessellator tess, Trans3 t, double nx, double ny, double nz, double shade) {
		Vector3 n = t.v(nx, ny, nz);
		float bm = (float) (shade * (0.6 * n.x * n.x + 0.8 * n.z * n.z + (n.y > 0 ? 1 : 0.5) * n.y * n.y));
		tess.setNormal((float) n.x, (float) n.y, (float) n.z);
		tess.setColorOpaque_F(bm * cmr, bm * cmg, bm * cmb);
	}

	private void cubeFace(Tessellator tess, Trans3 t, double[] c) {
		setNormal(tess, t, c[9], c[10], c[11], 1.0);
		face(tess, t, c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], 0, 0, 16, 16);
	}

	private void face(Tessellator tess, Trans3 t, double x, double y, double z, double dx1, double dy1, double dz1,
			double dx2, double dy2, double dz2, double u, double v, double du, double dv) {
		vertex(tess, t, x, y, z, u, v);
		vertex(tess, t, x + dx1, y + dy1, z + dz1, u, v + dv);
		vertex(tess, t, x + dx1 + dx2, y + dy1 + dy2, z + dz1 + dz2, u + du, v + dv);
		vertex(tess, t, x + dx2, y + dy2, z + dz2, u + du, v);
	}

	private void vertex(Tessellator tess, Trans3 t, double x, double y, double z, double u, double v) {
		Vector3 p = t.p(x, y, z);
		tess.addVertexWithUV(p.x, p.y, p.z, u0 + u * us, v0 + v * vs);
	}

}
