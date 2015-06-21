package lc.client.render.animations;

import lc.client.animation.Animation;
import lc.common.base.LCTile;
import lc.common.util.data.StateMap;
import lc.common.util.game.RunnableTileCallback;

public class RingSpinAnimation extends Animation {

	public RingSpinAnimation(Double duration, Double start, Double end, boolean resample) {
		super(duration, resample, new RunnableTileCallback() {
			@Override
			public void run(LCTile tile) {
				tile.mixer().replayChannel("spin");
			}
		}, new RunnableTileCallback() {
			@Override
			public void run(LCTile tile) {
				tile.mixer().stopChannel("spin");
			}
		});
		addProperty("ring-rotation", start, end, InterpolationMode.SIN);
	}

	@Override
	public void resampleProperties(StateMap map) {
		super.resampleProperties(map);
		/** TODO: Compute how we really want to rotate */
	}

}
