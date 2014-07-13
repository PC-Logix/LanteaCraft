package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.base.worldgen.NaquadahOreWorldGen;
import pcl.lc.core.ModuleManager;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.core.item.ItemCraftingReagent.ReagentList;
import pcl.lc.util.RegistrationHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModuleWorldGenerator implements IModule {

	private NaquadahOreWorldGen naquadahOreGenerator;

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE, Module.STARGATE);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.TERRAIN_GEN_BUS.register(LanteaCraft.getInstance());

		LanteaCraft.getLogger().log(Level.DEBUG, "Registering LanteaCraft random drop items...");
		String[] categories = { ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.PYRAMID_DESERT_CHEST,
				ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH };
		if (Module.STARGATE.isLoaded()) {
			RegistrationHelper.addRandomChestItem(new ItemStack(ModuleStargates.Blocks.stargateBaseBlock), 1, 1, 2,
					categories);
			RegistrationHelper.addRandomChestItem(new ItemStack(ModuleStargates.Blocks.stargateControllerBlock), 1, 1,
					1, categories);
			RegistrationHelper.addRandomChestItem(new ItemStack(ModuleStargates.Blocks.stargateRingBlock, 1, 0), 1, 3,
					8, categories);
			RegistrationHelper.addRandomChestItem(new ItemStack(ModuleStargates.Blocks.stargateRingBlock, 1, 1), 1, 3,
					7, categories);
			RegistrationHelper.addRandomChestItem(new ItemStack(ModuleCore.Items.reagentItem, 1,
					ReagentList.CORECRYSTAL.ordinal()), 1, 1, 2, categories);
			RegistrationHelper.addRandomChestItem(new ItemStack(ModuleCore.Items.reagentItem, 1,
					ReagentList.CONTROLCRYSTAL.ordinal()), 1, 1, 1, categories);
		}

		LanteaCraft.getLogger().log(Level.DEBUG, "Registering LanteaCraft NaquadahOre generator...");
		naquadahOreGenerator = new NaquadahOreWorldGen();
		naquadahOreGenerator.configure(ModuleManager.getConfig(this));
		GameRegistry.registerWorldGenerator(naquadahOreGenerator, 0);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

	public NaquadahOreWorldGen getNaquadahOreGenerator() {
		return naquadahOreGenerator;
	}

}
