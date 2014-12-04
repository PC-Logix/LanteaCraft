package lc.client.models;

import lc.client.models.loader.WavefrontModel;
import net.minecraft.util.ResourceLocation;

/**
 * Error model implementation
 *
 * @author AfterLifeLochie
 *
 */
public class ModelError extends WavefrontModel {

	/**
	 * Default constructor
	 *
	 * @param resource
	 *            The ResourceLocation of the model
	 * @throws WavefrontModelException
	 *             Any model loading exception
	 */
	public ModelError(ResourceLocation resource) throws WavefrontModelException {
		super(resource);
	}

}
