//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Mod
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.base;

import gcewing.sg.SGCraft;
import gcewing.sg.base.BaseModClient.IDBinding;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

/**
 * TODO: This stub is to be removed, for our purposes it's pretty useless and
 * can be replaced with inline loading code.
 */
@Deprecated
public class BaseMod implements IGuiHandler {

	private boolean clientSide;
	private Configuration config;

	public void setConfig(Configuration c, boolean cs) {
		config = c;
		clientSide = cs;
	}

	@Deprecated
	public Item newItem(String name, String title) {
		return newItem(name, Item.class, title);
	}

	@Deprecated
	public <ITEM extends Item> ITEM newItem(String name, Class<ITEM> cls, String title) {
		try {
			int id = config.getItem(name, 31743).getInt();
			Constructor<ITEM> ctor = cls.getConstructor(int.class);
			ITEM item = ctor.newInstance(id);
			String qualName = SGCraft.getInstance().assetKey + ":" + name;
			item.setUnlocalizedName(qualName);
			item.setTextureName(qualName + "_" + SGCraft.RenderHD);
			LanguageRegistry.addName(item, title);
			if (clientSide)
				if (item.getCreativeTab() == null)
					item.setCreativeTab(CreativeTabs.tabMisc);
			return item;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public Block newBlock(String name, String title) {
		return newBlock(name, Block.class, title);
	}

	@Deprecated
	public <BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, String title) {
		return newBlock(name, cls, ItemBlock.class, title, SGCraft.getInstance().assetKey + ":" + name);
	}

	@Deprecated
	public <BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, Class itemClass, String title,
			String registerMeAs) {
		try {
			int id = config.getBlock(name, 4095).getInt();
			Constructor<BLOCK> ctor = cls.getConstructor(int.class);
			BLOCK block = ctor.newInstance(id);
			String qualName = SGCraft.getInstance().assetKey + ":" + name;
			block.setUnlocalizedName(qualName);
			System.out.println("Registering block " + qualName + ", id " + id);
			// block.func_111022_d(qualName.toLowerCase()); // Set default icon
			// name
			block.setTextureName(qualName + "_" + SGCraft.RenderHD); // Set
																		// default
																		// icon
																		// name
			// GameRegistry.registerBlock(block, itemClass);
			if (itemClass == null)
				itemClass = ItemBlock.class;
			GameRegistry.registerBlock(block, itemClass, registerMeAs);
			if (title != null) {
				LanguageRegistry.addName(block, title);
				if (clientSide)
					if (block.getCreativeTabToDisplayOn() == null && !title.startsWith("["))
						block.setCreativeTab(CreativeTabs.tabMisc);
			}
			return block;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// --------------- Ore registration
	// ----------------------------------------------------------

	@Deprecated
	public void registerOre(String name, Block block) {
		OreDictionary.registerOre(name, new ItemStack(block));
	}

	@Deprecated
	public void registerOre(String name, Item item) {
		OreDictionary.registerOre(name, item);
	}

	@Deprecated
	public static boolean blockMatchesOre(Block block, String name) {
		return stackMatchesOre(new ItemStack(block), name);
	}

	@Deprecated
	public static boolean itemMatchesOre(Item item, String name) {
		return stackMatchesOre(new ItemStack(item), name);
	}

	@Deprecated
	public static boolean stackMatchesOre(ItemStack stack, String name) {
		int id = OreDictionary.getOreID(stack);
		return id == OreDictionary.getOreID(name);
	}

	// --------------- Recipe construction
	// ----------------------------------------------------------

	@Deprecated
	public void newRecipe(Item product, int qty, Object... params) {
		newRecipe(new ItemStack(product, qty), params);
	}

	@Deprecated
	public void newRecipe(Block product, int qty, Object... params) {
		newRecipe(new ItemStack(product, qty), params);
	}

	@Deprecated
	public void newRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(product, params));
	}

	@Deprecated
	public void newShapelessRecipe(Block product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}

	@Deprecated
	public void newShapelessRecipe(Item product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}

	@Deprecated
	public void newShapelessRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
	}

	@Deprecated
	public void newSmeltingRecipe(Item product, int qty, Item input) {
		GameRegistry.addSmelting(input.itemID, new ItemStack(product, qty), 0);
	}

	@Deprecated
	public void newSmeltingRecipe(Item product, int qty, Block input) {
		GameRegistry.addSmelting(input.blockID, new ItemStack(product, qty), 0);
	}

	// --------------- Dungeon loot
	// ----------------------------------------------------------

	@Deprecated
	public void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
		WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
		for (String element : category)
			ChestGenHooks.addItem(element, item);
	}

	// --------------- Entity registration
	// ----------------------------------------------------------

	@Deprecated
	public void registerEntity(Class<? extends Entity> cls, String name, Enum id) {
		registerEntity(cls, name, id.ordinal());
	}

	@Deprecated
	public void registerEntity(Class<? extends Entity> cls, String name, int id) {
		registerEntity(cls, name, id, 1, true);
	}

	@Deprecated
	public void registerEntity(Class<? extends Entity> cls, String name, Enum id, int updateFrequency,
			boolean sendVelocityUpdates) {
		registerEntity(cls, name, id.ordinal(), updateFrequency, sendVelocityUpdates);
	}

	@Deprecated
	public void registerEntity(Class<? extends Entity> cls, String name, int id, int updateFrequency,
			boolean sendVelocityUpdates) {
		EntityRegistry.registerModEntity(cls, name, id, this, 256, updateFrequency, sendVelocityUpdates);
	}

	@Deprecated
	static class VSBinding extends IDBinding<ResourceLocation> {
	};

	@Deprecated
	public List<VSBinding> registeredVillagers = new ArrayList<VSBinding>();

	@Deprecated
	public int addVillager(int id, String name, ResourceLocation skin) {
		VSBinding b = new VSBinding();
		b.id = id;
		b.object = skin;
		registeredVillagers.add(b);
		return id;
	}

	@Deprecated
	public void addTradeHandler(int villagerID, IVillageTradeHandler handler) {
		VillagerRegistry.instance().registerVillageTradeHandler(villagerID, handler);
	}

	@Deprecated
	public void addContainer(Enum id, Class<? extends Container> cls) {
		addContainer(id.ordinal(), cls);
	}

	@Deprecated
	public void addContainer(int id, Class<? extends Container> cls) {
		containerClasses.put(id, cls);
	}

	@Deprecated
	public void openGui(EntityPlayer player, Enum id, World world, int x, int y, int z) {
		openGui(player, id.ordinal(), world, x, y, z);
	}

	@Deprecated
	public void openGui(EntityPlayer player, int id, World world, int x, int y, int z) {
		player.openGui(this, id, world, x, y, z);
	}

	@Deprecated
	Map<Integer, Class<? extends Container>> containerClasses = new HashMap<Integer, Class<? extends Container>>();

	@Deprecated
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Class cls = containerClasses.get(id);
		Object result;
		if (cls != null)
			result = createGuiElement(cls, player, world, x, y, z);
		else
			result = getGuiContainer(id, player, world, x, y, z);
		return result;
	}

	@Deprecated
	Container getGuiContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Deprecated
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Deprecated
	Object createGuiElement(Class cls, EntityPlayer player, World world, int x, int y, int z) {
		try {
			try {
				return cls.getMethod("create", EntityPlayer.class, World.class, int.class, int.class, int.class)
						.invoke(null, player, world, x, y, z);
			} catch (NoSuchMethodException e) {
				return cls.getConstructor(EntityPlayer.class, World.class, int.class, int.class, int.class)
						.newInstance(player, world, x, y, z);
			}
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause != null)
				cause.printStackTrace();
			else
				e.printStackTrace();
			return null;
		}
	}

}
