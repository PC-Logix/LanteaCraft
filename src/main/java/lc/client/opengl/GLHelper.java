package lc.client.opengl;

import org.lwjgl.opengl.GL11;

/**
 * OpenGL helpers.
 *
 * @author AfterLifeLochie
 *
 */
public class GLHelper {

	/**
	 * Push an immediate texture coordinate and vertex to the graphics card.
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @param u
	 *            The texture x-coordinate (u)
	 * @param v
	 *            The texture y-coordinate (v)
	 */
	public static void pushTexVertex(double x, double y, double z, double u, double v) {
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, z);
	}
}
