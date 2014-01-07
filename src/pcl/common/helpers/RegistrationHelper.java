package pcl.common.helpers;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import pcl.lc.LanteaCraft;
import pcl.lc.fluids.ItemSpecialBucket;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class RegistrationHelper {

	/**
	 * Register a Block with a given class and unlocalized name. The block will use the default
	 * {@link ItemBlock} structure when held as an item.
	 * 
	 * @param classOf
	 *            The class of the block.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @return The Block singleton.
	 */
	public static <T extends Block> T registerBlock(Class<? extends T> classOf, String unlocalizedName) {
		return registerBlock(classOf, ItemBlock.class, unlocalizedName);
	}

	/**
	 * Register a block with a given class, a given item class and an unlocalized name.
	 * 
	 * @param classOf
	 *            The class of the block.
	 * @param itemClassOf
	 *            The class of the item.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @return The Block singleton.
	 */
	public static <T extends Block> T registerBlock(Class<? extends T> classOf, Class<? extends ItemBlock> itemClassOf,
			String unlocalizedName) {
		LanteaCraft.getLogger().log(Level.INFO, String.format("Attempting to register block %s", unlocalizedName));
		try {
			int id = LanteaCraft.getProxy().getConfig().getBlock(unlocalizedName, 4094).getInt();
			Constructor<? extends Block> ctor = classOf.getConstructor(int.class);
			T theMysteryBlock = (T) ctor.newInstance(id);
			theMysteryBlock.setUnlocalizedName(unlocalizedName).setCreativeTab(
					LanteaCraft.getInstance().getCreativeTab());
			// TODO: Move texture name setting somewhere more useful.
			// theBlock.setTextureName(LanteaCraft.getInstance().getAssetKey() + ":" +
			// unlocalizedName + "_"
			// + getRenderMode());
			GameRegistry.registerBlock(theMysteryBlock, itemClassOf, unlocalizedName);
			return theMysteryBlock;
		} catch (Exception e) {
			LanteaCraft.getLogger().log(Level.SEVERE, "Failed to register block, an exception occured.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Registers an item with a given class and an unlocalized name.
	 * 
	 * @param classOf
	 *            The class of the item.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @return The Item singleton.
	 */
	public static <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName) {
		LanteaCraft.getLogger().log(Level.FINE, "Attempting to register item " + unlocalizedName);
		try {
			int id = LanteaCraft.getProxy().getConfig().getItem(unlocalizedName, 31743).getInt();
			Constructor<? extends Item> ctor = classOf.getConstructor(int.class);
			T theMysteryItem = (T) ctor.newInstance(id);
			theMysteryItem.setUnlocalizedName(unlocalizedName).setCreativeTab(
					LanteaCraft.getInstance().getCreativeTab());
			// TODO: Move texture name setting somewhere more useful.
			// item.setTextureName(LanteaCraft.getInstance().getAssetKey() + ":" +
			// unlocalizedName + "_" + getRenderMode());
			GameRegistry.registerItem(theMysteryItem, unlocalizedName);
			return theMysteryItem;
		} catch (Exception e) {
			LanteaCraft.getLogger().log(Level.SEVERE, "Failed to register item, an exception occured.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Registers a special bucket.
	 * 
	 * @param hostOf
	 *            The host fluid.
	 * @param unlocalizedName
	 *            The unlocalized name for the bucket.
	 * @return The ItemSpecialBucket singleton for this host fluid.
	 */
	public static ItemSpecialBucket registerSpecialBucket(Block hostOf, String unlocalizedName) {
		LanteaCraft.getLogger().log(Level.FINE, "Attempting to register SpecialBucket " + unlocalizedName);
		int id = LanteaCraft.getProxy().getConfig().getItem(unlocalizedName, 31743).getInt();
		ItemSpecialBucket bucket = new ItemSpecialBucket(id, hostOf);
		bucket.setUnlocalizedName(LanteaCraft.getInstance().getAssetKey() + ":" + unlocalizedName);
		// TODO: Move texture name setting somewhere more useful.
		// bucket.setTextureName(LanteaCraft.getInstance().getAssetKey() + ":" +
		// unlocalizedName + "_" + getRenderMode());
		bucket.setCreativeTab(LanteaCraft.getInstance().getCreativeTab());
		GameRegistry.registerItem(bucket, unlocalizedName);
		return bucket;
	}

	/**
	 * Registers a trade handler for a type of villager.
	 * 
	 * @param villagerID
	 *            The villager type ID.
	 * @param handler
	 *            The handler to register.
	 */
	public static void addTradeHandler(int villagerID, IVillageTradeHandler handler) {
		LanteaCraft.getLogger().log(Level.FINE, "Registering trade handler for villager ID " + villagerID);
		VillagerRegistry.instance().registerVillageTradeHandler(villagerID, handler);
	}

	/**
	 * Registers an ore with the ore dictionary.
	 * 
	 * @param name
	 *            The ore name.
	 * @param item
	 *            The ItemStack.
	 */
	public static void registerOre(String name, ItemStack item) {
		LanteaCraft.getLogger().log(Level.FINE, "Registering ore with name " + name);
		OreDictionary.registerOre(name, item);
	}

	/**
	 * Creates a new shaped recipe.
	 * 
	 * @param product
	 *            The product ItemStack.
	 * @param params
	 *            The crafting arrangement.
	 */
	public static void newRecipe(ItemStack product, Object... params) {
		LanteaCraft.getLogger().log(Level.FINE, "Registering new generic recipe");
		GameRegistry.addRecipe(new ShapedOreRecipe(product, params));
	}

	/**
	 * Creates a new shapeless recipe.
	 * 
	 * @param product
	 *            The product ItemStack.
	 * @param params
	 *            The crafting components.
	 */
	public static void newShapelessRecipe(ItemStack product, Object... params) {
		LanteaCraft.getLogger().log(Level.FINE, "Registering new generic shapeless recipe");
		GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
	}

	/**
	 * Registers a new chest item handler.
	 * 
	 * @param stack
	 *            The ItemStack to add.
	 * @param minQty
	 *            The minimum random item quantity.
	 * @param maxQty
	 *            The maximum random item quantity.
	 * @param weight
	 *            The weighting of the random number generation for this random.
	 * @param category
	 *            The categories of chests this rule applies to.
	 */
	public static void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
		WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
		for (String element : category) {
			LanteaCraft.getLogger().log(Level.FINE, "Adding new WeightedRandomChestContent for element " + element);
			ChestGenHooks.addItem(element, item);
		}
	}
}
