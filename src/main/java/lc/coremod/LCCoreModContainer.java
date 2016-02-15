package lc.coremod;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import com.google.common.eventbus.EventBus;

/**
 * Forge expects us to return a ModContainer when loading core-mod code. This is
 * it.
 *
 * @author AfterLifeLochie
 */
public class LCCoreModContainer extends DummyModContainer {

	/** General constructor */
	public LCCoreModContainer() {
		super(new ModMetadata());
		ModMetadata md = getMetadata();
		md.modId = "LanteaCraft-Core";
		md.name = "LanteaCraft Core";
		md.version = "1.0";
		md.description = "LanteaCraft Core";
		md.parent = "LanteaCraft Core";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}
}
