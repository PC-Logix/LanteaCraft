package lc.client.render.animations;

import lc.client.animation.Animation;

public class ChevronMoveAnimation extends Animation {
	
	public ChevronMoveAnimation(int whichChevron) {
		super(20.0d);
		addProperty("chevron-dist-" + whichChevron, 0.0d, 1.0d / 16.0d, InterpolationMode.SQUARE);
	}

}
