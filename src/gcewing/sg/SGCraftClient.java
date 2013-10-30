//------------------------------------------------------------------------------------------------
//
//   SG Craft - Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.client.gui.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;

public class SGCraftClient extends BaseModClient {

	public SGCraftClient(BaseMod mod) {
		super(mod);
		debugSound = true;
	}
	
	@Override
	void registerScreens() {
		//System.out.printf("SGCraft: ProxyClient.registerScreens\n");
		addScreen(SGGui.SGBase, SGBaseScreen.class);
		addScreen(SGGui.SGController, SGControllerScreen.class);
	}

	@Override
	void registerRenderers() {
		//System.out.printf("SGCraft: SGCraftClient.registerScreens\n");
		addTileEntityRenderer(SGBaseTE.class, new SGBaseTERenderer());
		addTileEntityRenderer(SGControllerTE.class, new SGControllerTERenderer());
	}

}
