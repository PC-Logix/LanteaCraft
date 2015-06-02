package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.api.stargate.IDHDAccess;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateType;
import lc.blocks.BlockDHD;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCDHDPacket;
import lc.common.util.ScanningHelper;
import lc.common.util.math.DimensionPos;
import lc.server.HintProviderServer;
import lc.server.StargateConnection;
import lc.server.StargateManager;

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
		if (ownedConnection != null && ownedConnection.dead)
			ownedConnection = null;
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		if (packet instanceof LCDHDPacket) {
			LCDHDPacket request = (LCDHDPacket) packet;
			if (request.compound.getString("task").equals("openConnection")) {
				if (!ownsConnection()) {
					AxisAlignedBB box = AxisAlignedBB.getBoundingBox(-5, -5, -5, 5, 5, 5);
					TileEntity tile = ScanningHelper.findNearestTileEntityOf(getWorldObj(), TileStargateBase.class,
							xCoord, yCoord, zCoord, box);
					if (tile == null || !(tile instanceof TileStargateBase))
						return;
					TileStargateBase sg = (TileStargateBase) tile;
					HintProviderServer server = (HintProviderServer) LCRuntime.runtime.hints();
					String typedAddress = request.compound.getString("typedAddress");
					StargateAddress address = new StargateAddress(typedAddress.toCharArray());
					ownedConnection = server.stargates().openConnection(sg, address);
				}
			} else if (request.compound.getString("task").equals("hangUp")) {
				if (ownsConnection())
					ownedConnection.closeConnection();
			} else {
				LCLog.warn("Bad LCDHDPacket from client, unknown task requested.");
			}
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
