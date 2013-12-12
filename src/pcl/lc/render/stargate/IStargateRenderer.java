package pcl.lc.render.stargate;

import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.tileentity.TileEntityStargateBase;

public interface IStargateRenderer {
	
	public abstract void renderStargateAt(TileEntityStargateBaseRenderer renderer, TileEntityStargateBase te, double x, double y, double z, float t);

}
