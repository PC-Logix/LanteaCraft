package lc.client.models;

import lc.client.models.loader.WavefrontModel;
import lc.common.LCLog;
import lc.common.resource.ResourceAccess;

public class ModelTransportRing extends WavefrontModel {

	public static ModelTransportRing $;

	static {
		try {
			$ = new ModelTransportRing();
		} catch (WavefrontModelException mfe) {
			LCLog.warn("Failed to load ModelTransportRing, model error", mfe);
		}
	}

	public ModelTransportRing() throws WavefrontModelException {
		super(ResourceAccess.getNamedResource("models/model_transport_ring.obj"));
	}

	public void prepareAndRender() {
		renderAll();
	}

}
