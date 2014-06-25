package net.afterlifelochie.sandbox;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import pcl.common.network.ModPacket;
import pcl.common.util.WorldLocation;

/**
 * Packet to calculate and send Observable state changes.
 * 
 * @author AfterLifeLochie
 */
public class DiffModPacket extends ModPacket {

	private enum EnumOperation {
		ADD, REMOVE, MODIFY;
	}

	public static DiffModPacket createPacket(DataInputStream data) throws IOException {
		DiffModPacket pkt = new DiffModPacket();
		pkt.unpack(data);
		return pkt;
	}

	public static DiffModPacket createPacket(WatchedList<?, ?> list) {
		DiffModPacket pkt = new DiffModPacket();
		pkt.pack(list);
		return pkt;
	}

	private HashMap<String, Object> metadata;
	private volatile boolean forServer;

	private void unpack(DataInputStream data) {
		// TODO Auto-generated method stub
	}

	private void pack(WatchedList<?, ?> list) {
		// TODO Auto-generated method stub
	}

	public void setMetadata(String key, Object value) {
		metadata.put(key, value);
	}

	public Object getMetadata(String key) {
		return metadata.get(key);
	}

	public boolean hasMetadata() {
		return metadata.size() > 0;
	}

	public boolean hasMetadataField(String key) {
		return metadata.containsKey(key);
	}

	@Override
	public boolean getPacketIsForServer() {
		return forServer;
	}

	@Override
	public String getType() {
		return "DiffModPacket";
	}

	@Override
	public WorldLocation getOriginLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		// TODO Auto-generated method stub

	}

}
