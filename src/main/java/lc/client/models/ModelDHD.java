package lc.client.models;

import cpw.mods.fml.client.FMLClientHandler;
import lc.api.stargate.StargateType;
import lc.client.models.loader.WavefrontModel;
import lc.common.LCLog;
import lc.common.resource.ResourceAccess;

public class ModelDHD extends WavefrontModel {

	public static ModelDHD $;

	static {
		try {
			$ = new ModelDHD();
		} catch (WavefrontModelException mfe) {
			LCLog.warn("Failed to load ModelDHD, model error", mfe);
		}
	}

	public ModelDHD() throws WavefrontModelException {
		super(ResourceAccess.getNamedResource("models/model_dhd.obj"));
	}

	public void prepareAndRender(StargateType type, boolean state) {
		String typename = (type.getSuffix().length() != 0) ? "dhd_%s_" + type.getSuffix() : "dhd_%s";
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/models/%s_${TEX_QUALITY}.png",
						String.format(typename, (state) ? "on" : "off"))));
		renderAll();
	}

}
