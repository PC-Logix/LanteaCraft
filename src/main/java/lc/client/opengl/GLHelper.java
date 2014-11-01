package lc.client.opengl;

import org.lwjgl.opengl.GL11;

public class GLHelper {

	public static void pushTexVertex(double x, double y, double z, double u, double v) {
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, z);
	}
}
