package lc.client;

import lc.core.ResourceAccess;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileStargateBaseRenderer extends TileEntitySpecialRenderer {

	public final ResourceLocation texture;

	public TileStargateBaseRenderer() {
		this.texture = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_${TEX_QUALITY}.png"));
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float t) {
	}

	public void bind(ResourceLocation resource) {
		bindTexture(resource);
	}
}
