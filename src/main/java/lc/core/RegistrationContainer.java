package lc.core;

import lc.api.defs.Blocks;
import lc.blocks.BlockTest;
import lc.common.impl.DefinitionWrapperProvider;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class RegistrationContainer {

	public void preinit(LCRuntime runtime, FMLPreInitializationEvent event) {
		Blocks blocks = runtime.blocks();
		blocks.testBlock = DefinitionWrapperProvider.provide(BlockTest.class);
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		
	}

	public void postinit(LCRuntime runtime, FMLPostInitializationEvent event) {

	}

}
