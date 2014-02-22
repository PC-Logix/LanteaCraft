package pcl.lc.tileentity;

import pcl.lc.api.EnumRingPlatformState;
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

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 4, yCoord, zCoord - 4, xCoord + 4, yCoord + 7, zCoord + 4);
	}

}
