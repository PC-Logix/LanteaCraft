package pcl.lc.tileentity;

import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumRingPlatformState;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityRingPlatform extends TileEntity {

	private EnumRingPlatformState state;
	private int timeout;
	private boolean slave;

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			updateRendering();
		else {
			if (state != EnumRingPlatformState.Idle || timeout != 0) {
				if (timeout > 0)
					timeout--;
				else {
					if (state == EnumRingPlatformState.Connecting) {
						if (slave)
							updateState(EnumRingPlatformState.Recieveing, 10);
						else
							updateState(EnumRingPlatformState.Transmitting, 10);
					} else if (state == EnumRingPlatformState.Recieveing || state == EnumRingPlatformState.Transmitting) {
						updateState(EnumRingPlatformState.Disconnecting, 20);
					} else if (state == EnumRingPlatformState.Disconnecting) {
						updateState(EnumRingPlatformState.Idle, 0);
					}
				}
			}
		}
	}

	private void updateRendering() {
		// TODO Auto-generated method stub

	}

	private void updateState(EnumRingPlatformState state, int timeout) {
		this.state = state;
		this.timeout = timeout;
	}

	public void getStateFromPacket(ModPacket packet) {
		StandardModPacket packetOf = (StandardModPacket) packet;
		this.timeout = (Integer) packetOf.getValue("timeout");
		this.state = (EnumRingPlatformState) packetOf.getValue("state");
	}

	public ModPacket getPacketFromState() {
		StandardModPacket packet = new StandardModPacket();
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.TileUpdate");
		packet.setValue("DimensionID", worldObj.provider.dimensionId);
		packet.setValue("WorldX", xCoord);
		packet.setValue("WorldY", yCoord);
		packet.setValue("WorldZ", zCoord);

		packet.setValue("timeout", timeout);
		packet.setValue("state", state);
		return packet;
	}

	@Override
	public Packet getDescriptionPacket() {
		LanteaCraft.getProxy().sendToAllPlayers(getPacketFromState());
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 4, yCoord, zCoord - 4, xCoord + 4, yCoord + 7, zCoord + 4);
	}

}
