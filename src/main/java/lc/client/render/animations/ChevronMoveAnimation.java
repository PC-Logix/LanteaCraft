package lc.client.render.animations;

import lc.client.animation.Animation;

public class ChevronMoveAnimation extends Animation {
	
	public ChevronMoveAnimation(int whichChevron, boolean resample) {
		super(10.0d, resample);
		addProperty("chevron-dist-" + whichChevron, 0.0d, 1.0d / 8.0d, InterpolationMode.SQUARE);
		addProperty("chevron-light-" + whichChevron, 0.0d, 0.5d, InterpolationMode.LINEAR);
	}

}
