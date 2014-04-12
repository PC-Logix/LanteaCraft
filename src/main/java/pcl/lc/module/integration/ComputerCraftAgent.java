package pcl.lc.module.integration;

import java.lang.reflect.Method;
import java.util.logging.Level;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.module.integration.computercraft.TileEntityComputerCraftConnector;

public class ComputerCraftAgent implements IIntegrationAgent {

	private class ComputerCraftProvider implements IPeripheralProvider {
		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity instanceof TileEntityComputerCraftConnector)
				return (TileEntityComputerCraftConnector) entity;
			return null;
		}
	}

	private Class<?> clazz_ComputerCraftAPI;
	private Method registerHandler;
	private ComputerCraftProvider provider;

	public ComputerCraftAgent() {

	}

	@Override
	public String modName() {
		return "ComputerCraft";
	}

	@Override
	public void init() {
		try {
			clazz_ComputerCraftAPI = Class.forName("dan200.computercraft.api.ComputerCraftAPI");
			registerHandler = clazz_ComputerCraftAPI.getMethod("registerPeripheralProvider",
					new Class<?>[] { IPeripheralProvider.class });
			provider = new ComputerCraftProvider();
			registerHandler.invoke(null, provider);
		} catch (Throwable t) {
			LanteaCraft.getLogger().log(Level.INFO, "ComputerCraft not found!");
			return;
		}
	}

}
