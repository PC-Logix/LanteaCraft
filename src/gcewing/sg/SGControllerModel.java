package gcewing.sg;

import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
 
@SideOnly(Side.CLIENT)
public class SGControllerModel {
	public static SGControllerModel INSTANCE = null;
	public static final String MODEL_FILE = "/assets/gcewing_sg/models/dhd.obj";
	
	private IModelCustom SGControllerModel;
     
    public SGControllerModel()
    {
    	SGControllerModel = AdvancedModelLoader.loadModel(MODEL_FILE);
        System.out.println("SGCraft DHD Model Loaded");
    }

    public void render(){
    	SGControllerModel.renderAll();
    }
    
    public void renderOnly(String groupName){
    	SGControllerModel.renderOnly(groupName);
    }
    
    public void renderAll(){
    	SGControllerModel.renderAll();
    }
    
	public void renderPart(String partName) {
		SGControllerModel.renderPart(partName);
	}

	public void renderAllExcept(String group) {
		SGControllerModel.renderAllExcept(group);
	}
	
	public static void loadSGControllerModel() {
		System.out.println("loadSGControllerModel");
		INSTANCE = new SGControllerModel();
	}

}