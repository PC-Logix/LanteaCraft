package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class LCBlockRenderer implements ILanteaCraftRenderer {

	/**
	 * Get the parent renderer. Called when a render function cannot be
	 * completed by the current renderer.
	 * 
	 * @return The parent renderer.
	 */
	public abstract Class<? extends LCBlockRenderer> getParent();

	/**
	 * Render a block in the player's inventory. If this operation cannot be
	 * completed by the renderer, it must return {@link false}. If the rendering
	 * can be completed, the renderer must return {@link true}. If the rendering
	 * is not completed, the parent renderer will be called to render the block.
	 * 
	 * @param block
	 *            The block
	 * @param renderer
	 *            The RenderBlocks instance
	 * @param metadata
	 *            The block's metadata
	 * @return If the rendering was completed
	 * @see {@link LCBlockRenderer#getParent()}
	 */
	public abstract boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata);

	/**
	 * Render a block in the world. If this operation cannot be completed by the
	 * renderer, it must return {@link false}. If the rendering can be
	 * completed, the renderer must return {@link true}. If the rendering is not
	 * completed, the parent renderer will be called to render the block.
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
	 * @see {@link LCBlockRenderer#getParent()}
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

	private double u0, v0, u1, v1, us, vs;
	private boolean textureOverridden;
	private float cmr, cmg, cmb;

	public void renderDefaultInventoryBlock(Block block, int metadata, RenderBlocks rb) {
		renderDefaultInventoryBlock(block, metadata, new Trans3(0, 0, 0), rb);
	}

	public void renderDefaultInventoryBlock(Block block, int metadata, Trans3 trans, RenderBlocks rb) {
		setUpTextureOverride(rb);
		setColorMultiplier(0xffffff);
		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1, 1, 1);
		tess.startDrawingQuads();
		renderCube(tess, trans, null, block, 0, 0, 0, metadata, 0xf000f0);
		tess.draw();
	}

	public boolean renderDefaultWorldBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks rb) {
		return renderDefaultWorldBlock(world, x, y, z, block, new Trans3(x + 0.5, y + 0.5, z + 0.5), rb);
	}

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

	protected static double cubeMap[][] = { { -0.5, -0.5, 0.5, 0, 0, -1, 1, 0, 0, 0, -1, 0 }, // DOWN
			{ -0.5, 0.5, -0.5, 0, 0, 1, 1, 0, 0, 0, 1, 0 }, // UP
			{ 0.5, 0.5, -0.5, 0, -1, 0, -1, 0, 0, 0, 0, -1 }, // NORTH
			{ -0.5, 0.5, 0.5, 0, -1, 0, 1, 0, 0, 0, 0, 1 }, // SOUTH
			{ -0.5, 0.5, -0.5, 0, -1, 0, 0, 0, 1, -1, 0, 0 }, // WEST
			{ 0.5, 0.5, 0.5, 0, -1, 0, 0, 0, -1, 1, 0, 0 }, // EAST
	};

	protected void renderCube(Tessellator tess, Trans3 t, IBlockAccess world, Block block, int x, int y, int z,
			int data, int brightness) {
		for (int i = 0; i < 6; i++) {
			selectTile(block.getIcon(i, data));
			if (world != null) {
				ForgeDirection d = ForgeDirection.getOrientation(i);
				Vector3 p = t.p(d.offsetX, d.offsetY, d.offsetZ);
				tess.setBrightness(block.getMixedBrightnessForBlock(world, p.floorX(), p.floorY(), p.floorZ()));
			} else
				tess.setBrightness(brightness);
			cubeFace(tess, t, cubeMap[i]);
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
