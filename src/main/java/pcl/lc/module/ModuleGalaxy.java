package pcl.lc.module;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ConfigList;
import pcl.lc.cfg.ConfigNode;
import pcl.lc.cfg.DOMHelper;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.core.ModuleManager;
import pcl.lc.core.ResourceAccess;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.galaxy.IDimension;
import pcl.lc.module.galaxy.MapGenFeatureStructureStart;
import pcl.lc.module.galaxy.abydos.AbydosDimension;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleGalaxy implements IModule {

	// TODO: Change this to a config option
	public static final int __TMP_ABYDOS_IDX = 5;
	// TODO: Move the Biome ID somewhere sensible (is it even needed??)
	public static final int __TMP_ABYDOX_BMX = 1;

	public static enum Dimension {
		ABYDOS(AbydosDimension.class, 3, 3);

		public final Class<?> registrar;
		public final int defaultDimensionId;
		public final int defaultProviderId;

		public IDimension dimensionInstance;

		private Dimension(Class<?> clazz, int defaultDimensionIdx, int defaultProviderIdx) {
			registrar = clazz;
			defaultDimensionId = defaultDimensionIdx;
			defaultProviderId = defaultProviderIdx;
		}
	}

	public static class DimensionConfig extends ConfigList {
		private Dimension dimension;

		public DimensionConfig(String name, ConfigNode parent) {
			super(name, parent);
		}

		public void setDimension(Dimension dimension) {
			this.dimension = dimension;
		}

		public int getProviderId() {
			if (!parameters().containsKey("providerId")) {
				parameters().put("providerId", dimension.defaultProviderId);
				modify();
			}
			return Integer.parseInt(parameters().get("providerId").toString());
		}

		public int getDimensionId() {
			if (!parameters().containsKey("dimensionId")) {
				parameters().put("dimensionId", dimension.defaultDimensionId);
				modify();
			}
			return Integer.parseInt(parameters().get("dimensionId").toString());
		}
	}

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE, Module.CRITTERS, Module.DECOR, Module.STARGATE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE, Module.CRITTERS, Module.DECOR, Module.STARGATE);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ModuleConfig config = ModuleManager.getConfig(this);

		ArrayList<ConfigNode> dimensionMap = ConfigHelper.findAllConfigByClass(config, "Dimension");
		HashMap<String, DimensionConfig> dimensionConfigs = new HashMap<String, DimensionConfig>();
		for (ConfigNode node : dimensionMap)
			if (node instanceof ConfigList) {
				DimensionConfig setup = (DimensionConfig) node;
				dimensionConfigs.put(setup.parameters().get("name").toString(), setup);
			}

		MapGenStructureIO.registerStructure(MapGenFeatureStructureStart.class,
				ResourceAccess.formatResourceName("${ASSET_KEY}:LanteaCraft"));

		for (Dimension dimension : Dimension.values()) {
			DimensionConfig dimensionSettings = dimensionConfigs.get(dimension.name());
			if (dimensionSettings == null) {
				dimensionSettings = new DimensionConfig("Dimension", config);
				dimensionSettings.parameters().put("name", dimension.name());
				dimensionSettings.parameters().put("enabled", "true");
				config.children().add(dimensionSettings);
				dimensionSettings.modify();
			}
			if (DOMHelper.popBoolean(dimensionSettings.parameters().get("enabled").toString(), false)) {
				try {
					dimensionSettings.setDimension(dimension);
					Constructor<?> dimensionCtr = dimension.registrar
							.getConstructor(new Class<?>[] { DimensionConfig.class });
					dimension.dimensionInstance = (IDimension) dimensionCtr.newInstance(dimensionSettings);
				} catch (Throwable t) {
					LanteaCraft.getLogger().log(Level.WARN, "Error when setting up dimension.", t);
				}
			}
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
