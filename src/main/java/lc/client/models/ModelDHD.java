package lc.client.models;

import lc.ResourceAccess;
import lc.client.models.loader.WavefrontModel;

public class ModelDHD extends WavefrontModel {

	public ModelDHD() throws WavefrontModelException {
		super(ResourceAccess.getNamedResource("models/model_dhd.obj"));
	}

}
