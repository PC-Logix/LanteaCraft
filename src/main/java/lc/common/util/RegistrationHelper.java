package lc.common.util;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCItemBucket;
import lc.core.BuildInfo;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class RegistrationHelper {

	private static boolean isLateRegistrationZone = false;

	public static class VillagerMapping {
		public final int villagerID;
		public final ResourceLocation villagerSkin;

		public VillagerMapping(int id, ResourceLocation skin) {
			villagerID = id;
			villagerSkin = skin;
		}
	};

	public static class BlockItemMapping {
		public final Class<? extends Block> blockClass;
		public final Class<? extends ItemBlock> itemClass;

		public BlockItemMapping(Class<? extends Block> block, Class<? extends ItemBlock> item) {
			blockClass = block;
			itemClass = item;
		}
	}

	/**
	 * Internal list of all registered Block instances.
	 */
	private static Map<Integer, BlockItemMapping> registeredBlocks = new HashMap<Integer, BlockItemMapping>();

	/**
	 * Get a list of all Block and ItemBlock mappings in the registry.
	 * 
	 * @return The list of all Block and ItemBlock mappings.
	 */
	public static Map<Integer, BlockItemMapping> getBlockMappings() {
		if (!isLateRegistrationZone)
			LCLog.warn("Block registration mappings are being accessed earlier than init::finish, problems may occur.");
		return registeredBlocks;
	}

	/**
	 * Internal list of all registered Item instances.
	 */
	private static Map<Integer, Class<? extends Item>> registeredItems = new HashMap<Integer, Class<? extends Item>>();

	/**
	 * Get a list of all Item mappings in the registry.
	 * 
	 * @return The list of all Item mappings.
	 */
	public static Map<Integer, Class<? extends Item>> getItemMappings() {
		if (!isLateRegistrationZone)
			return null;
		return registeredItems;
	}

	/**
	 * Internal list of all registered Container instances.
	 */
	private static Map<Integer, Class<? extends Container>> registeredContainers = new HashMap<Integer, Class<? extends Container>>();
	/**
	 * Internal list of all registered GUI instances.
	 */
	private static Map<Integer, Class<? extends GuiScreen>> registeredGUIs = new HashMap<Integer, Class<? extends GuiScreen>>();
	/**
	 * Internal list of all registered Villager types.
	 */
	private static Map<String, VillagerMapping> registeredVillagers = new HashMap<String, VillagerMapping>();

	/**
	 * Marks the RegistrationHelper in the PostInit phase. If any registration
	 * occurs after the late registration flag is set, a warning will be issued.
	 */
	public static void flagLateRegistrationZone() {
		RegistrationHelper.isLateRegistrationZone = true;
	}

	/**
	 * Register a Block with a given class and unlocalized name. The block will
	 * use the default {@link ItemBlock} structure when held as an item.
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
	 * Register a block with a given class, a given item class and an
	 * unlocalized name. The block will display by default in CreativeTabs.
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
		return registerBlock(classOf, itemClassOf, unlocalizedName, CreativeTabHelper.getTab("LanteaCraft"));
	}

	/**
	 * Register a block with a given class, a given item class, an unlocalized
	 * name and a display preference in CreativeTabs.
	 * 
	 * @param classOf
	 *            The class of the block.
	 * @param itemClassOf
	 *            The class of the item.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @param inCreativeTabs
	 *            Show the item in the CreativeTabs instance.
	 * @return The Block singleton.
	 */
	public static <T extends Block> T registerBlock(Class<? extends T> classOf, Class<? extends ItemBlock> itemClassOf,
			String unlocalizedName, CreativeTabs tab) {
		LCLog.debug("Attempting to register block %s", unlocalizedName);
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this block is later than was expected!");
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryBlock = ctor.newInstance();
			theMysteryBlock.setBlockName(unlocalizedName);
			theMysteryBlock.setCreativeTab(tab);
			GameRegistry.registerBlock(theMysteryBlock, itemClassOf, unlocalizedName);
			registeredBlocks.put(registeredBlocks.size(), new BlockItemMapping(classOf, itemClassOf));
			return theMysteryBlock;
		} catch (Throwable e) {
			LCLog.fatal("Failed to register block, an exception occured.", e);
			throw new RuntimeException(e);
		}
	}

	public static <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName) {
		LCLog.debug("Attempting to register item " + unlocalizedName);
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this item is later than was expected!");
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryItem = ctor.newInstance();
			theMysteryItem.setUnlocalizedName(unlocalizedName).setCreativeTab(CreativeTabHelper.getTab("LanteaCraft"));
			GameRegistry.registerItem(theMysteryItem, unlocalizedName);
			registeredItems.put(registeredItems.size(), classOf);
			return theMysteryItem;
		} catch (Exception e) {
			LCLog.fatal("Failed to register item, an exception occured.", e);
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
	public static <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName, CreativeTabs tab) {
		LCLog.debug("Attempting to register item " + unlocalizedName);
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this item is later than was expected!");
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryItem = ctor.newInstance();
			theMysteryItem.setUnlocalizedName(unlocalizedName).setCreativeTab(tab);
			GameRegistry.registerItem(theMysteryItem, unlocalizedName);
			registeredItems.put(registeredItems.size(), classOf);
			return theMysteryItem;
		} catch (Exception e) {
			LCLog.fatal("Failed to register item, an exception occured.", e);
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
	 * @param bucketTextureName
	 *            The texture target for the bucket.
	 * @return The ItemSpecialBucket singleton for this host fluid.
	 */
	public static LCItemBucket registerSpecialBucket(LCBlock hostOf, String unlocalizedName, String bucketTextureName,
			CreativeTabs tab) {
		LCLog.debug("Attempting to register SpecialBucket " + unlocalizedName);
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this SpecialBucket is later than was expected!");
		LCItemBucket bucket = new LCItemBucket(hostOf);
		bucket.setUnlocalizedName(unlocalizedName).setCreativeTab(tab);
		bucket.setTargetTexture(bucketTextureName);
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
		LCLog.debug("Registering trade handler for villager ID " + villagerID);
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this trade handler is later than was expected!");
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
		LCLog.debug("Registering ore with name " + name);
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this ore is later than was expected!");
		OreDictionary.registerOre(String.format("%s:%s", BuildInfo.modID, name), item);
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
		LCLog.debug("Registering new generic recipe");
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this recipe is later than was expected!");
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
		LCLog.debug("Registering new generic shapeless recipe");
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this shapeless recipe is later than was expected!");
		GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
	}

	/**
	 * Registers a new smelting recipe.
	 * 
	 * @param in
	 *            The input stack
	 * @param out
	 *            The output result
	 * @param xp
	 *            Quantity of XP earnt
	 */
	public static void newSmeltingRecipe(ItemStack in, ItemStack out, float xp) {
		LCLog.debug("Registering new smelting recipe");
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this smelting is later than was expected!");
		FurnaceRecipes.smelting().func_151394_a(in, out, xp);
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
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this random chest behaviour is later than was expected!");
		WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
		for (String element : category) {
			LCLog.debug("Adding new WeightedRandomChestContent for element " + element);
			ChestGenHooks.addItem(element, item);
		}
	}

	/**
	 * Register a block renderer
	 * 
	 * @param renderer
	 *            A block renderer
	 */
	public static void registerRenderer(ISimpleBlockRenderingHandler renderer) {
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this block renderer is later than was expected!");
		// TODO: Fix rendering registry :<
		// renderer.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderer);
	}

	/**
	 * Registers a tile entity renderer
	 * 
	 * @param teClass
	 *            The tile entity class
	 * @param renderer
	 *            The renderer object
	 */
	public static void addTileEntityRenderer(Class<? extends TileEntity> teClass, Object renderer) {
		if (isLateRegistrationZone)
			LCLog.warn("Warning, registration of this tile-entity renderer is later than was expected!");
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, (TileEntitySpecialRenderer) renderer);
	}

	public static void registerEntityRenderer(Class<? extends Entity> entity, Object renderer) {
		RenderingRegistry.registerEntityRenderingHandler(entity, (Render) renderer);
	}

	public static int registerVillager(int id, String name, ResourceLocation skin) {
		LCLog.debug("Adding villager ID " + id + " with name " + name);
		registeredVillagers.put(name, new VillagerMapping(id, skin));
		return id;
	}

	public static void registerContainer(int id, Class<? extends Container> cls) {
		LCLog.debug("Registering container with ID " + id + ", class " + cls.getCanonicalName());
		registeredContainers.put(id, cls);
	}

	public static void registerGui(int id, Class<? extends GuiScreen> cls) {
		LCLog.debug("Registering GUI with ID " + id + ", class " + cls.getCanonicalName());
		registeredGUIs.put(id, cls);
	}

	public static Class<? extends Container> getRegisteredContainer(int id) {
		return registeredContainers.get(id);
	}

	public static Class<? extends GuiScreen> getRegisteredGui(int id) {
		return registeredGUIs.get(id);
	}

	public static int getRegisteredVillager(String name) {
		VillagerMapping villager = registeredVillagers.get(name);
		if (villager != null)
			return villager.villagerID;
		return 0;
	}
}
