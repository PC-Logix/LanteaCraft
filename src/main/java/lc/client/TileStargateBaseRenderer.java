package lc.client;

import lc.api.defs.IDefinitionReference;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderHook;
import lc.common.base.LCTileRenderer;
import lc.common.impl.registry.DefinitionReference;
import lc.core.ResourceAccess;
import net.minecraft.util.ResourceLocation;

public class TileStargateBaseRenderer extends LCTileRenderer {

	public final ResourceLocation texture;

	public TileStargateBaseRenderer() {
		this.texture = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_${TEX_QUALITY}.png"));
	}

	@Override
	public LCTileRenderer getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderHook renderer, double x, double y, double z,
			float partialTickTime) {
		// TODO Auto-generated method stub
		return false;
	}
}
