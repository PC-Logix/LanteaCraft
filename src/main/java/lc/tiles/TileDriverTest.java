package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import lc.api.components.IntegrationType;
import lc.common.base.LCTile;
import lc.common.network.LCPacket;
import lc.api.drivers.DeviceDrivers;
import lc.api.drivers.IPowerDriver;

@DeviceDrivers.DriverCandidate(types = { IntegrationType.POWER, IntegrationType.POWER })
public class TileDriverTest extends LCTile implements IPowerDriver {

	@Override
	public void handlePacket(LCPacket packetOf, EntityPlayer player) {
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
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canInterface(ForgeDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getStoredEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEnergyCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double acceptEnergy(double quantity, boolean simulated) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double pullEnergy(double quantity, boolean simulated) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

}
