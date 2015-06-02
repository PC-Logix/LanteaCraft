package lc.client.render.animations;

import lc.client.animation.Animation;
import lc.common.util.data.StateMap;
import lc.common.util.math.MathUtils;

public class RingSpinAnimation extends Animation {

	public RingSpinAnimation(Double duration, Double start, Double end, boolean resample) {
		super(duration, resample);
		addProperty("ring-rotation", start, end, InterpolationMode.SIN);
	}

	@Override
	public void resampleProperties(StateMap map) {
		super.resampleProperties(map);
		/** TODO: Compute how we really want to rotate */
	}

}
