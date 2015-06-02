package lc.client.render.animations;

import lc.client.animation.Animation;

public class ChevronMoveAnimation extends Animation {
	
	public ChevronMoveAnimation(double time, int whichChevron, boolean resample) {
		super(time, resample);
		addProperty("chevron-dist-" + whichChevron, 0.0d, 1.0d / 8.0d, InterpolationMode.SQUARE);
		addProperty("chevron-light-" + whichChevron, 0.0d, 0.5d, InterpolationMode.LINEAR);
	}

}
