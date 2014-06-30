package pcl.lc.module.stargate.render;

import pcl.lc.module.stargate.tile.TileEntityStargateBase;

public interface IStargateRenderer {

	public abstract void renderStargateAt(TileEntityStargateBaseRenderer renderer, TileEntityStargateBase te, double x,
			double y, double z, float t);

}
