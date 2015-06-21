package lc.client.render.animations;

import lc.client.animation.Animation;
import lc.common.base.LCTile;
import lc.common.util.game.RunnableTileCallback;

public class ChevronMoveAnimation extends Animation {

	public ChevronMoveAnimation(double time, int whichChevron, double newPos, double newLight, boolean resample) {
		super(time, resample, new RunnableTileCallback() {
			@Override
			public void run(LCTile tile) {
				tile.mixer().replayChannel("lock");
			}
		}, null);
		addProperty("chevron-dist-" + whichChevron, 0.0d, newPos, InterpolationMode.SQUARE);
		addProperty("chevron-light-" + whichChevron, 0.0d, newLight, InterpolationMode.LINEAR);
	}

}
