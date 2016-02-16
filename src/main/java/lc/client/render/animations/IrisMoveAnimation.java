package lc.client.render.animations;

import lc.client.animation.Animation;

public class IrisMoveAnimation extends Animation {

	public IrisMoveAnimation(Double duration, double result) {
		super(duration, true, null, null);
		addProperty("iris-progress", 0.0d, result, InterpolationMode.SMOOTHSTEP);
	}

}
