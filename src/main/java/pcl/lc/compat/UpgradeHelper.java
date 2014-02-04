package pcl.lc.compat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pcl.lc.LanteaCraft;
import pcl.lc.core.OreTypes;
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
		upgrades.add(new UpgradeMapping("sgBaseBlock", new ItemStack(LanteaCraft.Blocks.stargateBaseBlock, 1), false));
		upgrades.add(new UpgradeMapping("sgRingBlock", new ItemStack(LanteaCraft.Blocks.stargateRingBlock, 1, 0), false, 0));
		upgrades.add(new UpgradeMapping("sgRingBlock", new ItemStack(LanteaCraft.Blocks.stargateRingBlock, 1, 1), false, 1));
		upgrades.add(new UpgradeMapping("sgControllerBlock", new ItemStack(LanteaCraft.Blocks.stargateControllerBlock, 1), false));
		upgrades.add(new UpgradeMapping("naquadahBlock",
				new ItemStack(LanteaCraft.Blocks.lanteaOreAsBlock, 1, OreTypes.NAQUADAH.ordinal()), false));
		upgrades.add(new UpgradeMapping("naquadahOre", new ItemStack(LanteaCraft.Blocks.lanteaOre, 1, OreTypes.NAQUADAH.ordinal()), false));

		upgrades.add(new UpgradeMapping("naquadah", new ItemStack(LanteaCraft.Items.lanteaOreItem, 1, OreTypes.NAQUADAH.ordinal()), true));
		upgrades.add(new UpgradeMapping("naquadahIngot", new ItemStack(LanteaCraft.Items.lanteaOreIngot, 1, OreTypes.NAQUADAH.ordinal()),
				true));
		upgrades.add(new UpgradeMapping("sgCoreCrystal", new ItemStack(LanteaCraft.Items.coreCrystal, 1), true));
		upgrades.add(new UpgradeMapping("sgControllerCrystal", new ItemStack(LanteaCraft.Items.controllerCrystal, 1), true));
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

	/**
	 * Attempt to guess if there is an SGCraft-Reloaded (PCL) build currently
	 * running. We added GCESGCompatHelper in the first few releases to maintain
	 * backwards compat, then removed it (in favor of this file) in LC RC1.
	 * 
	 * @return If SGCraft-Reloaded is present.
	 */
	public static boolean detectSGCraftReloadedInstall() {
		try {
			Class<?> clazz = Class.forName("gcewing.sg.GCESGCompatHelper");
			if (clazz != null)
				return true;
			return false;
		} catch (ClassNotFoundException notfound) {
			return false;
		}
	}

	public void hookSGCraft() {
		try {
			Class<?> clazz = Class.forName("gcewing.sg.SGCraft");

			for (UpgradeMapping mapping : upgrades) {
				if (!mapping.isItemSource())
					try {
						Field f1 = clazz.getField(mapping.getSourceName());
						if (f1 != null && (Block) f1.get(null) != null)
							createBlockConversionRecipe(mapping, (Block) f1.get(null));
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to cast block field %s  to Block typeof, ignoring!", mapping.getSourceName()));
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to find block field %s, ignoring!", mapping.getSourceName()));
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Could not access block field %s, ignoring!", mapping.getSourceName()));
					}
				else
					try {
						Field f1 = clazz.getField(mapping.getSourceName());
						if (f1 != null && (Item) f1.get(null) != null)
							createItemConversionRecipe(mapping, (Item) f1.get(null));
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to cast item field %s to Item typeof, ignoring!", mapping.getSourceName()));
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to find item field %s, ignoring!", mapping.getSourceName()));
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Could not access item field %s, ignoring!", mapping.getSourceName()));
					}
			}
		} catch (ClassNotFoundException notfound) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not locate a class required for upgrade!", notfound);
		}
	}

	public void hookSGCraftReloaded() {
		try {
			Class<?> clazz_blocks = Class.forName("gcewing.sg.SGCraft$Blocks");
			Class<?> clazz_items = Class.forName("gcewing.sg.SGCraft$Items");

			for (UpgradeMapping mapping : upgrades) {
				if (!mapping.isItemSource())
					try {
						Field f1 = clazz_blocks.getField(mapping.getSourceName());
						if (f1 != null && (Block) f1.get(null) != null)
							createBlockConversionRecipe(mapping, (Block) f1.get(null));
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to cast block field %s  to Block typeof, ignoring!", mapping.getSourceName()));
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to find block field %s, ignoring!", mapping.getSourceName()));
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Could not access block field %s, ignoring!", mapping.getSourceName()));
					}
				else
					try {
						Field f1 = clazz_items.getField(mapping.getSourceName());
						if (f1 != null && (Item) f1.get(null) != null)
							createItemConversionRecipe(mapping, (Item) f1.get(null));
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to cast item field %s to Item typeof, ignoring!", mapping.getSourceName()));
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Failed to find item field %s, ignoring!", mapping.getSourceName()));
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								String.format("Could not access item field %s, ignoring!", mapping.getSourceName()));
					}
			}

		} catch (ClassNotFoundException notfound) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not locate a class required for upgrade!", notfound);
		}
	}

	public void createBlockConversionRecipe(UpgradeMapping mapping, Block foreign) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				String.format("Adding block conversion {foreign: %s; local: %s}.", foreign.getClass().getCanonicalName(), mapping
						.getResultStack().toString()));
		if (mapping.getInputMetadata() != -1)
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1, mapping.getInputMetadata()));
		else
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1));
	}

	public void createItemConversionRecipe(UpgradeMapping mapping, Item foreign) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				String.format("Adding item conversion {foreign: %s; local: %s}.", foreign.getClass().getCanonicalName(), mapping
						.getResultStack().toString()));
		if (mapping.getInputMetadata() != -1)
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1, mapping.getInputMetadata()));
		else
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1));
	}
}
