package lc.common.network.packets.abs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.util.math.DimensionPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

/**
 * Contract class for packets with specific targets in a world.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCTargetPacket extends LCPacket {

	/**
	 * Handle an incoming packet
	 *
	 * @param packet
	 *            The packet
	 * @param player
	 *            The player
	 * @throws LCNetworkException
	 *             Any handling exceptions
	 */
	public static void handlePacket(LCTargetPacket packet, EntityPlayer player) throws LCNetworkException {
		if (player.worldObj == null || player.worldObj.provider == null)
			return;
		if (packet.target.dimension != player.worldObj.provider.getDimensionId())
			return;
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(packet.target.x, packet.target.y, packet.target.z));
		if (tile != null && tile instanceof LCTile) {
			LCTile theTile = (LCTile) tile;
			theTile.handlePacket(packet, player);
		} else
			throw new LCNetworkException("Invalid target tile specified.");
	}

	/** The dimension target */
	public DimensionPos target;

	/**
	 * Read a dimension from the input buffer at the current read pointer.
	 *
	 * @param buffer
	 *            The buffer
	 * @return A dimension target
	 * @throws IOException
	 *             Any I/O problem
	 */
	public DimensionPos readDimensionPosFromBuffer(ByteBuf buffer) throws IOException {
		return new DimensionPos(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	/**
	 * Write a dimension to the output buffer at the current write pointer.
	 *
	 * @param buffer
	 *            The buffer
	 * @param target
	 *            A dimension target
	 * @throws IOException
	 *             Any I/O problem
	 */
	public void writeDimensionPosToBuffer(ByteBuf buffer, DimensionPos target) throws IOException {
		buffer.writeInt(target.dimension).writeInt(target.x).writeInt(target.y).writeInt(target.z);
	}

}
