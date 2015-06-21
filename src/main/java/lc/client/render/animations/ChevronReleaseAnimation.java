package lc.client.render.animations;

import lc.client.animation.Animation;
import lc.client.animation.Animation.InterpolationMode;

public class ChevronReleaseAnimation extends Animation {

	public ChevronReleaseAnimation(int range, boolean resample) {
		super(10.0d, resample, null, null);
		for (int i = 0; i <= range; i++) {
			addProperty("chevron-dist-" + i, 0.0d, 0.0d, InterpolationMode.SQUARE);
			addProperty("chevron-light-" + i, 0.0d, 0.0d, InterpolationMode.LINEAR);
		}
	}

}
