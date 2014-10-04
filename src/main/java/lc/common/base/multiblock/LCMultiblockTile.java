package lc.common.base.multiblock;

import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class LCMultiblockTile extends LCTile {

	private MultiblockState state;
	private MultiblockState state_next;

	protected void changeState(MultiblockState next) {
		if (state != next)
			state_next = next;
	}

	public MultiblockState getState() {
		return state;
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
		if (state_next != state) {
			// TODO: Send update state packet
		}
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
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

}
