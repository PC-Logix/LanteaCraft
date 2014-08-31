package lc.common.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

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
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import lc.api.components.IComponentRegistry;
import lc.api.components.IDefinitionRegistry;
import lc.api.defs.ILanteaCraftDefinition;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCItemBucket;
import lc.common.base.LCTile;
import lc.common.util.CreativeTabHelper;
import lc.core.BuildInfo;
import lc.core.LCRuntime;

public class DefinitionRegistry implements IDefinitionRegistry {

	/** Pool of all known definitions. */
	private final HashMap<String, ILanteaCraftDefinition> definitionPool;
	/** Internal list of all registered Container instances. */
	private final Map<Integer, Class<? extends Container>> registeredContainers;
	/** Internal list of all registered GUI instances. */
	private final Map<Integer, Class<? extends GuiScreen>> registeredGUIs;

	public DefinitionRegistry() {
		definitionPool = new HashMap<String, ILanteaCraftDefinition>();
		registeredContainers = new HashMap<Integer, Class<? extends Container>>();
		registeredGUIs = new HashMap<Integer, Class<? extends GuiScreen>>();
	}

	@Override
	public void addDefinition(ILanteaCraftDefinition definition) {
		definitionPool.put(definition.getName(), definition);
	}

	@Override
	public ILanteaCraftDefinition getDefinition(String name) {
		return definitionPool.get(name);
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		IComponentRegistry components = runtime.registries().components();
		LCLog.debug("Evaluating %s definitions for candidacy.", definitionPool.size());
		for (ILanteaCraftDefinition definition : definitionPool.values()) {
			if (definition instanceof BlockItemDefinition) {
				BlockItemDefinition element = (BlockItemDefinition) definition;
				if (components.isEnabled(element.getComponentOwner())) {
					LCLog.trace("Registering element %s, component %s enabled.", element.getName(),
							element.getComponentOwner());
					element.init(this);
				} else
					LCLog.trace("Dropping registration for element %s, component %s disabled.", element.getName(),
							element.getComponentOwner());
			} else
				LCLog.warn("Strange definition type %s, ignoring it.", definition.getClass().getName());
		}
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
	public <T extends Block> T registerBlock(Class<? extends T> classOf, String unlocalizedName) {
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
	public <T extends Block> T registerBlock(Class<? extends T> classOf, Class<? extends ItemBlock> itemClassOf,
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
	public <T extends Block> T registerBlock(Class<? extends T> classOf, Class<? extends ItemBlock> itemClassOf,
			String unlocalizedName, CreativeTabs tab) {
		LCLog.debug("Attempting to register block %s", unlocalizedName);
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryBlock = ctor.newInstance();
			theMysteryBlock.setBlockName(unlocalizedName);
			theMysteryBlock.setCreativeTab(tab);
			GameRegistry.registerBlock(theMysteryBlock, itemClassOf, unlocalizedName);
			return theMysteryBlock;
		} catch (Throwable e) {
			LCLog.fatal("Failed to register block, an exception occured.", e);
			throw new RuntimeException(e);
		}
	}

	public <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName) {
		LCLog.debug("Attempting to register item " + unlocalizedName);
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryItem = ctor.newInstance();
			theMysteryItem.setUnlocalizedName(unlocalizedName).setCreativeTab(CreativeTabHelper.getTab("LanteaCraft"));
			GameRegistry.registerItem(theMysteryItem, unlocalizedName);
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
	public <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName, CreativeTabs tab) {
		LCLog.debug("Attempting to register item " + unlocalizedName);
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryItem = ctor.newInstance();
			theMysteryItem.setUnlocalizedName(unlocalizedName).setCreativeTab(tab);
			GameRegistry.registerItem(theMysteryItem, unlocalizedName);
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
	public LCItemBucket registerSpecialBucket(LCBlock hostOf, String unlocalizedName, String bucketTextureName,
			CreativeTabs tab) {
		LCLog.debug("Attempting to register SpecialBucket " + unlocalizedName);
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
	public void addTradeHandler(int villagerID, IVillageTradeHandler handler) {
		LCLog.debug("Registering trade handler for villager ID " + villagerID);
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
	public void registerOre(String name, ItemStack item) {
		LCLog.debug("Registering ore with name " + name);
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
	public void newRecipe(ItemStack product, Object... params) {
		LCLog.debug("Registering new generic recipe");
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
	public void newShapelessRecipe(ItemStack product, Object... params) {
		LCLog.debug("Registering new generic shapeless recipe");
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
	public void newSmeltingRecipe(ItemStack in, ItemStack out, float xp) {
		LCLog.debug("Registering new smelting recipe");
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
	public void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
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
	public void registerRenderer(ISimpleBlockRenderingHandler renderer) {
		// TODO: Fix rendering registry :<
		// renderer.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderer);
	}

	/**
	 * Registers a TileEntity
	 * 
	 * @param tileType
	 *            The tile class type.
	 * @param tileName
	 *            The tile name.
	 */
	public void registerTileEntity(Class<? extends LCTile> tileType, String tileName) {
		GameRegistry.registerTileEntity(tileType, tileName);
	}

	/**
	 * Registers a tile entity renderer
	 * 
	 * @param teClass
	 *            The tile entity class
	 * @param renderer
	 *            The renderer object
	 */
	public void addTileEntityRenderer(Class<? extends TileEntity> teClass, Object renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, (TileEntitySpecialRenderer) renderer);
	}

	public void registerEntityRenderer(Class<? extends Entity> entity, Object renderer) {
		RenderingRegistry.registerEntityRenderingHandler(entity, (Render) renderer);
	}

	public void registerContainer(int id, Class<? extends Container> cls) {
		LCLog.debug("Registering container with ID " + id + ", class " + cls.getCanonicalName());
		registeredContainers.put(id, cls);
	}

	public void registerGui(int id, Class<? extends GuiScreen> cls) {
		LCLog.debug("Registering GUI with ID " + id + ", class " + cls.getCanonicalName());
		registeredGUIs.put(id, cls);
	}

	public Class<? extends Container> getRegisteredContainer(int id) {
		return registeredContainers.get(id);
	}

	public Class<? extends GuiScreen> getRegisteredGui(int id) {
		return registeredGUIs.get(id);
	}

}
