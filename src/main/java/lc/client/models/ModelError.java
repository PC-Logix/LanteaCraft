package lc.client.models;

import net.minecraft.util.ResourceLocation;
import lc.client.models.loader.WavefrontModel;

public class ModelError extends WavefrontModel {

	public ModelError(ResourceLocation resource) throws WavefrontModelException {
		super(resource);
	}

}
