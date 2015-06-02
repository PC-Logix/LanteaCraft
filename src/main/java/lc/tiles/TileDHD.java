package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.api.stargate.IDHDAccess;
import lc.api.stargate.StargateType;
import lc.blocks.BlockDHD;
import lc.common.base.LCTile;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCDHDPacket;
import lc.common.util.math.DimensionPos;
import lc.server.StargateConnection;

public class TileDHD extends LCTile implements IDHDAccess {

	private StargateConnection ownedConnection;

	@Override
	public void configure(ComponentConfig c) {
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
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		if (packet instanceof LCDHDPacket) {
			// TODO: Handle a client -> server DHD task packet
		}
	}

	@Override
	public boolean shouldRender() {
		return true;
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
		return new String[] { "ownsConnection: " + ((ownsConnection()) ? "yes" : "no") };
	}

	@Override
	public StargateType getDHDType() {
		return ((BlockDHD) getBlockType()).getDHDType(getBlockMetadata());
	}

	@Override
	public boolean ownsConnection() {
		if (ownedConnection == null)
			return false;
		if (ownedConnection.dead)
			return false;
		return true;
	}

	public void clientDoOpenConnection(String typedAddress) {
		NBTTagCompound request = new NBTTagCompound();
		request.setString("task", "openConnection");
		request.setString("typedAddress", typedAddress);
		LCDHDPacket packet = new LCDHDPacket(new DimensionPos(this), request);
		LCRuntime.runtime.network().sendToServer(packet);
	}

	public void clientDoHangUp() {
		NBTTagCompound request = new NBTTagCompound();
		request.setString("task", "hangUp");
		LCDHDPacket packet = new LCDHDPacket(new DimensionPos(this), request);
		LCRuntime.runtime.network().sendToServer(packet);
	}

}
