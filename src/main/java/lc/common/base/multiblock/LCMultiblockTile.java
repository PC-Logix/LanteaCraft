package lc.common.base.multiblock;

import java.util.List;

import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCMultiblockPacket;
import lc.common.network.packets.LCTileSync;
import lc.common.util.math.DimensionPos;
import lc.core.LCRuntime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class LCMultiblockTile extends LCTile {

	private NBTTagCompound multiblockCompound = new NBTTagCompound();
	private boolean multiblockNbtDirty = false;

	protected void changeState(MultiblockState next) {
		if (multiblockCompound.hasKey("state")
				&& MultiblockState.fromOrdinal(multiblockCompound.getInteger("state")) == next)
			return;
		multiblockCompound.setInteger("state_next", next.ordinal());
	}

	public MultiblockState getState() {
		if (!multiblockCompound.hasKey("state"))
			return MultiblockState.NONE;
		return MultiblockState.fromOrdinal(multiblockCompound.getInteger("state"));
	}

	public MultiblockState nextState() {
		if (!multiblockCompound.hasKey("state_next"))
			return null;
		return MultiblockState.fromOrdinal(multiblockCompound.getInteger("state_next"));
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
