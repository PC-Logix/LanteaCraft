package pcl.lc.guis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import pcl.common.base.GenericContainerGUI;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerNaquadahGenerator;
import pcl.lc.containers.ContainerStargateBase;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;

public class ScreenNaquadahGenerator extends GenericContainerGUI {

	private static final int guiWidth = 600;
	private static final int guiHeight = 216;

	private final TileEntityNaquadahGenerator tileEntity;

	public ScreenNaquadahGenerator(TileEntityNaquadahGenerator te, EntityPlayer player) {
		super(new ContainerNaquadahGenerator(te, player), guiWidth, guiHeight);
		this.tileEntity = te;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawBackgroundLayer() {
		bindTexture(LanteaCraft.getInstance().getResource("textures/gui/naquadah_generator.png"), 1024, 256);
		drawTexturedRectUV(0, 0, 1024, 256, 0, 0, 1, 1);

	}

	@Override
	protected void drawForegroundLayer() {
		// TODO Auto-generated method stub

	}

}
