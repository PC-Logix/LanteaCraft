//------------------------------------------------------------------------------------------------
//
//   SG Craft - Main Class
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.event.terraingen.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.*;

@Mod(modid = Info.modID, name = Info.modName, version = Info.versionNumber)

@NetworkMod(clientSideRequired = true, serverSideRequired = false, versionBounds = Info.versionBounds)

public class SGCraft extends BaseMod {

	public static SGCraft mod;

	static boolean GenerateStruct = false;
	static boolean GalacticraftCompat = false;
	static int RenderHD = 32;
	static boolean HDModels = true;
	static boolean ActiveGateExplosion = true;
	
	public static SGChannel channel;
	public static BaseTEChunkManager chunkManager;
	
	public static SGBaseBlock sgBaseBlock;
	public static SGRingBlock sgRingBlock;
	public static SGControllerBlock sgControllerBlock;

	public static SGPegasusBaseBlock sgPegasusBaseBlock;
	public static SGPegasusRingBlock sgPegasusRingBlock;
	public static SGPegasusControllerBlock sgPegasusControllerBlock;
	
	//public static SGPortalBlock sgPortalBlock;
	public static Block naquadahBlock, naquadahOre;
	
	public static /*Base*/Item naquadah, naquadahIngot, sgCoreCrystal, sgControllerCrystal;
	
	public static boolean addOresToExistingWorlds;
	public static NaquadahOreWorldGen naquadahOreGenerator;
	public static int tokraVillagerID;

	public SGCraft() {
		mod = this;
	}
	
	@EventHandler 
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}
	
	@EventHandler 
	public void init(FMLInitializationEvent e) {
		super.init(e);
		configure();
		channel = new SGChannel(Info.modID);
		chunkManager = new BaseTEChunkManager(this);
		//System.out.printf("SGCraft: chunkManager = %s\n", chunkManager);
		chunkManager.debug = true;
	}

	@EventHandler 
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override	
	BaseModClient initClient() {
		return new SGCraftClient(this);
	}

	void configure() {
		SGControllerTE.configure(config);
		NaquadahOreWorldGen.configure(config);
		SGBaseTE.configure(config);
		addOresToExistingWorlds = config.getBoolean("options", "addOresToExistingWorlds", false);
		
		Property GenerateStruct = config.get("options", "GenerateStructures", true);
		SGCraft.GenerateStruct = config.getBoolean("stargate", "GenerateStructures", true);
		GenerateStruct.comment = "true/false Enables/Disables generation of Gate Rooms under Desert Pyramids";
		
		Property GalacticraftCompat = config.get("options", "GalacticCraftCompat", false);
		SGCraft.GalacticraftCompat = config.getBoolean("stargate", "GalacticCraftCompat", false);
		GalacticraftCompat.comment = "true/false Enables/Disables Galcticraft support THIS WILL CHANGE ALL EXISTING ADDRESSES!";
		
		Property textureRes = config.get("graphics_options", "textureRes", 32);
		SGCraft.RenderHD = 	config.getInteger("graphics_options", "textureRes", 32);
		textureRes.comment = "This value must be either 32, 64, or 128. Controls Textures Resolution";

		Property HDModels = config.get("graphics_options", "HDModels", true);
		SGCraft.HDModels = config.getBoolean("graphics_options", "HDModels", true);
		HDModels.comment = "True/False Should HD models be loaded for DHDs.";
		
		ActiveGateExplosion = config.getBoolean("options", "ActiveGateExplosion", true);
		
		if (SGCraft.GalacticraftCompat == true) {
			SGAddressing.minDimension = -99;
		}
	}		

	@Override
	void registerOther() {
		if (GenerateStruct == true) {
			System.out.println("GenerateStructure = true");
			MinecraftForge.TERRAIN_GEN_BUS.register(this);
	        try
	        {
	        	if (new CallableMinecraftVersion(null).minecraftVersion().equals("1.6.4")) {
	        		MapGenStructureIO.func_143031_a(FeatureUnderDesertPyramid.class, "SGCraft:DesertPyramid");
	        	}
	        }
	        catch (Throwable e)  { System.out.println("registerOther threw an exception"); }
		}
	}

	@Override
	void registerBlocks() {
		sgRingBlock = newBlock("stargateRing", SGRingBlock.class, SGRingItem.class, "Stargate Ring Segment");
		sgBaseBlock = newBlock("stargateBase", SGBaseBlock.class, "Stargate Base");
		sgControllerBlock = newBlock("stargateController", SGControllerBlock.class, "Stargate Controller");
	/*
		sgPegasusBaseBlock = newBlock("stargatePegasusBase", SGPegasusBaseBlock.class, "Pegasus Stargate Base");
		sgPegasusRingBlock = newBlock("stargatePegasusRing", SGPegasusRingBlock.class, "Pegasus Ring Segment");
		sgPegasusControllerBlock = newBlock("stargatePegasusController", SGPegasusControllerBlock.class, "Pegasus Stargate Controller");
	*/	
		//sgPortalBlock = newBlock("stargatePortal", SGPortalBlock.class, "Stargate Portal");
		naquadahBlock = newBlock("naquadahBlock", NaquadahBlock.class, "Naquadah Alloy Block");
		naquadahOre = newBlock("naquadahOre", NaquadahOreBlock.class, "Naquadah Ore");
	}
	
	@Override
	void registerItems() {
		naquadah = newItem("naquadah", "Naquadah");
		naquadahIngot = newItem("naquadahIngot", "Naquadah Alloy Ingot");
		sgCoreCrystal = newItem("sgCoreCrystal", "Stargate Core Crystal");
		sgControllerCrystal = newItem("sgControllerCrystal", "Stargate Controller Crystal");
	}
	
	@Override
	void registerOres() {
		registerOre("oreNaquadah", naquadahOre);
		registerOre("naquadah", naquadah);
		registerOre("ingotNaquadahAlloy", naquadahIngot);
	}
	
	@Override
	void registerRecipes() {
		ItemStack chiselledSandstone = new ItemStack(Block.sandStone, 1, 1);
		ItemStack smoothSandstone = new ItemStack(Block.sandStone, 1, 2);
		ItemStack sgChevronBlock = new ItemStack(sgRingBlock, 1, 1);
		ItemStack blueDye = new ItemStack(Item.dyePowder, 1, 4);
		ItemStack orangeDye = new ItemStack(Item.dyePowder, 1, 14);
		if (config.getBoolean("options", "allowCraftingNaquadah", false))
			newShapelessRecipe(naquadah, 1, Item.coal, Item.slimeBall, Item.blazePowder);
		newRecipe(sgRingBlock, 1, "CCC", "NNN", "SSS",
			'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone);
		newRecipe(sgChevronBlock, "CgC", "NpN", "SrS",
			'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone,
			'g', Item.glowstone, 'r', Item.redstone, 'p', Item.enderPearl);
		newRecipe(sgBaseBlock, 1, "CrC", "NeN", "ScS",
			'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone,
			'r', Item.redstone, 'e', Item.eyeOfEnder, 'c', sgCoreCrystal);
		newRecipe(sgControllerBlock, 1, "bbb", "OpO", "OcO",
			'b', Block.stoneButton, 'O', Block.obsidian, 'p', Item.enderPearl,
			'r', Item.redstone, 'c', sgControllerCrystal);
		newShapelessRecipe(naquadahIngot, 1, "naquadah", Item.ingotIron);
		newRecipe(naquadahBlock, 1, "NNN", "NNN", "NNN", 'N', "ingotNaquadahAlloy");
		newRecipe(naquadahIngot, 9, "B", 'B', naquadahBlock);
		if (config.getBoolean("options", "allowCraftingCrystals", false)) {
			newRecipe(sgCoreCrystal, 1, "bbr", "rdb", "brb",
				'b', blueDye, 'r', Item.redstone, 'd', Item.diamond);
			newRecipe(sgControllerCrystal, 1, "roo", "odr", "oor",
				'o', orangeDye, 'r', Item.redstone, 'd', Item.diamond);
		}
	}
	
	@Override
	void registerContainers() {
		//System.out.printf("SGCraft.registerContainers\n");
		addContainer(SGGui.SGBase, SGBaseContainer.class);
	}

	@Override
	void registerRandomItems() {
		String[] categories = {ChestGenHooks.MINESHAFT_CORRIDOR,
			ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
			ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH};
		addRandomChestItem(new ItemStack(sgBaseBlock), 1, 1, 2, categories);
		addRandomChestItem(new ItemStack(sgControllerBlock), 1, 1, 1, categories);
		addRandomChestItem(new ItemStack(sgRingBlock, 1, 0), 1, 3, 8, categories);
		addRandomChestItem(new ItemStack(sgRingBlock, 1, 1), 1, 3, 7, categories);
		addRandomChestItem(new ItemStack(sgCoreCrystal, 1, 0), 1, 1, 2, categories);
		addRandomChestItem(new ItemStack(sgControllerCrystal, 1, 0), 1, 1, 1, categories);
	}
	
	@Override
	void registerWorldGenerators() {
		if (config.getBoolean("options", "enableNaquadahOre", true)) {
			naquadahOreGenerator = new NaquadahOreWorldGen();
			GameRegistry.registerWorldGenerator(naquadahOreGenerator);
		}
	}
	
	@Override
	void registerVillagers() {
		tokraVillagerID = addVillager("tokra", resourceLocation("textures/skins/tokra.png"));
		addTradeHandler(tokraVillagerID, new SGTradeHandler());
	}
	
	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		//System.out.printf("SGCraft.onChunkLoad: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData.onChunkLoad(e);
	}

	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		//System.out.printf("SGCraft.onChunkSave: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData.onChunkSave(e);
	}

	@ForgeSubscribe
	public void onInitMapGen(InitMapGenEvent e) {
		FeatureGeneration.onInitMapGen(e);
	}

}
