/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.rendering;

/**
 * OpenGL graphics buffering class contract interface.
 *
 * @author AfterLifeLochie
 *
 */
public interface IGraphicsBuffer {

	/**
	 * Called to initialize the graphics buffer.
	 */
	public void init();

	/**
	 * Called to ask the buffer if it is currently supported on the host
	 * platform. The result value from this method is populated after the call
	 * to {{@link #init()}.
	 * 
	 * @return If the buffer is supported.
	 */
	public boolean supported();

	/**
	 * Called to enter the graphics buffer. Any OpenGL operations after calling
	 * enter() will be fed to the appropriate buffer.
	 */
	public void enter();

	/**
	 * Called to exit the graphics buffer.
	 */
	public void exit();

	/**
	 * Called to bind the graphics buffer data to OpenGL. In the case of display
	 * lists, this causes the display lists to render. In the case of
	 * texture-buffers, the texture is bound.
	 *
	 * @param args
	 *            Any arguments to the bind operation - dependant on the
	 *            implementation.
	 */
	public void bind(Object... args);

	/**
	 * Called to release the graphics buffer data from OpenGL.
	 */
	public void release();

	/**
	 * Called to delete the graphics buffer.
	 */
	public void delete();

}
