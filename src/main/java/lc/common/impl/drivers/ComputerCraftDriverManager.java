package lc.common.impl.drivers;

import java.util.Enumeration;

import lc.common.LCLog;
import lc.common.base.LCTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

/**
 * ComputerCraft driver manager. Guess what - the API is still a pain.
 * 
 * @author AfterLifeLochie
 *
 */
public class ComputerCraftDriverManager {

	private final IPeripheralProvider provider = new IPeripheralProvider() {
		@Override
		public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
			TileEntity whatTile = world.getTileEntity(x, y, z);
			if (whatTile == null)
				return null;
			if (!(whatTile instanceof LCTile))
				return null;
			if (!(whatTile instanceof IPeripheral))
				return null;
			return (IPeripheral) whatTile;
		}
	};

	public ComputerCraftDriverManager() {
		LCLog.debug("ComputerCraft driver manager initialized.");
		ComputerCraftAPI.registerPeripheralProvider(provider);
	}

	public static Object performCastToType(Object xx, Class<?> yy) throws Exception {
		LCLog.debug("ComputerCraft driver: perform cast: %s to %s", xx.getClass().getName(), yy.getName());
		if (yy == Character.class || yy == char.class) {
			if (!(xx instanceof String))
				throw new Exception("Cannot cast " + xx.getClass().getSimpleName() + " to Character");
			String xxx = (String) xx;
			if (xxx.length() != 1)
				throw new Exception("Illegal Character length.");
			return xxx.charAt(0);
		}
		return xx;
	}

	public static Object castToComputerSafe(Object aresult) {
		if (aresult instanceof Enumeration) {
			return ((Enumeration) aresult).toString();
		}
		return aresult;
	}

}
