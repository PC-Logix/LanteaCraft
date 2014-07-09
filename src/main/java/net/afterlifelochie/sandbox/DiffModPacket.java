package net.afterlifelochie.sandbox;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import pcl.common.util.WorldLocation;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.base.network.IStreamPackable;
import pcl.lc.base.network.ModPacket;

/**
 * Packet to calculate and send Observable state changes.
 * 
 * @author AfterLifeLochie
 */
public class DiffModPacket extends ModPacket {
	private HashMap<Object, Object> valueMap;
	private ArrayList<Object> key_add;
	private ArrayList<Object> key_remove;
	private ArrayList<Object> key_modified;
	private WorldLocation origin;
	private volatile boolean forServer;

	public DiffModPacket() {
		valueMap = new HashMap<Object, Object>();
		key_add = new ArrayList<Object>();
		key_remove = new ArrayList<Object>();
		key_modified = new ArrayList<Object>();
	}

	public DiffModPacket(WorldLocation location, WatchedList<String, Object> alist) {
		origin = location;
		valueMap = new HashMap<Object, Object>();
		key_add = (ArrayList<Object>) alist.added().clone();
		key_remove = (ArrayList<Object>) alist.removed().clone();
		key_modified = (ArrayList<Object>) alist.modified().clone();

		for (Object added : key_add)
			valueMap.put(added, alist.get((String) added));
		for (Object modified : key_modified)
			valueMap.put(modified, alist.get((String) modified));
		for (Object deleted : key_remove)
			if (valueMap.containsKey((String) deleted))
				valueMap.remove((String) deleted);
	}

	public void apply(WatchedList<String, Object> alist) {
		for (Object added : key_add)
			alist.set((String) added, valueMap.get((String) added));
		for (Object modified : key_modified)
			alist.set((String) modified, valueMap.get((String) modified));
		for (Object deleted : key_remove)
			alist.remove((String) deleted);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void writeValue(Object o, DataOutputStream data) throws IOException {
		int intValueOf = ModPacket.getGenericID(o.getClass());
		if (intValueOf == -1) {
			IStreamPackable packer = null;
			if (o instanceof IStreamPackable)
				packer = (IStreamPackable<?>) o;
			if (packer == null)
				packer = ModPacket.findPacker(o.getClass());
			if (packer != null) {
				data.writeInt(255);
				data.writeInt(packer.getTypeOf());
				if (BuildInfo.NET_DEBUGGING)
					LanteaCraft.getLogger().log(Level.INFO,
							String.format("Packing complex of type %s.", packer.getTypeOf()));
				packer.pack(o, data);
			} else {
				if (BuildInfo.NET_DEBUGGING)
					LanteaCraft.getLogger().log(Level.WARN, String.format("Cannot pack %s!", o.getClass().getName()));
				throw new IOException("Cannot pack " + o.getClass().getName() + "; unknown value.");
			}
		} else {
			if (BuildInfo.NET_DEBUGGING)
				LanteaCraft.getLogger().log(Level.INFO, String.format("Packing primitive of type %s.", intValueOf));
			data.writeInt(intValueOf);
			if (intValueOf != -1)
				switch (intValueOf) {
				case 0:
				case 1:
					data.writeInt((Integer) o);
					break;
				case 2:
				case 3:
					data.writeByte((Boolean) o ? 1 : 0);
					break;
				case 4:
				case 5:
					data.writeDouble((Double) o);
					break;
				case 6:
				case 7:
					data.writeFloat((Float) o);
					break;
				case 8:
				case 9:
					data.writeChar((Character) o);
					break;
				case 10:
					writeString((String) o, data);
					break;
				case 11:
					writeArrayList((ArrayList<?>) o, data);
					break;
				case 12:
					writeHashMap((HashMap<?, ?>) o, data);
					break;
				default:
					throw new IOException("Don't know what to do with typeof " + intValueOf);
				}
		}
	}

	public static Object readValue(DataInputStream data) throws IOException {
		int typeAsInt = data.readInt();
		if (BuildInfo.NET_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("Unpacking primitive of type %s.", typeAsInt));
		if (typeAsInt == -1)
			return null;
		else {
			Class<?> classValueOf = getGeneric(typeAsInt);
			if (classValueOf == null)
				if (typeAsInt == 255) {
					int packerTypeAsInt = data.readInt();
					if (BuildInfo.NET_DEBUGGING)
						LanteaCraft.getLogger().log(Level.INFO,
								String.format("Unpacking complex of type %s.", packerTypeAsInt));
					IStreamPackable<?> packer = ModPacket.findPacker(packerTypeAsInt);
					if (packer != null)
						return packer.unpack(data);
					else
						throw new IOException(String.format("Cannot unpack; unknown value type %d.", packerTypeAsInt));
				} else
					throw new IOException(String.format("Cannot unpack; no such generic %d.", typeAsInt));
			if (classValueOf.equals(int.class) || classValueOf.equals(Integer.class))
				return data.readInt();
			else if (classValueOf.equals(boolean.class) || classValueOf.equals(Boolean.class))
				return (data.readByte() != 0);
			else if (classValueOf.equals(double.class) || classValueOf.equals(Double.class))
				return data.readDouble();
			else if (classValueOf.equals(float.class) || classValueOf.equals(Float.class))
				return data.readFloat();
			else if (classValueOf.equals(char.class) || classValueOf.equals(Character.class))
				return data.readChar();
			else if (classValueOf.equals(String.class))
				return readString(data, 8192);
			else if (classValueOf.equals(HashMap.class))
				return readHashMap(data);
			else if (classValueOf.equals(ArrayList.class))
				return readArrayList(data);
			else
				throw new IOException("Do not know what to do with " + classValueOf.getName());

		}
	}

	public static void writeHashMap(HashMap<?, ?> values, DataOutputStream data) throws IOException {
		int sign = 0, written = 0;
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null)
				sign++;
		if (BuildInfo.NET_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("Packing %s values in HashMap to stream.", sign));
		data.writeInt(sign);
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null) {
				writeValue(entry.getKey(), data);
				writeValue(entry.getValue(), data);
				written++;
			}
		if (written != sign)
			throw new IOException(String.format("Could not pack packet, wrote %s pairs, expected %s!", written, sign));
	}

	public static HashMap<?, ?> readHashMap(DataInputStream data) throws IOException {
		int size = data.readInt();
		if (BuildInfo.NET_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("Unpacking %s values in HashMap from stream.", size));
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		for (int i = 0; i < size; i++) {
			Object key = readValue(data);
			Object value = readValue(data);
			result.put(key, value);
		}
		return result;
	}

	public static void writeArrayList(ArrayList<?> array, DataOutputStream data) throws IOException {
		if (BuildInfo.NET_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO,
					String.format("Packing %s values in ArrayList to stream.", array.size()));
		data.writeInt(array.size());
		for (Object o : array)
			writeValue(o, data);
	}

	public static ArrayList<?> readArrayList(DataInputStream data) throws IOException {
		int size = data.readInt();
		if (BuildInfo.NET_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO,
					String.format("Unpacking %s values in ArrayList from stream.", size));
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < size; i++)
			result.add(readValue(data));
		return result;
	}

	public static void writeString(String s, DataOutputStream stream) throws IOException {
		stream.writeInt(s.length());
		stream.writeChars(s);
	}

	public static String readString(DataInputStream stream, int max) throws IOException {
		int len = stream.readInt();
		if (len > max)
			throw new IOException(String.format("String too large: got %s, expected maximum %s!", len, max));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < len; i++)
			builder.append(stream.readChar());
		return builder.toString();
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
		return origin;
	}

	public void encode(DataOutputStream data) throws IOException {
		data.writeByte((forServer) ? 1 : 0);
		IStreamPackable<WorldLocation> packer = (IStreamPackable<WorldLocation>) ModPacket
				.findPacker(WorldLocation.class);
		if (origin == null)
			throw new IOException("Cannot encode void location packets.");
		packer.pack(origin, data);
		writeArrayList(key_add, data);
		writeArrayList(key_remove, data);
		writeArrayList(key_modified, data);
		writeHashMap(valueMap, data);
		data.flush();
	}

	public void decode(DataInputStream data) throws IOException {
		forServer = (data.readByte() == 1);
		IStreamPackable<?> unpacker = ModPacket.findPacker(WorldLocation.class);
		origin = (WorldLocation) unpacker.unpack(data);
		key_add = (ArrayList<Object>) readArrayList(data);
		key_remove = (ArrayList<Object>) readArrayList(data);
		key_modified = (ArrayList<Object>) readArrayList(data);
		valueMap = (HashMap<Object, Object>) readHashMap(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(output);
		encode(data);
		data.close();
		output.flush();
		buffer.writeBytes(output.toByteArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		byte[] b = new byte[buffer.readableBytes() - buffer.readerIndex()];
		buffer.readBytes(b);
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(b));
		decode(data);
	}

}
