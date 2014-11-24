package lc.client;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import lc.client.models.ModelError;
import lc.client.models.loader.WavefrontModel.WavefrontModelException;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderHook;
import lc.common.base.LCTileRenderer;
import lc.core.ResourceAccess;

public class DefaultTileRenderer extends LCTileRenderer {

	private ModelError model;
	private ArrayList<Class<? extends LCTile>> seenTypes;

	public DefaultTileRenderer() {
		try {
			model = new ModelError(ResourceAccess.getNamedResource("models/error.obj"));
		} catch (WavefrontModelException e) {
			e.printStackTrace();
		}
		seenTypes = new ArrayList<Class<? extends LCTile>>();
	}

	@Override
	public LCTileRenderer getParent() {
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderHook renderer, double x, double y, double z,
			float partialTickTime) {
		if (!seenTypes.contains(tile.getClass())) {
			Class<? extends LCTile> clazz = tile.getClass();
			seenTypes.add(clazz);
			LCLog.warn("Tile class %s fed to default renderer, check implementation!", clazz.getName());
		}
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);

		float a0 = (float) (180.0f - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0f;
		float a1 = (float) (-Minecraft.getMinecraft().thePlayer.rotationPitch) % 360.0f;

		GL11.glTranslated(x + 0.5f, y - 0.25f, z + 0.5f);
		GL11.glTranslated(0f, 0.5f, 0f);
		GL11.glRotatef(a0, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(a1, 1.0f, 0.0f, 0.0f);
		GL11.glTranslatef(0f, -0.5f, 0f);
		GL11.glScalef(0.02f, 0.02f, 0.02f);
		model.renderAll();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
	}

}
