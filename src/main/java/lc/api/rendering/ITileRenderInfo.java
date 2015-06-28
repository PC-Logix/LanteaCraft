/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.rendering;

import lc.common.util.data.StateMap;

/**
 * Contract interface for tile entity rendering information providers.
 *
 * @author AfterLifeLochie
 *
 */
public interface ITileRenderInfo {

	/**
	 * Get the rendering state map.
	 * 
	 * @return The state map.
	 */
	public StateMap tileRenderState();

	public Object tileAnimation();

	public double tileAnimationProgress();

}
