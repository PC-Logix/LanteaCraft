package lc.client.render.animations;

import lc.client.animation.Animation;

public class ChevronReleaseAnimation extends Animation {

	public ChevronReleaseAnimation(int range, boolean resample) {
		super(5.0d, resample);
		for (int i = 0; i <= range; i++) {
			addProperty("chevron-dist-" + i, 1.0d / 16.0d, 0.0d, InterpolationMode.SQUARE);
		}
	}

}
