package lc.client.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import lc.api.rendering.IGraphicsBuffer;

/**
 * OpenGL to texture buffering mode.
 * 
 * @author AfterLifeLochie
 * 
 */
public class BufferTexture implements IGraphicsBuffer {

	private boolean assigned = false;
	private boolean entered = false;
	private boolean bound = false;

	private int texture, fbo, depth;
	private int width, height;

	/**
	 * Create a new texture buffer.
	 * 
	 * @param width
	 *            The virtual texture width.
	 * @param height
	 *            The virtual texture height.
	 */
	public BufferTexture(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void init() {
		if (assigned)
			throw new RuntimeException("Illegal state: cannot init() while assigned.");
		fbo = EXTFramebufferObject.glGenFramebuffersEXT();
		texture = GL11.glGenTextures();
		depth = EXTFramebufferObject.glGenRenderbuffersEXT();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_INT,
				(ByteBuffer) null);
		EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, texture, 0);
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depth);
		EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				ARBFramebufferObject.GL_DEPTH24_STENCIL8, width, height);
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				ARBFramebufferObject.GL_DEPTH_STENCIL_ATTACHMENT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, depth);
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		assigned = true;
	}

	@Override
	public void enter() {
		if (entered)
			throw new RuntimeException("Illegal state: cannot enter() while entered.");
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
		entered = true;
	}

	@Override
	public void exit() {
		if (!entered)
			throw new RuntimeException("Illegal state: cannot exit() while exited.");
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		entered = false;
	}

	@Override
	public void bind(Object... args) {
		if (bound)
			throw new RuntimeException("Illegal state: cannot bind() while bound.");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
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
		EXTFramebufferObject.glDeleteFramebuffersEXT(fbo);
		GL11.glDeleteTextures(texture);
		EXTFramebufferObject.glDeleteRenderbuffersEXT(depth);
		assigned = false;
	}

}
