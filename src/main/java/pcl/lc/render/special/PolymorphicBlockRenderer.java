package pcl.lc.render.special;

import org.lwjgl.opengl.GL11;

import pcl.lc.api.internal.IPolymorphicRenderHost;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

public class PolymorphicBlockRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float ticks) {
		IPolymorphicRenderHost host = (IPolymorphicRenderHost) tileentity;
		int metatdata = host.getPolymorphicTargetBlockMetadata();
		Block renderAs = host.getPolymorphicTargetBlock();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);

		for (int side = 0; side < 6; side++) {
			// Icons are not actually resources, they're just used in the
			// TESR, so we need some trickery to convert a sided icon
			// to a resource we can mount.
			Icon sided = renderAs.getIcon(side, metatdata);
		}

		GL11.glPopMatrix();
	}
}
