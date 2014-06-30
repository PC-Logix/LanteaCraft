package pcl.lc.base;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import pcl.lc.api.internal.IPolymorphicRenderHost;

public class PolymorphicBlockRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float ticks) {
		IPolymorphicRenderHost host = (IPolymorphicRenderHost) tileentity;
		int metatdata = host.getPolymorphicTargetBlockMetadata();
		Block renderAs = host.getPolymorphicTargetBlock();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);

		GL11.glPopMatrix();
	}
}
