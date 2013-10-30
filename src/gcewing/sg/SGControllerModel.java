package gcewing.sg;

import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
 
@SideOnly(Side.CLIENT)
public class SGControllerModel {
	private IModelCustom model;
     
    public SGControllerModel()
    {
    	model = AdvancedModelLoader.loadModel("/assets/gcewing_sg/models/dhd.obj");
        System.out.println("SGCraft DHD Model Loaded");
    }

    public void render(){
    	model.renderAll();
    }
    
	public void renderPart(String partName) {

		model.renderPart(partName);
	}
}