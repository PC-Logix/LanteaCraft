package pcl.common.asm;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

/**
 * Forge expects us to return a ModContainer when loading core-mod code. This is
 * it.
 * 
 * @author AfterLifeLochie
 */
public class PCLCoreModContainer extends DummyModContainer {
	public PCLCoreModContainer() {
		super(new ModMetadata());

		ModMetadata md = getMetadata();
		md.modId = "PCLCoreModASM";
		md.name = "PCLCoreModASM";
		md.version = "1.0";
		md.description = "PCL CoreMod Factory";
		md.parent = "PCL CoreMod Factory";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}
}
