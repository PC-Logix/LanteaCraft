package lc.client.render.animations;

import lc.client.animation.Animation;

public class ChevronMoveAnimation extends Animation {
	
	public ChevronMoveAnimation(double time, int whichChevron, double newPos, double newLight, boolean resample) {
		super(time, resample);
		addProperty("chevron-dist-" + whichChevron, 0.0d, newPos, InterpolationMode.SQUARE);
		addProperty("chevron-light-" + whichChevron, 0.0d, newLight, InterpolationMode.LINEAR);
	}

}
