package pcl.lc.compat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pcl.lc.LanteaCraft;
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
		upgrades.add(new UpgradeMapping("sgRingBlock", new ItemStack(LanteaCraft.Blocks.stargateRingBlock, 1, 0),
				false, 0));
		upgrades.add(new UpgradeMapping("sgRingBlock", new ItemStack(LanteaCraft.Blocks.stargateRingBlock, 1, 1),
				false, 1));
		upgrades.add(new UpgradeMapping("sgControllerBlock", new ItemStack(LanteaCraft.Blocks.stargateControllerBlock,
				1), false));
		upgrades.add(new UpgradeMapping("naquadahBlock", new ItemStack(LanteaCraft.Blocks.lanteaOreAsBlock, 1), false));
		upgrades.add(new UpgradeMapping("naquadahOre", new ItemStack(LanteaCraft.Blocks.lanteaOre, 1), false));

		upgrades.add(new UpgradeMapping("naquadah", new ItemStack(LanteaCraft.Items.lanteaOreItem, 1), true));
		upgrades.add(new UpgradeMapping("naquadahIngot", new ItemStack(LanteaCraft.Items.lanteaOreIngot, 1), true));
		upgrades.add(new UpgradeMapping("sgCoreCrystal", new ItemStack(LanteaCraft.Items.coreCrystal, 1), true));
		upgrades.add(new UpgradeMapping("sgControllerCrystal", new ItemStack(LanteaCraft.Items.controllerCrystal, 1), true));
	}

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
						if (f1 != null) {
							Block foreignBlock = (Block) f1.get(null);
							if (foreignBlock != null)
								createBlockConversionRecipe(mapping, foreignBlock);
						}
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger()
								.log(Level.WARNING,
										"Failed to cast block field " + mapping.getSourceName()
												+ " to Block typeof, ignoring!");
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Failed to find block field " + mapping.getSourceName() + ", ignoring!");
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Could not access block field " + mapping.getSourceName() + ", ignoring!");
					}

				if (mapping.isItemSource())
					try {
						Field f1 = clazz.getField(mapping.getSourceName());
						if (f1 != null) {
							Item foreignItem = (Item) f1.get(null);
							if (foreignItem != null)
								createItemConversionRecipe(mapping, foreignItem);
						}
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Failed to cast item field " + mapping.getSourceName() + " to Item typeof, ignoring!");
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Failed to find item field " + mapping.getSourceName() + ", ignoring!");
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Could not access item field " + mapping.getSourceName() + ", ignoring!");
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
						if (f1 != null) {
							Block foreignBlock = (Block) f1.get(null);
							if (foreignBlock != null)
								createBlockConversionRecipe(mapping, foreignBlock);
						}
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger()
								.log(Level.WARNING,
										"Failed to cast block field " + mapping.getSourceName()
												+ " to Block typeof, ignoring!");
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Failed to find block field " + mapping.getSourceName() + ", ignoring!");
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Could not access block field " + mapping.getSourceName() + ", ignoring!");
					}

				if (mapping.isItemSource())
					try {
						Field f1 = clazz_items.getField(mapping.getSourceName());
						if (f1 != null) {
							Item foreignItem = (Item) f1.get(null);
							if (foreignItem != null)
								createItemConversionRecipe(mapping, foreignItem);
						}
					} catch (ClassCastException cast) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Failed to cast item field " + mapping.getSourceName() + " to Item typeof, ignoring!");
					} catch (NoSuchFieldException field) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Failed to find item field " + mapping.getSourceName() + ", ignoring!");
					} catch (IllegalAccessException illegal) {
						LanteaCraft.getLogger().log(Level.WARNING,
								"Could not access item field " + mapping.getSourceName() + ", ignoring!");
					}
			}

		} catch (ClassNotFoundException notfound) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not locate a class required for upgrade!", notfound);
		}
	}

	public void createBlockConversionRecipe(UpgradeMapping mapping, Block foreign) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				"Adding block conversion {foreign: " + foreign.getClass().getCanonicalName() + "; local: "
						+ mapping.getResultStack().toString() + "}.");
		if (mapping.getInputMetadata() != -1)
			GameRegistry.addShapelessRecipe(mapping.getResultStack(),
					new ItemStack(foreign, 1, mapping.getInputMetadata()));
		else
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1));
	}

	public void createItemConversionRecipe(UpgradeMapping mapping, Item foreign) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				"Adding item conversion {foreign: " + foreign.getClass().getCanonicalName() + "; local: "
						+ mapping.getResultStack().toString() + "}.");
		if (mapping.getInputMetadata() != -1)
			GameRegistry.addShapelessRecipe(mapping.getResultStack(),
					new ItemStack(foreign, 1, mapping.getInputMetadata()));
		else
			GameRegistry.addShapelessRecipe(mapping.getResultStack(), new ItemStack(foreign, 1));
	}
}
