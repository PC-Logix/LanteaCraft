package lc.client.render.animations;

import lc.client.animation.Animation;

public class RingSpinAnimation extends Animation {

	public RingSpinAnimation(Double duration, Double start, Double end) {
		super(duration);
		addProperty("ring-rotation", start, end, InterpolationMode.CUBED);
	}

}
