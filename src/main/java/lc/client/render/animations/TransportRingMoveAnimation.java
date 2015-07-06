package lc.client.render.animations;

import lc.client.animation.Animation;
import lc.common.util.game.RunnableTileCallback;

/**
 * Transport ring movement animation
 * 
 * @author AfterLifeLochie
 *
 */
public class TransportRingMoveAnimation extends Animation {

	/**
	 * Move a transport ring on the ring stack to a certain height
	 * 
	 * @param time
	 *            The speed at which to move
	 * @param whichRing
	 *            Which ring to animate
	 * @param newHeight
	 *            The new height of the chevron
	 * @param doBefore
	 *            The do-before callback
	 * @param doAfter
	 *            The do-after callback
	 */
	public TransportRingMoveAnimation(double time, int whichRing, double newHeight, RunnableTileCallback doBefore,
			RunnableTileCallback doAfter) {
		super(time, true, doBefore, doAfter);
		addProperty("ring-height-" + whichRing, 0.0d, newHeight, InterpolationMode.INVSIN);
	}

}
