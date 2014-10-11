package lc.tiles;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import lc.api.components.IntegrationType;
import lc.api.drivers.DeviceDrivers.DriverCandidate;
import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;

/**
 * Stargate Ring tile implementation.
 * 
 * @author AfterLifeLochie
 * 
 */
@DriverCandidate(types = { IntegrationType.POWER })
public class TileStargateRing extends LCTile {

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	public LCPacket[] sendPackets() throws LCNetworkException {
		return null;
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
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] debug(Side side) {
		// TODO Auto-generated method stub
		return null;
	}

}
