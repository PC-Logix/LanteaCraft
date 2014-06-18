package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.api.internal.IModule;
import pcl.lc.blocks.BlockTransporterRing;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.containers.ContainerStargateBase;
import pcl.lc.containers.ContainerStargateController;
import pcl.lc.containers.ContainerStargateControllerEnergy;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.items.ItemControllerCrystal;
import pcl.lc.items.ItemCoreCrystal;
import pcl.lc.items.ItemGDO;
import pcl.lc.items.ItemIris;
import pcl.lc.items.ItemStargateRing;
import pcl.lc.items.ItemTransportRingActivator;
import pcl.lc.items.ItemTransporterRing;
import pcl.lc.tileentity.TileEntityTransporterRing;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModuleStargates implements IModule {

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
	public void preInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
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
		LanteaCraft.getProxy().addContainer(LanteaCraft.EnumGUIs.StargateController.ordinal(),
				ContainerStargateController.class);
		LanteaCraft.getProxy().addContainer(LanteaCraft.EnumGUIs.StargateControllerEnergy.ordinal(),
				ContainerStargateControllerEnergy.class);
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
