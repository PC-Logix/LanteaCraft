package pcl.common.helpers;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		LanteaCraft.getLogger().log(Level.FINE, String.format("Initializing Container with ordinal %s.", ID));
		Class<? extends Container> container = LanteaCraft.getProxy().getContainer(ID);
		if (container != null)
			try {
				LanteaCraft.getLogger().log(Level.FINE,
						String.format("Initializing Container of class %s.", container.getName()));
				TileEntity entity = world.getBlockTileEntity(x, y, z);
				Constructor<?> constr = container
						.getConstructor(new Class<?>[] { entity.getClass(), EntityPlayer.class });
				Object val = constr.newInstance(entity, player);
				return val;
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARNING, "Failed to create GUI component!", t);
			}
		LanteaCraft.getLogger().log(Level.WARNING, String.format("Could not find component with ID %s!", ID));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return LanteaCraft.getProxy().getGuiElement(ID, player, world, x, y, z);
	}

}
