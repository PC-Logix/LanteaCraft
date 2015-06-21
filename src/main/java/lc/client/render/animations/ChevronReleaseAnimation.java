package lc.client.render.animations;

import lc.client.animation.Animation;

/**
 * Chevron release animation implementation.
 * 
 * @author AfterLifeLochie
 *
 */
public class ChevronReleaseAnimation extends Animation {

	/**
	 * Release all active chevrons
	 * 
	 * @param range
	 *            The range of chevrons to release (0 -> n)
	 * @param resample
	 *            If the properties need to be resampled before animating
	 */
	public ChevronReleaseAnimation(int range, boolean resample) {
		super(10.0d, resample, null, null);
		for (int i = 0; i <= range; i++) {
			addProperty("chevron-dist-" + i, 0.0d, 0.0d, InterpolationMode.SQUARE);
			addProperty("chevron-light-" + i, 0.0d, 0.0d, InterpolationMode.LINEAR);
		}
	}

}
