package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import org.apache.logging.log4j.Level;

import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.critters.entity.EntityReplicator;
import pcl.lc.module.critters.entity.EntityTokra;
import pcl.lc.module.critters.render.EntityReplicatorRenderer;
import pcl.lc.module.critters.render.EntityTokraRenderer;
import pcl.lc.worldgen.TradeHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ModuleCritters implements IModule {

	public static class Entities {
		public static EntityTokra entityTokra;
		public static EntityReplicator entityReplicator;
	}

	public static class Render {
		public static EntityTokraRenderer entityTokraRenderer;
		public static EntityReplicatorRenderer entityReplicatorRenderer;
	}

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return null;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FMLInitializationEvent event) {
		// TODO: Disabled for 1.7
		// EntityRegistry.registerModEntity(EntityTokra.class, "tokra", 0,
		// LanteaCraft.getInstance(), 80, 1, true);
		// EntityRegistry.registerModEntity(EntityReplicator.class,
		// "replicator", 1, LanteaCraft.getInstance(), 80, 1,
		// true);

		LanteaCraft.getLogger().log(Level.DEBUG, "Registering LanteaCraft Tokra villagers...");
		LanteaCraft.getProxy().tokraVillagerID = LanteaCraft.getProxy().addVillager(
				LanteaCraft.getProxy().getConfig().getVillager("tokra"), "tokra",
				LanteaCraft.getResource("textures/skins/tokra.png"));
		RegistrationHelper.addTradeHandler(LanteaCraft.getProxy().tokraVillagerID, new TradeHandler());

		if (event.getSide() == Side.CLIENT) {
			Render.entityTokraRenderer = new EntityTokraRenderer();
			RenderingRegistry.registerEntityRenderingHandler(EntityTokra.class, Render.entityTokraRenderer);

			Render.entityReplicatorRenderer = new EntityReplicatorRenderer();
			RenderingRegistry.registerEntityRenderingHandler(EntityReplicator.class, Render.entityReplicatorRenderer);
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
