package pcl.lc.module.stargate.render;

import pcl.lc.module.stargate.tile.TileStargateBase;

public interface IStargateRenderer {

	public abstract void renderStargateAt(TileStargateBaseRenderer renderer, TileStargateBase te, double x,
			double y, double z, float t);

}
