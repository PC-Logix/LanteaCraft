package pcl.lc.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityTransporterRing;
import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityTransporterRingRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		TileEntityTransporterRing platform = (TileEntityTransporterRing) tileEntity;
		if (!platform.isHost())
			return;

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float scale = 1.0f;
		GL11.glScalef(scale, scale, scale);
		FMLClientHandler.instance().getClient().renderEngine
				.bindTexture(LanteaCraft.getResource("textures/models/transport_rings_base_"
						+ LanteaCraft.getProxy().getRenderMode() + ".png"));
		GL11.glTranslated(x, y + 1.0d, z);
		LanteaCraft.Render.modelRingPlatformBase.renderAll();

		FMLClientHandler.instance().getClient().renderEngine.bindTexture(LanteaCraft
				.getResource("textures/models/transport_rings_" + LanteaCraft.getProxy().getRenderMode() + ".png"));
		GL11.glTranslated(0, -0.2, 0);

		double heightOf = platform.getAsStructure().getRingPosition(f);
		int numRingsOf = (int) Math.floor(heightOf / 0.5d);
		GL11.glTranslated(0, heightOf % 0.5d, 0);
		for (int i = 0; i < numRingsOf; i++) {
			GL11.glTranslated(0.d, 0.5d, 0d);
			LanteaCraft.Render.modelRingPlatformRing.renderAll();
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

}
