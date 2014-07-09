package pcl.lc.module.compat;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.core.item.ItemCraftingReagent.ReagentList;
import cpw.mods.fml.common.registry.GameRegistry;

public class UpgradeHelper {

	static class UpgradeMapping {
		private String sourceName;
		private ItemStack destinationStack;
		private boolean sourceIsItem;
		private int inputMetadata;

		public UpgradeMapping(String s, ItemStack d, boolean i) {
			sourceName = s;
			destinationStack = d;
			sourceIsItem = i;
			inputMetadata = -1;
		}

		public UpgradeMapping(String s, ItemStack d, boolean i, int j) {
			sourceName = s;
			destinationStack = d;
			sourceIsItem = i;
			inputMetadata = j;
		}

		public String getSourceName() {
			return sourceName;
		}

		public ItemStack getResultStack() {
			return destinationStack;
		}

		public boolean isItemSource() {
			return sourceIsItem;
		}

		public int getInputMetadata() {
			return inputMetadata;
		}
	}

	private static ArrayList<UpgradeMapping> upgrades = new ArrayList<UpgradeMapping>();

	static {
		upgrades.add(new UpgradeMapping("sgBaseBlock", new ItemStack(ModuleStargates.Blocks.stargateBaseBlock, 1),
				false));
		upgrades.add(new UpgradeMapping("sgRingBlock", new ItemStack(ModuleStargates.Blocks.stargateRingBlock, 1, 0),
				false, 0));
		upgrades.add(new UpgradeMapping("sgRingBlock", new ItemStack(ModuleStargates.Blocks.stargateRingBlock, 1, 1),
				false, 1));
		upgrades.add(new UpgradeMapping("sgControllerBlock", new ItemStack(
				ModuleStargates.Blocks.stargateControllerBlock, 1), false));
		upgrades.add(new UpgradeMapping("naquadahBlock", new ItemStack(ModuleCore.Blocks.lanteaOreAsBlock, 1,
				OreTypes.NAQUADAH.ordinal()), false));
		upgrades.add(new UpgradeMapping("naquadahOre", new ItemStack(ModuleCore.Blocks.lanteaOre, 1, OreTypes.NAQUADAH
				.ordinal()), false));

		upgrades.add(new UpgradeMapping("naquadah", new ItemStack(ModuleCore.Items.lanteaOreItem, 1, OreTypes.NAQUADAH
				.ordinal()), true));
		upgrades.add(new UpgradeMapping("naquadahIngot", new ItemStack(ModuleCore.Items.lanteaOreIngot, 1,
				OreTypes.NAQUADAH.ordinal()), true));
		upgrades.add(new UpgradeMapping("sgCoreCrystal", new ItemStack(ModuleCore.Items.reagentItem, 1,
				ReagentList.CORECRYSTAL.ordinal()), true));
		upgrades.add(new UpgradeMapping("sgControllerCrystal", new ItemStack(ModuleCore.Items.reagentItem, 1,
				ReagentList.CONTROLCRYSTAL.ordinal()), true));
	}

	/**
	 * Attempt to guess if there is an SGCraft (Greg Ewing version) build
	 * currently running. We removed the Utils package in the first refactor, so
	 * this will be able to tell SGCraft from any other build.
	 * 
	 * @return If SGCraft is present.
	 */
	public static boolean detectSGCraftInstall() {
		try {
			Class<?> clazz_a = Class.forName("gcewing.sg.SGCraft");
			Class<?> clazz_b = Class.forName("gcewing.sg.Utils");
			if (clazz_a != null && clazz_b != null)
				return true;
			return false;
		} catch (ClassNotFoundException notfound) {
			return false;
		}
	}

	public void hookSGCraft() {
		try {
			Class<?> clazz = Class.forName("gcewing.sg.SGCraft");

			for (UpgradeMapping mapping : upgrades)
				if (!mapping.isItemSource())
					try {
						Field f1 = clazz.getField(mapping.getSourceName());
						if (f1 != null && (Block) f1.get(null) != null)
							createBlockConversionRecipe(mapping, (Block) f1.get(null));
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(
								Level.WARN,
								String.format("Failed to cast block field %s  to Block typeof, ignoring!",
										mapping.getSourceName()));
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARN,
								String.format("Failed to find block field %s, ignoring!", mapping.getSourceName()));
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARN,
								String.format("Could not access block field %s, ignoring!", mapping.getSourceName()));
					}
				else
					try {
						Field f1 = clazz.getField(mapping.getSourceName());
						if (f1 != null && (Item) f1.get(null) != null)
							createItemConversionRecipe(mapping, (Item) f1.get(null));
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(
								Level.WARN,
								String.format("Failed to cast item field %s to Item typeof, ignoring!",
										mapping.getSourceName()));
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARN,
								String.format("Failed to find item field %s, ignoring!", mapping.getSourceName()));
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARN,
								String.format("Could not access item field %s, ignoring!", mapping.getSourceName()));
					}
		} catch (ClassNotFoundException notfound) {
			LanteaCraft.getLogger().log(Level.WARN, "Could not locate a class required for upgrade!", notfound);
		}
	}

	public void createBlockConversionRecipe(UpgradeMapping mapping, Block foreign) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				String.format("Adding block conversion {foreign: %s; local: %s}.", foreign.getClass()
						.getCanonicalName(), mapping.getResultStack().toString()));
		if (mapping.getInputMetadata() != -1)
			GameRegistry.addShapelessRecipe(mapping.getResultStack(),
					new ItemStack(foreign, 1, mapping.getInputMetadata()));
		else
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1));
	}

	public void createItemConversionRecipe(UpgradeMapping mapping, Item foreign) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				String.format("Adding item conversion {foreign: %s; local: %s}.",
						foreign.getClass().getCanonicalName(), mapping.getResultStack().toString()));
		if (mapping.getInputMetadata() != -1)
			GameRegistry.addShapelessRecipe(mapping.getResultStack(),
					new ItemStack(foreign, 1, mapping.getInputMetadata()));
		else
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1));
	}
}
