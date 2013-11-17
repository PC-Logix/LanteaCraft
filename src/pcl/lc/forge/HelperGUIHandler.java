package pcl.lc.forge;

import java.lang.reflect.Constructor;

import pcl.lc.LanteaCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class HelperGUIHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Class<? extends Container> container = LanteaCraft.getProxy().getContainer(ID);
		if (container != null) {
			try {
				TileEntity entity = world.getBlockTileEntity(x, y, z);
				Constructor constr = container.getConstructor(new Class<?>[] { entity.getClass(), EntityPlayer.class });
				Object val = constr.newInstance(entity, player);
				return val;
			} catch (Throwable t) {
				System.err.println("Could not create Container ID " + ID + ", a " + t.getClass().getName()
						+ " exception occurred.");
				t.printStackTrace(System.err);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return LanteaCraft.getProxy().getGuiElement(ID, player, world, x, y, z);
	}

}
