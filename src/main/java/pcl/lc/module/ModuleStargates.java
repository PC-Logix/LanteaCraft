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
import pcl.lc.blocks.BlockRingPlatform;
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
import pcl.lc.tileentity.TileEntityRingPlatform;
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

		Blocks.ringPlatform = RegistrationHelper.registerBlock(BlockRingPlatform.class, "ringPlatform");

		GameRegistry.registerTileEntity(TileEntityStargateBase.class, "tileEntityStargateBase");
		GameRegistry.registerTileEntity(TileEntityStargateRing.class, "tileEntityStargateRing");
		GameRegistry.registerTileEntity(TileEntityStargateController.class, "tileEntityStargateController");
		GameRegistry.registerTileEntity(TileEntityRingPlatform.class, "tileEntityRingPlatform");

		Items.coreCrystal = RegistrationHelper.registerItem(ItemCoreCrystal.class, "coreCrystal");
		Items.controllerCrystal = RegistrationHelper.registerItem(ItemControllerCrystal.class, "controllerCrystal");
		Items.iris = RegistrationHelper.registerItem(ItemIris.class, "iris");
		Items.gdo = RegistrationHelper.registerItem(ItemGDO.class, "gdo");

		Items.transportRingActivator = RegistrationHelper.registerItem(ItemTransportRingActivator.class,
				"transportRingActivator");

		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateRingBlock, 1), "ICI", "NNN", "III", 'I',
				Item.ingotIron, 'N', new ItemStack(Items.lanteaOreIngot), 'C', new ItemStack(Block.sandStone, 1, 1));
		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateRingBlock, 1, 1), "CgC", "NpN", "IrI", 'I',
				Item.ingotIron, 'N', new ItemStack(Items.lanteaOreIngot), 'C', new ItemStack(Block.sandStone, 1, 1), 'g',
				Item.glowstone, 'r', Item.redstone, 'p', Item.enderPearl);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateBaseBlock, 1), "CrC", "NeN", "IcI", 'I',
				Item.ingotIron, 'N', new ItemStack(Items.lanteaOreIngot), 'C', new ItemStack(Block.sandStone, 1, 1), 'r',
				Item.redstone, 'e', Item.eyeOfEnder, 'c', Items.coreCrystal);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateControllerBlock, 1), "bbb", "OpO", "OcO", 'b',
				Block.stoneButton, 'O', Block.obsidian, 'p', Item.enderPearl, 'r', Item.redstone, 'c',
				Items.controllerCrystal);

		RegistrationHelper.newRecipe(new ItemStack(Items.coreCrystal, 1), "bbr", "rdb", "brb", 'b', new ItemStack(
				Item.dyePowder, 1, 4), 'r', Item.redstone, 'd', Item.diamond);
		RegistrationHelper.newRecipe(new ItemStack(Items.controllerCrystal, 1), "roo", "odr", "oor", 'o',
				new ItemStack(Item.dyePowder, 1, 14), 'r', Item.redstone, 'd', Item.diamond);

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
