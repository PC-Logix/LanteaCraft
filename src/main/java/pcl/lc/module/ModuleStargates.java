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
import pcl.lc.module.stargate.block.BlockStargateDHD;
import pcl.lc.module.stargate.block.BlockStargateRing;
import pcl.lc.module.stargate.block.BlockTransporterRing;
import pcl.lc.module.stargate.gui.ContainerStargateBase;
import pcl.lc.module.stargate.gui.ContainerStargateDHDEnergy;
import pcl.lc.module.stargate.item.ItemControllerCrystal;
import pcl.lc.module.stargate.item.ItemCoreCrystal;
import pcl.lc.module.stargate.item.ItemGDO;
import pcl.lc.module.stargate.item.ItemIris;
import pcl.lc.module.stargate.item.ItemStargateRing;
import pcl.lc.module.stargate.item.ItemTransportRingActivator;
import pcl.lc.module.stargate.item.ItemTransporterRing;
import pcl.lc.module.stargate.render.BlockStargateBaseRenderer;
import pcl.lc.module.stargate.render.BlockStargateDHDRenderer;
import pcl.lc.module.stargate.render.BlockStargateRingRenderer;
import pcl.lc.module.stargate.render.BlockTransporterRingRenderer;
import pcl.lc.module.stargate.render.ItemHeldRenderer;
import pcl.lc.module.stargate.render.ModelRingPlatformBase;
import pcl.lc.module.stargate.render.ModelRingPlatformRing;
import pcl.lc.module.stargate.render.ModelStargateDHD;
import pcl.lc.module.stargate.render.TileStargateBaseRenderer;
import pcl.lc.module.stargate.render.TileStargateDHDRenderer;
import pcl.lc.module.stargate.render.TileTransporterRingRenderer;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.module.stargate.tile.TileStargateDHD;
import pcl.lc.module.stargate.tile.TileStargateRing;
import pcl.lc.module.stargate.tile.TileTransporterRing;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModuleStargates implements IModule {

	public static class Blocks {
		public static BlockStargateBase stargateBaseBlock;
		public static BlockStargateRing stargateRingBlock;
		public static BlockStargateDHD stargateControllerBlock;
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
		public static ModelStargateDHD modelController;
		public static ModelRingPlatformBase modelRingPlatformBase;
		public static ModelRingPlatformRing modelRingPlatformRing;

		public static BlockStargateBaseRenderer blockStargateBaseRenderer;
		public static BlockStargateRingRenderer blockStargateRingRenderer;
		public static BlockStargateDHDRenderer blockControllerRenderer;
		public static BlockTransporterRingRenderer blockTransporterRingRenderer;

		public static TileStargateBaseRenderer tileEntityBaseRenderer;
		public static TileStargateDHDRenderer tileEntityControllerRenderer;
		public static TileTransporterRingRenderer tileEntityRingPlatformRenderer;

		public static ItemHeldRenderer heldItemRenderer;
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
		Blocks.stargateControllerBlock = RegistrationHelper.registerBlock(BlockStargateDHD.class,
				"stargateDHD");

		Blocks.transporterRing = RegistrationHelper.registerBlock(BlockTransporterRing.class,
				ItemTransporterRing.class, "ringPlatform");

		GameRegistry.registerTileEntity(TileStargateBase.class, "tileEntityStargateBase");
		GameRegistry.registerTileEntity(TileStargateRing.class, "tileEntityStargateRing");
		GameRegistry.registerTileEntity(TileStargateDHD.class, "tileEntityStargateDHD");
		GameRegistry.registerTileEntity(TileTransporterRing.class, "tileEntityRingPlatform");

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

		RegistrationHelper.registerContainer(LanteaCraft.EnumGUIs.StargateBase.ordinal(), ContainerStargateBase.class);
		RegistrationHelper.registerContainer(LanteaCraft.EnumGUIs.StargateDHDEnergy.ordinal(),
				ContainerStargateDHDEnergy.class);

		if (event.getSide() == Side.CLIENT) {
			Render.modelController = new ModelStargateDHD(LanteaCraft.getResource("models/dhd.obj"));

			Render.modelRingPlatformBase = new ModelRingPlatformBase(
					LanteaCraft.getResource("models/transport_rings_base.obj"));
			Render.modelRingPlatformRing = new ModelRingPlatformRing(
					LanteaCraft.getResource("models/transport_rings.obj"));

			Render.tileEntityBaseRenderer = new TileStargateBaseRenderer();
			RegistrationHelper.addTileEntityRenderer(TileStargateBase.class, Render.tileEntityBaseRenderer);

			Render.tileEntityControllerRenderer = new TileStargateDHDRenderer();
			RegistrationHelper.addTileEntityRenderer(TileStargateDHD.class,
					Render.tileEntityControllerRenderer);

			Render.tileEntityRingPlatformRenderer = new TileTransporterRingRenderer();
			RegistrationHelper.addTileEntityRenderer(TileTransporterRing.class,
					Render.tileEntityRingPlatformRenderer);

			Render.blockStargateBaseRenderer = new BlockStargateBaseRenderer();
			RegistrationHelper.registerRenderer(Render.blockStargateBaseRenderer);

			Render.blockStargateRingRenderer = new BlockStargateRingRenderer();
			RegistrationHelper.registerRenderer(Render.blockStargateRingRenderer);

			Render.blockControllerRenderer = new BlockStargateDHDRenderer();
			RegistrationHelper.registerRenderer(Render.blockControllerRenderer);

			Render.blockTransporterRingRenderer = new BlockTransporterRingRenderer();
			RegistrationHelper.registerRenderer(Render.blockTransporterRingRenderer);

			Render.heldItemRenderer = new ItemHeldRenderer();
			MinecraftForgeClient.registerItemRenderer(Items.gdo, Render.heldItemRenderer);
		}

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
