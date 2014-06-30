package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.stargate.block.BlockStargateBase;
import pcl.lc.module.stargate.block.BlockStargateController;
import pcl.lc.module.stargate.block.BlockStargateRing;
import pcl.lc.module.stargate.block.BlockTransporterRing;
import pcl.lc.module.stargate.gui.ContainerStargateBase;
import pcl.lc.module.stargate.gui.ContainerStargateController;
import pcl.lc.module.stargate.gui.ContainerStargateControllerEnergy;
import pcl.lc.module.stargate.item.ItemControllerCrystal;
import pcl.lc.module.stargate.item.ItemCoreCrystal;
import pcl.lc.module.stargate.item.ItemGDO;
import pcl.lc.module.stargate.item.ItemIris;
import pcl.lc.module.stargate.item.ItemStargateRing;
import pcl.lc.module.stargate.item.ItemTransportRingActivator;
import pcl.lc.module.stargate.item.ItemTransporterRing;
import pcl.lc.module.stargate.render.BlockStargateBaseRenderer;
import pcl.lc.module.stargate.render.BlockStargateControllerRenderer;
import pcl.lc.module.stargate.render.BlockStargateRingRenderer;
import pcl.lc.module.stargate.render.BlockTransporterRingRenderer;
import pcl.lc.module.stargate.render.HeldItemRenderer;
import pcl.lc.module.stargate.render.RingPlatformBaseModel;
import pcl.lc.module.stargate.render.RingPlatformRingModel;
import pcl.lc.module.stargate.render.StargateControllerModel;
import pcl.lc.module.stargate.render.TileEntityStargateBaseRenderer;
import pcl.lc.module.stargate.render.TileEntityStargateControllerRenderer;
import pcl.lc.module.stargate.render.TileEntityTransporterRingRenderer;
import pcl.lc.module.stargate.tile.TileEntityStargateBase;
import pcl.lc.module.stargate.tile.TileEntityStargateController;
import pcl.lc.module.stargate.tile.TileEntityStargateRing;
import pcl.lc.module.stargate.tile.TileEntityTransporterRing;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModuleStargates implements IModule {

	public static class Blocks {
		public static BlockStargateBase stargateBaseBlock;
		public static BlockStargateRing stargateRingBlock;
		public static BlockStargateController stargateControllerBlock;
		public static BlockTransporterRing transporterRing;
	}

	public static class Items {
		public static ItemCoreCrystal coreCrystal;
		public static ItemControllerCrystal controllerCrystal;
		public static ItemIris iris;
		public static ItemGDO gdo;
		public static ItemTransportRingActivator transportRingActivator;
	}

	public static class Render {
		public static StargateControllerModel modelController;
		public static RingPlatformBaseModel modelRingPlatformBase;
		public static RingPlatformRingModel modelRingPlatformRing;

		public static BlockStargateBaseRenderer blockStargateBaseRenderer;
		public static BlockStargateRingRenderer blockStargateRingRenderer;
		public static BlockStargateControllerRenderer blockControllerRenderer;
		public static BlockTransporterRingRenderer blockTransporterRingRenderer;

		public static TileEntityStargateBaseRenderer tileEntityBaseRenderer;
		public static TileEntityStargateControllerRenderer tileEntityControllerRenderer;
		public static TileEntityTransporterRingRenderer tileEntityRingPlatformRenderer;

		public static HeldItemRenderer heldItemRenderer;
	}

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FMLInitializationEvent event) {
		Blocks.stargateRingBlock = RegistrationHelper.registerBlock(BlockStargateRing.class, ItemStargateRing.class,
				"stargateRing");
		Blocks.stargateBaseBlock = RegistrationHelper.registerBlock(BlockStargateBase.class, "stargateBase");
		Blocks.stargateControllerBlock = RegistrationHelper.registerBlock(BlockStargateController.class,
				"stargateController");

		Blocks.transporterRing = RegistrationHelper.registerBlock(BlockTransporterRing.class,
				ItemTransporterRing.class, "ringPlatform");

		GameRegistry.registerTileEntity(TileEntityStargateBase.class, "tileEntityStargateBase");
		GameRegistry.registerTileEntity(TileEntityStargateRing.class, "tileEntityStargateRing");
		GameRegistry.registerTileEntity(TileEntityStargateController.class, "tileEntityStargateController");
		GameRegistry.registerTileEntity(TileEntityTransporterRing.class, "tileEntityRingPlatform");

		Items.coreCrystal = RegistrationHelper.registerItem(ItemCoreCrystal.class, "coreCrystal");
		Items.controllerCrystal = RegistrationHelper.registerItem(ItemControllerCrystal.class, "controllerCrystal");
		Items.iris = RegistrationHelper.registerItem(ItemIris.class, "iris");
		Items.gdo = RegistrationHelper.registerItem(ItemGDO.class, "gdo");

		Items.transportRingActivator = RegistrationHelper.registerItem(ItemTransportRingActivator.class,
				"transportRingActivator");

		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateRingBlock, 1), "ICI", "NNN", "III", 'I',
				net.minecraft.init.Items.iron_ingot, 'N', "ingotNaquadahAlloy", 'C', new ItemStack(
						(Block) Block.blockRegistry.getObject("sandStone"), 1, 1));

		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateRingBlock, 1, 1), "CgC", "NpN", "IrI", 'I',
				net.minecraft.init.Items.iron_ingot, 'N', "ingotNaquadahAlloy", 'C', new ItemStack(
						net.minecraft.init.Blocks.sandstone, 1, 1), 'g', net.minecraft.init.Blocks.glowstone, 'r',
				net.minecraft.init.Items.redstone, 'p', net.minecraft.init.Items.ender_pearl);

		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateBaseBlock, 1), "CrC", "NeN", "IcI", 'I',
				net.minecraft.init.Items.iron_ingot, 'N', "ingotNaquadahAlloy", 'C', new ItemStack(
						net.minecraft.init.Blocks.sandstone, 1, 1), 'r', net.minecraft.init.Items.redstone, 'e',
				net.minecraft.init.Items.ender_eye, 'c', Items.coreCrystal);

		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateControllerBlock, 1), "bbb", "OpO", "OcO", 'b',
				net.minecraft.init.Blocks.stone_button, 'O', net.minecraft.init.Blocks.obsidian, 'p',
				net.minecraft.init.Items.ender_pearl, 'r', net.minecraft.init.Items.redstone, 'c',
				Items.controllerCrystal);

		RegistrationHelper.newRecipe(new ItemStack(Items.coreCrystal, 1), "bbr", "rdb", "brb", 'b', new ItemStack(
				net.minecraft.init.Items.dye, 1, 4), 'r', net.minecraft.init.Items.redstone, 'd',
				net.minecraft.init.Items.diamond);

		RegistrationHelper.newRecipe(new ItemStack(Items.controllerCrystal, 1), "roo", "odr", "oor", 'o',
				new ItemStack(net.minecraft.init.Items.dye, 1, 14), 'r', net.minecraft.init.Items.redstone, 'd',
				net.minecraft.init.Items.diamond);

		LanteaCraft.getProxy().addContainer(LanteaCraft.EnumGUIs.StargateBase.ordinal(), ContainerStargateBase.class);
		LanteaCraft.getProxy().addContainer(LanteaCraft.EnumGUIs.StargateControllerEnergy.ordinal(),
				ContainerStargateControllerEnergy.class);

		if (event.getSide() == Side.CLIENT) {
			Render.modelController = new StargateControllerModel(LanteaCraft.getResource("models/dhd.obj"));

			Render.modelRingPlatformBase = new RingPlatformBaseModel(
					LanteaCraft.getResource("models/transport_rings_base.obj"));
			Render.modelRingPlatformRing = new RingPlatformRingModel(
					LanteaCraft.getResource("models/transport_rings.obj"));

			Render.tileEntityBaseRenderer = new TileEntityStargateBaseRenderer();
			RegistrationHelper.addTileEntityRenderer(TileEntityStargateBase.class, Render.tileEntityBaseRenderer);

			Render.tileEntityControllerRenderer = new TileEntityStargateControllerRenderer();
			RegistrationHelper.addTileEntityRenderer(TileEntityStargateController.class,
					Render.tileEntityControllerRenderer);

			Render.tileEntityRingPlatformRenderer = new TileEntityTransporterRingRenderer();
			RegistrationHelper.addTileEntityRenderer(TileEntityTransporterRing.class,
					Render.tileEntityRingPlatformRenderer);

			Render.blockStargateBaseRenderer = new BlockStargateBaseRenderer();
			RegistrationHelper.registerRenderer(Render.blockStargateBaseRenderer);

			Render.blockStargateRingRenderer = new BlockStargateRingRenderer();
			RegistrationHelper.registerRenderer(Render.blockStargateRingRenderer);

			Render.blockControllerRenderer = new BlockStargateControllerRenderer();
			RegistrationHelper.registerRenderer(Render.blockControllerRenderer);

			Render.blockTransporterRingRenderer = new BlockTransporterRingRenderer();
			RegistrationHelper.registerRenderer(Render.blockTransporterRingRenderer);

			Render.heldItemRenderer = new HeldItemRenderer();
			MinecraftForgeClient.registerItemRenderer(Items.gdo, Render.heldItemRenderer);
		}

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
