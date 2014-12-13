package pcl.lc.module.integration;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.core.item.ItemCraftingReagent.ReagentList;
import pcl.lc.module.integration.computercraft.BlockComputerCraftConnector;
import pcl.lc.module.integration.computercraft.TileEntityComputerCraftConnector;
import pcl.lc.util.RegistrationHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

@Agent(modname = "ComputerCraft")
public class ComputerCraftAgent implements IIntegrationAgent {

	private class ComputerCraftProvider implements IPeripheralProvider {
		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity entity = world.getTileEntity(x, y, z);
			if (entity instanceof TileEntityComputerCraftConnector)
				return (TileEntityComputerCraftConnector) entity;
			return null;
		}
	}

	private Class<?> clazz_ComputerCraftAPI;
	private Method registerHandler;
	private ComputerCraftProvider provider;

	private BlockComputerCraftConnector block_connector;

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
		}
		block_connector = RegistrationHelper.registerBlock(BlockComputerCraftConnector.class,
				"integration.computercraft");
		GameRegistry.registerTileEntity(TileEntityComputerCraftConnector.class, "tileEntityComputercraftAdapter");
		RegistrationHelper.newRecipe(new ItemStack(block_connector, 1), "OOO", "OcO", "OrO", 'O',
				net.minecraft.init.Blocks.obsidian, 'r', net.minecraft.init.Items.redstone, 'c',
				ReagentList.CONTROLCRYSTAL.ordinal());
	}

	@Override
	public void postInit() {

	}

}
