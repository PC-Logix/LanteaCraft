package pcl.lc.render.models;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RingPlatformBaseModel {
	private IModelCustom model;

	public RingPlatformBaseModel(ResourceLocation location) {
		model = AdvancedModelLoader.loadModel(location);
	}

	public void render() {
		model.renderAll();
	}

	public void renderOnly(String groupName) {
		model.renderOnly(groupName);
	}

	public void renderAll() {
		model.renderAll();
	}

	public void renderPart(String partName) {
		model.renderPart(partName);
	}

	public void renderAllExcept(String group) {
		model.renderAllExcept(group);
	}
}