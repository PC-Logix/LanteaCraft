package lc.client.openal;

import net.minecraftforge.common.DimensionManager;
import lc.api.audio.streaming.ISoundPosition;
import lc.common.util.math.DimensionPos;
import lc.common.util.math.Vector3;

public class StreamingSoundPosition implements ISoundPosition {

	private DimensionPos position;

	public StreamingSoundPosition(DimensionPos pos) {
		this.position = pos;
	}

	@Override
	public Object getWorldObject() {
		return DimensionManager.getWorld(position.dimension);
	}

	@Override
	public Object getPositionObject() {
		return new Vector3(position.x, position.y, position.z);
	}

}
