package pcl.lc.compat;

import java.lang.reflect.Field;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import pcl.lc.LanteaCraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class UpgradeHelper {

	String[] mapBlocks = new String[] { "sgBaseBlock", "sgRingBlock",
			"sgControllerBlock", "naquadahBlock", "naquadahOre" };
	String[] mapItems = new String[] { "naquadah", "naquadahIngot",
			"sgCoreCrystal", "sgControllerCrystal" };

	public static boolean detectSGCraftInstall() {
		try {
			Class<?> clazz_a = Class.forName("gcewing.sg.SGCraft");
			Class<?> clazz_b = Class.forName("gcewing.sg.Utils");
			if (clazz_a != null && clazz_b != null) return true;
			return false;
		} catch (ClassNotFoundException notfound) {
			return false;
		}
	}

	public static boolean detectSGCraftReloadedInstall() {
		try {
			Class<?> clazz = Class.forName("gcewing.sg.GCESGCompatHelper");
			if (clazz != null) return true;
			return false;
		} catch (ClassNotFoundException notfound) {
			return false;
		}
	}

	public void hookSGCraft() {
		try {
			Class<?> clazz = Class.forName("gcewing.sg.SGCraft");
			Class<?> ours_blocks = Class.forName("pcl.lc.LanteaCraft$Blocks");
			Class<?> ours_items = Class.forName("pcl.lc.LanteaCraft$Items");

			for (String block : mapBlocks) {
				try {
					Field f1 = clazz.getField(block);
					Field f2_local = ours_blocks.getField(block);
					if (f1 != null && f2_local != null) {
						Block foreignBlock = (Block) f1.get(null);
						Block localBlock = (Block) f2_local.get(null);
						if (foreignBlock != null && localBlock != null) {
							LanteaCraft.getLogger().log(
									Level.INFO,
									"Adding SGCraft convert: "
											+ foreignBlock.getClass().getName()
											+ " to "
											+ localBlock.getClass().getName());
							createBlockConversionRecipe(foreignBlock,
									localBlock, block.equals("sgRingBlock"));
						}
					}
				} catch (ClassCastException cast) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Failed to cast block field " + block
									+ " to Block typeof, ignoring!");
				} catch (NoSuchFieldException field) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Failed to find block field " + block
									+ ", ignoring!");
				} catch (IllegalAccessException illegal) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Could not access block field " + block
									+ ", ignoring!");
				}
			}

			for (String item : mapItems) {
				try {
					Field f1 = clazz.getField(item);
					Field f2_local = ours_items.getField(item);
					if (f1 != null && f2_local != null) {

						Item foreignItem = (Item) f1.get(null);
						Item localItem = (Item) f2_local.get(null);
						if (foreignItem != null && localItem != null) {
							LanteaCraft.getLogger().log(
									Level.INFO,
									"Adding SGCraft convert: "
											+ foreignItem.getClass().getName()
											+ " to "
											+ localItem.getClass().getName());
							createItemConversionRecipe(foreignItem, localItem,
									false);
						}
					}
				} catch (ClassCastException cast) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Failed to cast item field " + item
									+ " to Item typeof, ignoring!");
				} catch (NoSuchFieldException field) {
					LanteaCraft.getLogger()
							.log(Level.WARNING,
									"Failed to find item field " + item
											+ ", ignoring!");
				} catch (IllegalAccessException illegal) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Could not access item field " + item
									+ ", ignoring!");
				}
			}
		} catch (ClassNotFoundException notfound) {
			LanteaCraft.getLogger().log(Level.WARNING,
					"Could not locate a class required for upgrade!", notfound);
		}
	}

	public void hookSGCraftReloaded() {
		try {
			Class<?> clazz_blocks = Class.forName("gcewing.sg.SGCraft$Blocks");
			Class<?> clazz_items = Class.forName("gcewing.sg.SGCraft$Items");

			Class<?> ours_blocks = Class.forName("pcl.lc.LanteaCraft$Blocks");
			Class<?> ours_items = Class.forName("pcl.lc.LanteaCraft$Items");

			for (String block : mapBlocks) {
				try {
					Field f1 = clazz_blocks.getField(block);
					Field f2_local = ours_blocks.getField(block);
					if (f1 != null && f2_local != null) {
						Block foreignBlock = (Block) f1.get(null);
						Block localBlock = (Block) f2_local.get(null);
						if (foreignBlock != null && localBlock != null) {
							LanteaCraft.getLogger().log(
									Level.INFO,
									"Adding SGCraft-Reloaded convert: "
											+ foreignBlock.getClass().getName()
											+ " to "
											+ localBlock.getClass().getName());
							createBlockConversionRecipe(foreignBlock,
									localBlock, block.equals("sgRingBlock"));
						}
					}
				} catch (ClassCastException cast) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Failed to cast block field " + block
									+ " to Block typeof, ignoring!");
				} catch (NoSuchFieldException field) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Failed to find block field " + block
									+ ", ignoring!");
				} catch (IllegalAccessException illegal) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Could not access block field " + block
									+ ", ignoring!");
				}
			}

			for (String item : mapItems) {
				try {
					Field f1 = clazz_items.getField(item);
					Field f2_local = ours_items.getField(item);
					if (f1 != null && f2_local != null) {

						Item foreignItem = (Item) f1.get(null);
						Item localItem = (Item) f2_local.get(null);
						if (foreignItem != null && localItem != null) {
							LanteaCraft.getLogger().log(
									Level.INFO,
									"Adding SGCraft-Reloaded convert: "
											+ foreignItem.getClass().getName()
											+ " to "
											+ localItem.getClass().getName());
							createItemConversionRecipe(foreignItem, localItem,
									false);
						}
					}
				} catch (ClassCastException cast) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Failed to cast item field " + item
									+ " to Item typeof, ignoring!");
				} catch (NoSuchFieldException field) {
					LanteaCraft.getLogger()
							.log(Level.WARNING,
									"Failed to find item field " + item
											+ ", ignoring!");
				} catch (IllegalAccessException illegal) {
					LanteaCraft.getLogger().log(
							Level.WARNING,
							"Could not access item field " + item
									+ ", ignoring!");
				}
			}

		} catch (ClassNotFoundException notfound) {
			LanteaCraft.getLogger().log(Level.WARNING,
					"Could not locate a class required for upgrade!", notfound);
		}
	}

	public void createBlockConversionRecipe(Block foreign, Block local,
			boolean isMetadataSensitive) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				"Adding block conversion {foreign: "
						+ foreign.getClass().getCanonicalName() + "; local: "
						+ local.getClass().getCanonicalName() + "; sensitive: "
						+ isMetadataSensitive + "}.");
		if (!isMetadataSensitive) {
			GameRegistry.addShapelessRecipe(new ItemStack(local, 1),
					new ItemStack(foreign, 1));
		} else {
			for (int i = 0; i < 16; i++)
				GameRegistry.addShapelessRecipe(new ItemStack(local, 1, i),
						new ItemStack(foreign, 1, i));
		}
	}

	public void createItemConversionRecipe(Item foreign, Item local,
			boolean isMetadataSensitive) {
		LanteaCraft.getLogger().log(
				Level.INFO,
				"Adding item conversion {foreign: "
						+ foreign.getClass().getCanonicalName() + "; local: "
						+ local.getClass().getCanonicalName() + "; sensitive: "
						+ isMetadataSensitive + "}.");
		if (!isMetadataSensitive) {
			GameRegistry.addShapelessRecipe(new ItemStack(local, 1),
					new ItemStack(foreign, 1));
		} else {
			for (int i = 0; i < 16; i++)
				GameRegistry.addShapelessRecipe(new ItemStack(foreign, 1, i),
						new ItemStack(local, 1, i));
		}
	}
}
