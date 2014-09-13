package lc.common.network.packets.abs;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.util.math.DimensionPos;

public abstract class LCTargetPacket extends LCPacket {

	public static void handlePacket(LCTargetPacket packet, EntityPlayer player) throws LCNetworkException {
		if (player.worldObj == null || player.worldObj.provider == null)
			return;
		if (packet.target.dimension != player.worldObj.provider.dimensionId)
			return;
		TileEntity tile = player.worldObj.getTileEntity(packet.target.x, packet.target.y, packet.target.z);
		if (tile != null && tile instanceof LCTile) {
			LCTile theTile = (LCTile) tile;
			theTile.handlePacket(packet, player);
		} else
			throw new LCNetworkException("Invalid target tile specified.");
	}

	public DimensionPos target;

	public DimensionPos readDimensionPosFromBuffer(ByteBuf buffer) throws IOException {
		return new DimensionPos(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	public void writeDimensionPosToBuffer(ByteBuf buffer, DimensionPos target) throws IOException {
		buffer.writeInt(target.dimension).writeInt(target.x).writeInt(target.y).writeInt(target.z);
	}

}
