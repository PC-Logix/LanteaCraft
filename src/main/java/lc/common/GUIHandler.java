package lc.common;

import java.lang.reflect.Constructor;

import lc.LCRuntime;
import lc.api.components.IInterfaceRegistry;
import lc.api.defs.IInterfaceDefinition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;

public class GUIHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		LCLog.debug("Handling GUI request: id %s", id);
		IInterfaceRegistry registry = LCRuntime.runtime.registries().interfaces();
		IInterfaceDefinition def = registry.getDefinition(id);
		LCLog.doAssert(def != null, "Can't handle server GUI request for element ID %s.", id);
		try {
			String containerClass = def.getContainerClass();
			LCLog.debug("Creating container %s", containerClass);
			TileEntity tile = world.getTileEntity(x, y, z);
			Class<?> container = Class.forName(containerClass);
			Constructor<?> constr = container.getConstructor(new Class<?>[] { tile.getClass(), EntityPlayer.class });
			return constr.newInstance(tile, player);
		} catch (Throwable t) {
			LCLog.warn("Failed to create container object!", t);
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Side what = FMLCommonHandler.instance().getEffectiveSide();
		if (what == Side.SERVER) {
			return getServerGuiElement(id, player, world, x, y, z);
		} else {
			LCLog.debug("Handling GUI request: id %s", id);
			IInterfaceRegistry registry = LCRuntime.runtime.registries().interfaces();
			IInterfaceDefinition def = registry.getDefinition(id);
			LCLog.doAssert(def != null, "Can't handle client GUI request for element ID %s.", id);
			try {
				String guiClass = def.getGUIClass();
				LCLog.debug("Creating GUI %s", guiClass);
				TileEntity tile = world.getTileEntity(x, y, z);
				Class<?> gui = Class.forName(guiClass);
				Constructor<?> constr = gui.getConstructor(new Class<?>[] { tile.getClass(), EntityPlayer.class });
				return constr.newInstance(tile, player);
			} catch (Throwable t) {
				LCLog.warn("Failed to create GUI object!", t);
				return null;
			}
		}
	}
}
