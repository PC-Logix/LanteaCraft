package lc.client.render.animations;

import lc.client.animation.Animation;

public class RingSpinAnimation extends Animation {

	public RingSpinAnimation(Double duration, Double start, Double end, boolean resample) {
		super(duration, resample);
		addProperty("ring-rotation", start, end, InterpolationMode.CUBED);
	}

}
