package lc.common.impl.drivers;

import java.util.Enumeration;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import lc.common.LCLog;
import lc.common.base.LCTile;
import li.cil.oc.api.driver.Block;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.ManagedPeripheral;

public class OpenComputersDriverManager implements Block {

	public static interface IOCManagedEnvPerp extends ManagedEnvironment, ManagedPeripheral {
		public String getComponentName();
	}

	public OpenComputersDriverManager() {
	}

	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null)
			return false;
		if (!(tile instanceof LCTile))
			return false;
		if (!(tile instanceof IOCManagedEnvPerp))
			return false;
		return true;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return (IOCManagedEnvPerp) tile;
	}

	public static String findComponentName(String rzc) {
		rzc = rzc.replace("Tile", "").replace("tile", "");
		return rzc.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

}
