package lc.common.impl.drivers;

import net.minecraft.world.World;
import li.cil.oc.api.driver.Block;
import li.cil.oc.api.network.ManagedEnvironment;

public class OpenComputersDriverManager implements Block {

	public static interface IHookManagedEnvironment extends ManagedEnvironment {
		public String getComponentName();
	}

	public OpenComputersDriverManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

}
