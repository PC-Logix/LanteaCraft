package lc.common.impl.drivers;

import lc.common.LCLog;
import lc.common.base.LCTile;
import li.cil.oc.api.driver.Block;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.ManagedPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class OpenComputersDriverManager implements Block {

	public static interface IOCManagedEnvPerp extends ManagedEnvironment, ManagedPeripheral {
		public String getComponentName();
	}

	public OpenComputersDriverManager() {
	}

	@Override
	public boolean worksWith(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null)
			return false;
		if (!(tile instanceof LCTile))
			return false;
		if (!(tile instanceof IOCManagedEnvPerp))
			return false;
		return true;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		return (IOCManagedEnvPerp) tile;
	}

	public static Object performCastToType(Object xx, Class<?> yy) throws Exception {
		LCLog.debug("ComputerCraft driver: perform cast: %s to %s", xx.getClass().getName(), yy.getName());
		if (yy == Character.class || yy == char.class) {
			if (!(xx instanceof byte[]))
				throw new Exception("Cannot cast " + xx.getClass().getSimpleName() + " to Character");
			byte[] xxx = (byte[]) xx;
			if (xxx.length != 1)
				throw new Exception("Illegal Character length.");
			return (char) xxx[0];
		}
		if (yy == String.class) {
			if (!(xx instanceof byte[]))
				throw new Exception("Cannot cast " + xx.getClass().getSimpleName() + " to String");
			byte[] xxx = (byte[]) xx;
			StringBuilder m = new StringBuilder();
			for (int i = 0; i < xxx.length; i++)
				m.append(xxx[i]);
			return m.toString();
		}
		return xx;
	}

	public static String findComponentName(String rzc) {
		rzc = rzc.replace("Tile", "").replace("tile", "");
		return rzc.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

}
