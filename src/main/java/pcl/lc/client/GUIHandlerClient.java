package pcl.lc.client;

import java.lang.reflect.Constructor;

import org.apache.logging.log4j.Level;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.core.GUIHandler;

public class GUIHandlerClient extends GUIHandler {

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		LanteaCraft.getLogger().log(Level.DEBUG, String.format("Initializing GUI with ordinal %s.", ID));
		Class<? extends GuiScreen> gui = RegistrationHelper.getRegisteredGui(ID);
		if (gui != null)
			try {
				LanteaCraft.getLogger().log(Level.DEBUG, String.format("Initializing GUI of class %s.", gui.getName()));
				TileEntity entity = world.getTileEntity(x, y, z);
				Constructor<?> constr = gui.getConstructor(new Class<?>[] { entity.getClass(), EntityPlayer.class });
				Object val = constr.newInstance(entity, player);
				return val;
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.FATAL, String.format("Failed to create GUI with ID %s", ID), t);
			}
		return null;
	}
}
