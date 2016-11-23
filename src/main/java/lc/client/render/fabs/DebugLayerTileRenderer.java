package lc.client.render.fabs;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import lc.LCRuntime;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.util.math.Matrix3;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;

public class DebugLayerTileRenderer extends LCTileRenderer {

	/** Turn rotations expressed around the Y axis */
	private static double[] blockTurnRotations = { 0.0d, 90.0d, 180.0d, -90.0d };

	public DebugLayerTileRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public LCTileRenderer getParent() {
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderPipeline renderer, double x, double y, double z,
			float partialTickTime) {
		if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().thePlayer.inventory == null
				|| Minecraft.getMinecraft().thePlayer.inventory.armorInventory == null)
			return false;
		ItemStack[] wearStack = Minecraft.getMinecraft().thePlayer.inventory.armorInventory;
		Item glassesStack = LCRuntime.runtime.items().glasses.getItem();
		boolean flag = false;
		for (int i = 0; i < wearStack.length; i++)
			if (wearStack[i] != null && wearStack[i].getItem() != null && wearStack[i].getItem().equals(glassesStack))
				flag = true;
		if (!flag)
			return false;

		GL11.glPushMatrix();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);
		GL11.glLineWidth(1.0f);
		batchRenderAxis();
		GL11.glPopMatrix();

		Block ablock = tile.getBlockType();
		if (ablock instanceof LCBlock) {
			LCBlock target = (LCBlock) ablock;
			AxisAlignedBB box = tile.getRenderBoundingBox();
			AxisAlignedBB clip = target.getCollisionBoundingBoxFromPool(tile.getWorldObj(), (int) x, (int) y, (int) z);

			if (clip != null) {
				GL11.glPushMatrix();
				GL11.glLineWidth(1.0f);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
				batchRenderClipBox(clip, true, x, y, z, tile.getWorldObj().getWorldTime(), 0.5f);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			} else {
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5f, y + 0.5f, z + 0.5f);
				GL11.glLineWidth(1.0f);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
				batchRenderClipBox(box, false, x, y, z, tile.getWorldObj().getWorldTime(), 0.2f);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}

			if (target.canRotate()) {
				ForgeDirection facing = tile.getRotation();
				if (facing != null) {
					int which = DefaultBlockRenderer.rotationMap[facing.ordinal()];
					GL11.glPushMatrix();
					GL11.glTranslated(x, y, z);
					GL11.glPushMatrix();
					GL11.glTranslatef(0.5f, 0.5f, 0.5f);
					GL11.glRotated(blockTurnRotations[which], 0.0d, 1.0d, 0.0d);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
					float delta = 0.05f;
					batchRenderGBox((float) (box.maxX - box.minX) + delta, (float) (box.maxY - box.minY) + delta,
							(float) (box.maxZ - box.minZ) + delta, 0.5f);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
					GL11.glPopMatrix();
				} else {
					LCLog.debug("DebugLayerTileRenderer got canRotate block but no getRotation tile!");
				}
			}
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glPopMatrix();
		return true;
	}

	private void batchRenderClipBox(AxisAlignedBB box, boolean hit, double x, double y, double z, float hl, float opac) {
		//GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
		float pq = 0.2f + 0.6f * ((1.0f + (float) Math.sin(0.2f * hl)) / 2.0f);
		GL11.glTranslated(x, y, z);
		if (hit) {
			GL11.glColor4f(1.0f, pq, 0.0f, opac);
		} else
			GL11.glColor4f(0.0f, pq, 1.0f, opac);
		float delta = 0.002f;
		GL11.glBegin(GL11.GL_QUADS);
		LCLog.debug("batchRenderClipBox %s %s", hit, box);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);

		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);

		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);

		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);

		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.minZ);
		GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
		GL11.glVertex3d(box.minX, box.minY, box.maxZ);

		GL11.glVertex3d(box.minX, box.minY, box.minZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
		GL11.glVertex3d(box.minX, box.maxY, box.minZ);
		GL11.glEnd();

		//GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
	}

	private void batchRenderGBox(float w, float h, float d, float opac) {
		float i = w / 2, j = h / 2, k = d / 2;
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glColor4f(0.25f, 0.25f, 1.0f, opac);
		GL11.glVertex3f(-i, -j, k);
		GL11.glVertex3f(i, -j, k);
		GL11.glVertex3f(i, j, k);
		GL11.glVertex3f(-i, j, k);

		GL11.glColor4f(0.25f, 1.0f, 0.25f, opac);
		GL11.glVertex3f(-i, j, -k);
		GL11.glVertex3f(-i, j, k);
		GL11.glVertex3f(i, j, k);
		GL11.glVertex3f(i, j, -k);

		GL11.glColor4f(1.0f, 0.25f, 0.25f, opac);
		GL11.glVertex3f(i, -j, -k);
		GL11.glVertex3f(i, j, -k);
		GL11.glVertex3f(i, j, k);
		GL11.glVertex3f(i, -j, k);

		GL11.glColor4f(0.0f, 0.0f, 0.33f, opac);
		GL11.glVertex3f(-i, -j, -k);
		GL11.glVertex3f(-i, j, -k);
		GL11.glVertex3f(i, j, -k);
		GL11.glVertex3f(i, -j, -k);

		GL11.glColor4f(0.0f, 0.33f, 0.0f, opac);
		GL11.glVertex3f(-i, -j, -k);
		GL11.glVertex3f(i, -j, -k);
		GL11.glVertex3f(i, -j, k);
		GL11.glVertex3f(-i, -j, k);

		GL11.glColor4f(0.33f, 0.0f, 0.0f, opac);
		GL11.glVertex3f(-i, -j, -k);
		GL11.glVertex3f(-i, -j, k);
		GL11.glVertex3f(-i, j, k);
		GL11.glVertex3f(-i, j, -k);

		GL11.glEnd();
	}

	private void batchRenderAxis() {
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glVertex3f(0.0f, 0.0f, 0.0f);
		GL11.glVertex3f(1.0f, 0.0f, 0.0f);
		GL11.glColor3f(0.0f, 1.0f, 0.0f);
		GL11.glVertex3f(0.0f, 0.0f, 0.0f);
		GL11.glVertex3f(0.0f, 1.0f, 0.0f);
		GL11.glColor3f(0.0f, 0.0f, 1.0f);
		GL11.glVertex3f(0.0f, 0.0f, 0.0f);
		GL11.glVertex3f(0.0f, 0.0f, 1.0f);
		GL11.glEnd();
	}

}
