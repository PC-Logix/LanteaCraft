package lc.tiles;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import lc.api.components.IntegrationType;
import lc.api.drivers.DeviceDrivers.DriverCandidate;
import lc.common.base.LCTile;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;

/**
 * Stargate Ring tile implementation.
 * 
 * @author AfterLifeLochie
 * 
 */
@DriverCandidate(types = { IntegrationType.POWER })
public class TileStargateRing extends LCMultiblockTile {

	public TileStargateRing() {
		setSlave(true);
	}

	@Override
	public StructureConfiguration getConfiguration() {
		return null;
	}

	@Override
	public void thinkMultiblock() {
		// TODO Auto-generated method stub

	}

	@Override
	public IInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void thinkClient() {
		// TODO Auto-generated method stub

	}

	@Override
	public void thinkServer() {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] debug(Side side) {
		return new String[] { String.format("Multiblock: %s", getState()) };
	}

}
