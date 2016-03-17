package lc.common.impl.drivers;

import lc.common.base.LCTile;
import lc.digital.vm.peripheral.ILCPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * LanteaCraft driver manager.
 * 
 * @author AfterLifeLochie
 *
 */
public class LanteaCraftDriverManager {

	public ILCPeripheral getPeripheral(World world, int x, int y, int z) {
		TileEntity whatTile = world.getTileEntity(x, y, z);
		if (whatTile == null)
			return null;
		if (!(whatTile instanceof LCTile))
			return null;
		if (!(whatTile instanceof ILCPeripheral))
			return null;
		return (ILCPeripheral) whatTile;
	}

}
