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

}
