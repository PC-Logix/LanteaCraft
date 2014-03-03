package pcl.lc.module.integration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;

import net.minecraft.tileentity.TileEntity;
import pcl.lc.LanteaCraft;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import pcl.lc.api.IStargateControllerAccess;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.module.ModuleIntegration;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IMount;
import dan200.computer.api.IPeripheralHandler;

@Agent(modname = "ComputerCraft")
public class ComputerCraftAgent implements IIntegrationAgent {

	private class ComputerCraftProvider implements IPeripheralHandler {
		@Override
		public IHostedPeripheral getPeripheral(TileEntity entity) {
			if (entity instanceof IStargateAccess)
				return new ComputerCraftWrapperPool.StargateAccessWrapper((IStargateAccess) entity);
			else if (entity instanceof IStargateControllerAccess)
				return new ComputerCraftWrapperPool.StargateControllerAccessWrapper((IStargateControllerAccess) entity);
			else if (entity instanceof INaquadahGeneratorAccess)
				return new ComputerCraftWrapperPool.NaquadahGeneratorAccessWrapper((INaquadahGeneratorAccess) entity);
			else
				return null;
		}
	}

	private Class<?> clazz_ComputerCraftAPI;
	private Method registerHandler;
	private ComputerCraftProvider provider;
	private HashMap<String, IMount> mountCache;

	public ComputerCraftAgent() {

	}

	@Override
	public String modName() {
		return "ComputerCraft";
	}

	@Override
	public void init() {
		try {
			clazz_ComputerCraftAPI = Class.forName("dan200.computer.api.ComputerCraftAPI");
			registerHandler = clazz_ComputerCraftAPI.getMethod("registerExternalPeripheral", new Class<?>[] {
					Class.class, IPeripheralHandler.class });
			provider = new ComputerCraftProvider();
			registerHandler.invoke(null, TileEntityStargateBase.class, provider);
			registerHandler.invoke(null, TileEntityStargateController.class, provider);
			registerHandler.invoke(null, TileEntityNaquadahGenerator.class, provider);
		} catch (Throwable t) {
			LanteaCraft.getLogger().log(Level.INFO, "ComputerCraft not found!");
			return;
		}
	}
}
