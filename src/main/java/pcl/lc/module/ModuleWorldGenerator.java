package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.worldgen.FeatureUnderDesertPyramid;
import pcl.lc.worldgen.NaquadahOreWorldGen;

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
	public void preInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		MinecraftForge.TERRAIN_GEN_BUS.register(LanteaCraft.getInstance());
		try {
			if (new CallableMinecraftVersion(null).minecraftVersion().equals("1.6.4"))
				MapGenStructureIO.func_143031_a(FeatureUnderDesertPyramid.class, "LanteaCraft:DesertPyramid");
		} catch (Throwable e) {
			LanteaCraft.getLogger().log(Level.FINE, "Could not register structure type LanteaCraft:DesertPyramid", e);
		}

		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft random drop items...");
		String[] categories = { ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
				ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH };
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateBaseBlock), 1, 1, 2, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateControllerBlock), 1, 1, 1, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateRingBlock, 1, 0), 1, 3, 8, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateRingBlock, 1, 1), 1, 3, 7, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Items.coreCrystal, 1, 0), 1, 1, 2, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Items.controllerCrystal, 1, 0), 1, 1, 1, categories);

		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft NaquadahOre generator...");
		naquadahOreGenerator = new NaquadahOreWorldGen();
		GameRegistry.registerWorldGenerator(naquadahOreGenerator);

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}
	
	public NaquadahOreWorldGen getNaquadahOreGenerator() {
		return naquadahOreGenerator;
	}

}
