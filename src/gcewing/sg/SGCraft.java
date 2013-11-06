//------------------------------------------------------------------------------------------------
//
//   SG Craft - Main Class
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.base.BaseConfiguration;
import gcewing.sg.base.BaseTEChunkManager;
import gcewing.sg.blocks.BlockNaquadah;
import gcewing.sg.blocks.BlockNaquadahOre;
import gcewing.sg.blocks.BlockStargateBase;
import gcewing.sg.blocks.BlockStargateController;
import gcewing.sg.blocks.BlockPegasusStargateBase;
import gcewing.sg.blocks.BlockPegasusStargateController;
import gcewing.sg.blocks.BlockPegasusStargateRing;
import gcewing.sg.blocks.BlockPortal;
import gcewing.sg.blocks.BlockStargateRing;
import gcewing.sg.container.ContainerStargateBase;
import gcewing.sg.core.GateAddressHelper;
import gcewing.sg.core.StargateNetworkChannel;
import gcewing.sg.core.EnumGuiList;
import gcewing.sg.generators.FeatureGeneration;
import gcewing.sg.generators.FeatureUnderDesertPyramid;
import gcewing.sg.generators.NaquadahOreWorldGen;
import gcewing.sg.generators.ChunkData;
import gcewing.sg.generators.TradeHandler;
import gcewing.sg.items.ItemPegasusStargateRing;
import gcewing.sg.items.ItemStargateRing;
import gcewing.sg.render.BaseOrientedCtrBlkRenderer;
import gcewing.sg.render.blocks.BlockStargateBaseRenderer;
import gcewing.sg.render.blocks.BlockStargateRingRenderer;
import gcewing.sg.render.model.StargateControllerModel;
import gcewing.sg.render.tileentity.TileEntityPegasusStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateControllerRenderer;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
import gcewing.sg.tileentity.TileEntityStargateRing;
import gcewing.sg.util.HelperCreativeTab;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = BuildInfo.modID, name = BuildInfo.modName, version = BuildInfo.versionNumber + "build" + BuildInfo.buildNumber)
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class SGCraft {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access
	 * this object safely
	 */
	private static SGCraft mod;

	/**
	 * Returns the current instance singleton of the SGCraft mod object
	 * 
	 * @return The current, if any, instance of the SGCraft mod
	 */
	public static SGCraft getInstance() {
		return SGCraft.mod;
	}

	/**
	 * Public declaration of all Block objects
	 */
	public static class Blocks {
		public static BlockStargateBase sgBaseBlock;
		public static BlockStargateRing sgRingBlock;
		public static BlockStargateController sgControllerBlock;

		public static BlockPegasusStargateBase sgPegasusBaseBlock;
		public static BlockPegasusStargateRing sgPegasusRingBlock;
		public static BlockPegasusStargateController sgPegasusControllerBlock;

		public static BlockPortal sgPortalBlock;

		public static Block naquadahBlock;
		public static Block naquadahOre;
	}

	/**
	 * Public declaration of all Item objects
	 */
	public static class Items {
		public static Item naquadah;
		public static Item naquadahIngot;
		public static Item sgCoreCrystal;
		public static Item sgControllerCrystal;
	}

	/**
	 * Public declaration of all render objects
	 */
	public static class Render {
		public static StargateControllerModel modelController;

		public static BaseOrientedCtrBlkRenderer blockOrientedRenderer;

		public static BlockStargateBaseRenderer blockBaseRenderer;
		public static BlockStargateRingRenderer blockRingRenderer;

		public static TileEntityStargateBaseRenderer tileEntityBaseRenderer;
		public static TileEntityPegasusStargateBaseRenderer tileEntityPegausBaseRenderer;
		public static TileEntityStargateControllerRenderer tileEntityControllerRenderer;
	}

	public static HelperCreativeTab sgCraftTab = new HelperCreativeTab(CreativeTabs.getNextID(), "SGCraft") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(Item.bakedPotato);
		}
	};

	@SidedProxy(clientSide = "gcewing.sg.SGCraftClientProxy", serverSide = "gcewing.sg.SGCraftCommonProxy")
	public static SGCraftCommonProxy proxy;

	private String assetKey = "gcewing_sg";
	public IGuiHandler guiProxy;

	public SGCraft() {
		SGCraft.mod = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load e) {
		proxy.onChunkLoad(e);
	}

	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save e) {
		proxy.onChunkSave(e);
	}

	@ForgeSubscribe
	public void onInitMapGen(InitMapGenEvent e) {
		proxy.onInitMapGen(e);
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(SGCraft.getInstance().assetKey, path);
	}

	public static SGCraftCommonProxy getProxy() {
		return SGCraft.getInstance().proxy;
	}

	public static String getAssetKey() {
		return SGCraft.getInstance().assetKey;
	}

	public static CreativeTabs getCreativeTab() {
		return SGCraft.getInstance().sgCraftTab;
	}

}
