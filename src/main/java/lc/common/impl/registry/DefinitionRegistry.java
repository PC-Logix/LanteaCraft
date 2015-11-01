package lc.common.impl.registry;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lc.LCRuntime;
import lc.api.components.ComponentType;
import lc.api.components.IComponentRegistry;
import lc.api.components.IDefinitionRegistry;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.ILanteaCraftRenderer;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderer;
import lc.common.base.LCEntityRenderer;
import lc.common.base.LCItem;
import lc.common.base.LCItemBucket;
import lc.common.base.LCItemRenderer;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.configuration.IConfigure;
import lc.common.util.LCCreativeTabManager;
import lc.common.util.Tracer;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Global definition registry implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class DefinitionRegistry implements IDefinitionRegistry {

	/**
	 * Types of renderers managed by this registry.
	 *
	 * @author AfterLifeLochie
	 */
	public static enum RendererType {
		/** Block type */
		BLOCK,
		/** Entity type */
		ENTITY,
		/** Tile type */
		TILE,
		/** Item type */
		ITEM;
	}

	/** Pool of all known definitions. */
	private final Map<String, IContainerDefinition> definitionPool;

	/** Internal list of all registered renderers. */
	private final Map<RendererType, Map<Class<?>, Class<? extends ILanteaCraftRenderer>>> registeredRenderers;

	/** Internal list of all initialized renderers. */
	private final Map<RendererType, Map<Class<?>, ILanteaCraftRenderer>> initializedRenderers;

	/** Default constructor */
	public DefinitionRegistry() {
		definitionPool = new HashMap<String, IContainerDefinition>();
		registeredRenderers = new HashMap<RendererType, Map<Class<?>, Class<? extends ILanteaCraftRenderer>>>();
		initializedRenderers = new HashMap<DefinitionRegistry.RendererType, Map<Class<?>, ILanteaCraftRenderer>>();
	}

	@Override
	public void addDefinition(IContainerDefinition definition) {
		if (definitionPool.containsKey(definition.getName().toLowerCase()))
			throw new RuntimeException("Attempt to overwrite existing definition " + definition.getName());
		definitionPool.put(definition.getName().toLowerCase(), definition);
	}

	@Override
	public IContainerDefinition getDefinition(String name) {
		return definitionPool.get(name.toLowerCase());
	}

	/**
	 * Initializes the registry
	 *
	 * @param runtime
	 *            The LanteaCraft runtime instance
	 * @param event
	 *            The FML event initializing the runtime
	 */
	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		Tracer.begin(this);
		IComponentRegistry components = runtime.registries().components();
		LCLog.debug("Evaluating %s definitions for candidacy.", definitionPool.size());
		for (IContainerDefinition definition : definitionPool.values())
			if (definition instanceof BlockItemDefinition) {
				BlockItemDefinition element = (BlockItemDefinition) definition;
				if (components.isEnabled(element.getComponentOwner())) {
					LCLog.trace("Registering element %s, component %s enabled.", element.getName(),
							element.getComponentOwner());
					element.init(this);
				} else
					LCLog.trace("Dropping registration for element %s, component %s disabled.", element.getName(),
							element.getComponentOwner());
			} else if (definition instanceof EntityDefinition) {
				EntityDefinition element = (EntityDefinition) definition;
				if (components.isEnabled(element.getComponentOwner())) {
					LCLog.trace("Registering element %s, component %s enabled.", element.getName(),
							element.getComponentOwner());
					element.init(this);
				} else
					LCLog.trace("Dropping registration for element %s, component %s disabled.", element.getName(),
							element.getComponentOwner());
			} else
				LCLog.warn("Strange definition type %s, ignoring it.", definition.getClass().getName());
		Tracer.end();
	}

	/**
	 * Register a block with a given class, a given item class and an
	 * unlocalized name. The block will display in the default Creative tabs.
	 *
	 * @param classOf
	 *            The class of the block.
	 * @param itemClassOf
	 *            The class of the item.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @param type
	 *            The type of the component
	 * @return The Block singleton.
	 */
	public <T extends Block> T registerBlock(Class<? extends T> classOf, Class<? extends ItemBlock> itemClassOf,
			String unlocalizedName, ComponentType type) {
		return registerBlock(classOf, itemClassOf, unlocalizedName, type, LCCreativeTabManager.getTab("LanteaCraft"));
	}

	/**
	 * Register a block with a given class, a given item class, an unlocalized
	 * name, a base type and a display preference in CreativeTabs.
	 *
	 * @param classOf
	 *            The class of the block.
	 * @param itemClassOf
	 *            The class of the item.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @param type
	 *            The component type of the block
	 * @param tab
	 *            The creative tab to place the Block into.
	 * @return The Block singleton.
	 */
	public <T extends Block> T registerBlock(Class<? extends T> classOf, Class<? extends ItemBlock> itemClassOf,
			String unlocalizedName, ComponentType type, CreativeTabs tab) {
		LCLog.debug("Attempting to register block %s", unlocalizedName);
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryBlock = ctor.newInstance();
			theMysteryBlock.setBlockName(unlocalizedName);
			theMysteryBlock.setCreativeTab(tab);
			if (theMysteryBlock instanceof IConfigure)
				((IConfigure) theMysteryBlock).configure(LCRuntime.runtime.config().config(type));
			GameRegistry.registerBlock(theMysteryBlock, itemClassOf, unlocalizedName);
			return theMysteryBlock;
		} catch (Throwable e) {
			LCLog.fatal("Failed to register block, an exception occured.", e);
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
	 * @param type
	 *            The component type of the item
	 * @return The Item singleton.
	 */
	public <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName, ComponentType type) {
		return registerItem(classOf, unlocalizedName, type, LCCreativeTabManager.getTab("LanteaCraft"));
	}

	/**
	 * Registers an item with a given class and an unlocalized name.
	 *
	 * @param classOf
	 *            The class of the item.
	 * @param unlocalizedName
	 *            The unlocalized name.
	 * @param type
	 *            The component type of the item
	 * @param tab
	 *            The creative tab to place the Item into.
	 * @return The Item singleton.
	 */
	public <T extends Item> T registerItem(Class<? extends T> classOf, String unlocalizedName, ComponentType type,
			CreativeTabs tab) {
		LCLog.debug("Attempting to register item " + unlocalizedName);
		try {
			Constructor<? extends T> ctor = classOf.getConstructor();
			T theMysteryItem = ctor.newInstance();
			theMysteryItem.setUnlocalizedName(unlocalizedName).setCreativeTab(tab);
			if (theMysteryItem instanceof IConfigure)
				((IConfigure) theMysteryItem).configure(LCRuntime.runtime.config().config(type));
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
	 * @param tab
	 *            The creative tab to display the bucket on.
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
	 * Registers a block renderer with the definition registry
	 *
	 * @param block
	 *            The block class
	 * @param renderer
	 *            The block renderer to bind
	 */
	public void registerBlockRenderer(Class<? extends LCBlock> block, Class<? extends LCBlockRenderer> renderer) {
		if (!registeredRenderers.containsKey(RendererType.BLOCK))
			registeredRenderers.put(RendererType.BLOCK, new HashMap<Class<?>, Class<? extends ILanteaCraftRenderer>>());
		registeredRenderers.get(RendererType.BLOCK).put(block, renderer);
	}

	/**
	 * Registers a tile entity renderer with the definition registry
	 *
	 * @param tile
	 *            The tile class
	 * @param renderer
	 *            The tile renderer to bind
	 */
	public void registerTileRenderer(Class<? extends LCTile> tile, Class<? extends LCTileRenderer> renderer) {
		if (!registeredRenderers.containsKey(RendererType.TILE))
			registeredRenderers.put(RendererType.TILE, new HashMap<Class<?>, Class<? extends ILanteaCraftRenderer>>());
		registeredRenderers.get(RendererType.TILE).put(tile, renderer);
	}

	/**
	 * Registers an item renderer with the definition registry
	 *
	 * @param item
	 *            The item class
	 * @param renderer
	 *            The item renderer to bind
	 */
	public void registerItemRenderer(Class<? extends LCItem> item, Class<? extends LCItemRenderer> renderer) {
		if (!registeredRenderers.containsKey(RendererType.ITEM))
			registeredRenderers.put(RendererType.ITEM, new HashMap<Class<?>, Class<? extends ILanteaCraftRenderer>>());
		registeredRenderers.get(RendererType.ITEM).put(item, renderer);
	}

	/**
	 * Registers an entity renderer with the game registry
	 *
	 * @param entity
	 *            The entity class
	 * @param renderer
	 *            The renderer object
	 */
	public void registerEntityRenderer(Class<? extends Entity> entity, Class<? extends LCEntityRenderer> renderer) {
		if (!registeredRenderers.containsKey(RendererType.ENTITY))
			registeredRenderers
					.put(RendererType.ENTITY, new HashMap<Class<?>, Class<? extends ILanteaCraftRenderer>>());
		registeredRenderers.get(RendererType.ENTITY).put(entity, renderer);
	}

	/**
	 * Get a renderer for a class. If a dedicated renderer doesn't exist, null
	 * is returned.
	 *
	 * @param typeof
	 *            The element type.
	 * @param clazz
	 *            The element class.
	 * @return A dedicated renderer, or null if none is known.
	 */
	public ILanteaCraftRenderer getRendererFor(RendererType typeof, Class<?> clazz) {
		if (initializedRenderers.containsKey(typeof))
			for (Entry<Class<?>, ILanteaCraftRenderer> renderer : initializedRenderers.get(typeof).entrySet())
				if (renderer.getKey().equals(clazz))
					return renderer.getValue();

		if (!registeredRenderers.containsKey(typeof))
			return null;

		Map<Class<?>, Class<? extends ILanteaCraftRenderer>> typemap = registeredRenderers.get(typeof);
		for (Entry<Class<?>, Class<? extends ILanteaCraftRenderer>> type : typemap.entrySet())
			if (type.getKey().equals(clazz))
				try {
					ILanteaCraftRenderer renderer = type.getValue().getConstructor().newInstance();
					if (renderer instanceof IConfigure)
						((IConfigure) renderer).configure(LCRuntime.runtime.config().config(ComponentType.CLIENT));
					if (!initializedRenderers.containsKey(typeof))
						initializedRenderers.put(typeof, new HashMap<Class<?>, ILanteaCraftRenderer>());
					initializedRenderers.get(typeof).put(clazz, renderer);
					LCLog.trace("Successfully initialized renderer %s (type: %s)", type.getValue().getName(), typeof);
					return renderer;
				} catch (Throwable t) {
					LCLog.warn("Failed to initialize renderer.", t);
					return null;
				}
		return null;
	}

	/**
	 * Get a renderer from a class object.
	 *
	 * @param typeof
	 *            The type of renderer.
	 * @param type
	 *            The renderer's class
	 * @return The singleton of the renderer, or null if it doesn't exist.
	 */
	public ILanteaCraftRenderer getRenderer(RendererType typeof, Class<? extends ILanteaCraftRenderer> type) {
		if (initializedRenderers.containsKey(typeof))
			for (Entry<Class<?>, ILanteaCraftRenderer> renderer : initializedRenderers.get(typeof).entrySet())
				if (renderer.getValue().getClass().equals(type))
					return renderer.getValue();
		return null;
	}
}
