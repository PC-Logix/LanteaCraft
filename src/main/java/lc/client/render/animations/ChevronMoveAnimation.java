package lc.client.render.animations;

import lc.client.animation.Animation;
import lc.common.base.LCTile;
import lc.common.util.game.RunnableTileCallback;

/**
 * Chevron move animation implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class ChevronMoveAnimation extends Animation {

	/**
	 * Move a chevron on the Stargate
	 * 
	 * @param time
	 *            The speed at which to move
	 * @param whichChevron
	 *            Which chevron to animate (0-9)
	 * @param newPos
	 *            The new position of the chevron
	 * @param newLight
	 *            The new light value of the chevron (0.0 = off, 0.5 = lit)
	 * @param resample
	 *            If the properties need to be resampled before animating
	 */
	public ChevronMoveAnimation(double time, int whichChevron, double newPos, double newLight, boolean resample) {
		super(time, resample, new RunnableTileCallback() {
			@Override
			public void run(LCTile tile) {
				tile.mixer().replayChannel("lock");
			}
		}, null);
		addProperty("chevron-dist-" + whichChevron, 0.0d, newPos, InterpolationMode.SQUARE);
		addProperty("chevron-light-" + whichChevron, 0.0d, newLight, InterpolationMode.LINEAR);
	}

}
