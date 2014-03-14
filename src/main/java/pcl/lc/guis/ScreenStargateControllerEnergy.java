package pcl.lc.guis;

import net.minecraft.entity.player.EntityPlayer;
import pcl.lc.containers.ContainerStargateController;
import pcl.lc.tileentity.TileEntityStargateController;

public class ScreenStargateControllerEnergy extends GenericGlyphGUI {

	public ScreenStargateControllerEnergy(TileEntityStargateController controller, EntityPlayer actor) {
		super(new ContainerStargateController(controller, actor), 800, 600);
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

}
