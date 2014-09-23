package lc.api.rendering;

public interface IRenderInfo {

	public IBlockRenderInfo block();

	public ITileRenderInfo tile();

	public IEntityRenderInfo entity();
}
