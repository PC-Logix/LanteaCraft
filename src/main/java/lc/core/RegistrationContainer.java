package lc.core;

import lc.api.defs.Blocks;
import lc.api.defs.Items;
import lc.blocks.BlockLanteaOre;
import lc.blocks.BlockStargateBase;
import lc.blocks.BlockStargateRing;
import lc.common.impl.registry.DefinitionWrapperProvider;
import lc.items.ItemLanteaOre;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class RegistrationContainer {

	public void preinit(LCRuntime runtime, FMLPreInitializationEvent event) {
		Blocks blocks = runtime.blocks();
		Items items = runtime.items();

		blocks.stargateRingBlock = DefinitionWrapperProvider.provide(BlockStargateRing.class);
		blocks.stargateBaseBlock = DefinitionWrapperProvider.provide(BlockStargateBase.class);
		blocks.lanteaOreBlock = DefinitionWrapperProvider.provide(BlockLanteaOre.class);
		
		items.lanteaOreItem = DefinitionWrapperProvider.provide(ItemLanteaOre.class);
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {

	}

	public void postinit(LCRuntime runtime, FMLPostInitializationEvent event) {

	}

}
