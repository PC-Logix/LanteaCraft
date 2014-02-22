package pcl.lc.tileentity;

import java.util.logging.Level;

import pcl.common.base.GenericTileEntity;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumRingPlatformState;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityRingPlatform extends GenericTileEntity {

	private final double ringExtended = 2.5d;

	private EnumRingPlatformState state = EnumRingPlatformState.Idle;
	private int timeout;
	private boolean slave;

	private double ringPosition, lastRingPosition, nextRingPosition;

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
		lastRingPosition = ringPosition;
		if (0 > ringPosition)
			ringPosition = 0;
		ringPosition += nextRingPosition;
		if (ringPosition > ringExtended)
			ringPosition = ringExtended;
		if (timeout > 0) {
			if (state == EnumRingPlatformState.Connecting)
				nextRingPosition = (ringExtended / 20.0d);
			else if (state == EnumRingPlatformState.Disconnecting)
				nextRingPosition = -(ringExtended / 20.0d);
			else
				nextRingPosition = 0;
		}
	}

	public double getRingPosition(float partialticks) {
		double next = ringPosition + partialticks * nextRingPosition;
		if (next > ringExtended)
			return ringExtended;
		return next;
	}

	private void updateState(EnumRingPlatformState state, int timeout) {
		this.state = state;
		this.timeout = timeout;
		markBlockForUpdate();
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
