package lc.client.opengl;

import org.lwjgl.opengl.GL11;

import lc.api.rendering.IGraphicsBuffer;

/**
 * OpenGL display list buffering mode.
 * 
 * @author AfterLifeLochie
 * 
 */
public class BufferDisplayList implements IGraphicsBuffer {

	private boolean assigned = false;
	private boolean entered = false;
	private boolean bound = false;
	private int idx;

	@Override
	public void init() {
		if (assigned)
			throw new RuntimeException("Illegal state: cannot init() while assigned.");
		idx = GL11.glGenLists(1);
		assigned = true;
	}

	@Override
	public void enter() {
		if (entered)
			throw new RuntimeException("Illegal state: cannot enter() while entered.");
		GL11.glNewList(idx, GL11.GL_COMPILE);
		entered = true;
	}

	@Override
	public void exit() {
		if (!entered)
			throw new RuntimeException("Illegal state: cannot exit() while exited.");
		GL11.glEnd();
		GL11.glEndList();
		entered = false;
	}

	@Override
	public void bind(Object... args) {
		if (bound)
			throw new RuntimeException("Illegal state: cannot bind() while bound.");
		GL11.glCallList(idx);
		bound = true;
	}

	@Override
	public void release() {
		if (!bound)
			throw new RuntimeException("Illegal state: cannot release() while released.");
		bound = false;
	}

	@Override
	public void delete() {
		if (!assigned)
			throw new RuntimeException("Illegal state: cannot delete() while not assigned.");
		GL11.glDeleteLists(idx, 1);
		assigned = false;
	}

}
