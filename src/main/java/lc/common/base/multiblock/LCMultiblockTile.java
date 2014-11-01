package lc.common.base.multiblock;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCMultiblockPacket;
import lc.common.network.packets.LCTileSync;
import lc.common.util.math.DimensionPos;
import lc.common.util.math.Vector3;
import lc.core.LCRuntime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class LCMultiblockTile extends LCTile {

	private NBTTagCompound multiblockCompound = new NBTTagCompound();
	private boolean multiblockNbtDirty = false;
	private boolean isSlave = false;

	public void setSlave(boolean state) {
		isSlave = state;
	}

	public boolean isSlave() {
		return isSlave;
	}

	/**
	 * Change the multi-block state.
	 * 
	 * @param next
	 *            The state to transition to.
	 */
	protected void changeState(MultiblockState next) {
		if (isSlave)
			LCLog.fatal(new OperationNotSupportedException("Not allowed to changeState on a slave."));
		else {
			if (multiblockCompound.hasKey("state")
					&& MultiblockState.fromOrdinal(multiblockCompound.getInteger("state")) == next)
				return;
			multiblockCompound.setInteger("state_next", next.ordinal());
		}
	}

	public void setOwner(Vector3 owner) {
		if (!isSlave)
			LCLog.fatal(new OperationNotSupportedException("Not allowed to setOwner on a master."));
		else {
			if (owner == null)
				multiblockCompound.setTag("owner", null);
			else
				multiblockCompound.setTag("owner", owner.toNBT());
			markMultiblockDirty();
		}
	}

	public MultiblockState getState() {
		if (isSlave) {
			if (!multiblockCompound.hasKey("owner"))
				return MultiblockState.NONE;
			Vector3 owner = Vector3.from(multiblockCompound.getCompoundTag("owner"));
			TileEntity tile = worldObj.getTileEntity(owner.floorX(), owner.floorY(), owner.floorZ());
			if (!(tile instanceof LCMultiblockTile))
				return MultiblockState.NONE;
			return ((LCMultiblockTile) tile).getState();
		} else {
			if (!multiblockCompound.hasKey("state"))
				return MultiblockState.NONE;
			return MultiblockState.fromOrdinal(multiblockCompound.getInteger("state"));
		}
	}

	public MultiblockState nextState() {
		if (!multiblockCompound.hasKey("state_next"))
			return null;
		return MultiblockState.fromOrdinal(multiblockCompound.getInteger("state_next"));
	}

	public void markMultiblockDirty() {
		multiblockNbtDirty = true;
	}

	/**
	 * Get the structure configuration for this multiblock.
	 * 
	 * @return The structure configuration instance.
	 */
	public abstract StructureConfiguration getConfiguration();

	/** Called on the server to perform multiblock logic update */
	public abstract void thinkMultiblock();

	@Override
	public void thinkServerPost() {
		super.thinkServerPost();
		thinkMultiblock();
		MultiblockState next = nextState();
		if (next != null && next != getState()) {
			multiblockCompound.setInteger("state", next.ordinal());
			multiblockNbtDirty = true;
		}

		if (multiblockNbtDirty) {
			multiblockNbtDirty = false;
			LCMultiblockPacket update = new LCMultiblockPacket(new DimensionPos(this), multiblockCompound);
			LCRuntime.runtime.network().sendToAllAround(update, update.target, 128.0d);
		}
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		if (packet instanceof LCMultiblockPacket) {
			if (worldObj.isRemote) {
				multiblockCompound = ((LCMultiblockPacket) packet).compound;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public void sendPackets(List<LCPacket> packets) throws LCNetworkException {
		super.sendPackets(packets);
		packets.add(new LCMultiblockPacket(new DimensionPos(this), multiblockCompound));
	}

	@Override
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

}
