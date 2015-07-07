package lc.common.base.multiblock;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import lc.LCRuntime;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCMultiblockPacket;
import lc.common.util.Tracer;
import lc.common.util.math.DimensionPos;
import lc.common.util.math.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Internal multi-block implementation.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCMultiblockTile extends LCTile {

	/**
	 * The multi-block metadata compound. If you change this, you should call
	 * {@link LCMultiblockTile#markMultiblockDirty()} to send the change to all
	 * clients within range.
	 */
	private NBTTagCompound multiblockCompound = new NBTTagCompound();
	private boolean multiblockNbtDirty = false;
	private boolean isSlave = false;

	/**
	 * Set this multi-block to a slave state
	 *
	 * @param state
	 *            The state to set
	 */
	public void setSlave(boolean state) {
		isSlave = state;
	}

	/**
	 * Get this multi-block's slave state
	 *
	 * @return The state
	 */
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

	/**
	 * Set the owner location of this block
	 *
	 * @param owner
	 *            The owner location
	 */
	public void setOwner(Vector3 owner) {
		if (!isSlave)
			LCLog.fatal(new OperationNotSupportedException("Not allowed to setOwner on a master."));
		else {
			if (owner == null)
				multiblockCompound.removeTag("owner");
			else
				multiblockCompound.setTag("owner", owner.toNBT());
			markMultiblockDirty();
		}
	}

	/**
	 * Get the state of this multi-block
	 *
	 * @return The state of this multi-block
	 */
	public MultiblockState getState() {
		if (isSlave) {
			if (!multiblockCompound.hasKey("owner"))
				return MultiblockState.NONE;
			Vector3 owner = Vector3.from(multiblockCompound.getCompoundTag("owner"));
			TileEntity tile = worldObj.getTileEntity(owner.fx(), owner.fy(), owner.fz());
			if (!(tile instanceof LCMultiblockTile))
				return MultiblockState.NONE;
			return ((LCMultiblockTile) tile).getState();
		} else {
			if (!multiblockCompound.hasKey("state"))
				return MultiblockState.NONE;
			return MultiblockState.fromOrdinal(multiblockCompound.getInteger("state"));
		}
	}

	/**
	 * Get the next-state of the multi-block.
	 *
	 * @return The next state.
	 */
	public MultiblockState nextState() {
		if (!multiblockCompound.hasKey("state_next"))
			return null;
		return MultiblockState.fromOrdinal(multiblockCompound.getInteger("state_next"));
	}

	/**
	 * Mark the multi-block dirty.
	 */
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
		Tracer.begin(this);
		thinkMultiblock();
		MultiblockState next = nextState();
		if (next != null && next != getState()) {
			multiblockCompound.setInteger("state", next.ordinal());
			multiblockNbtDirty = true;
		}

		if (multiblockNbtDirty) {
			multiblockNbtDirty = false;
			LCMultiblockPacket update = new LCMultiblockPacket(new DimensionPos(this), multiblockCompound);
			LCRuntime.runtime.network().getPreferredPipe().sendToAllAround(update, update.target, 128.0d);
		}
		Tracer.end();
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		Tracer.begin(this);
		if (packet instanceof LCMultiblockPacket)
			if (worldObj.isRemote) {
				multiblockCompound = ((LCMultiblockPacket) packet).compound;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		Tracer.end();
	}

	@Override
	public void sendPackets(List<LCPacket> packets) throws LCNetworkException {
		super.sendPackets(packets);
		Tracer.begin(this);
		packets.add(new LCMultiblockPacket(new DimensionPos(this), multiblockCompound));
		Tracer.end();
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
