package pcl.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import pcl.common.util.WorldLocation;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;

public class StandardModPacket extends ModPacket {

	/**
	 * The type of the packet
	 */
	private String packetType;
	/**
	 * The origin location of the packet
	 */
	private WorldLocation origin;
	/**
	 * The values of objects in the packet
	 */
	private HashMap<Object, Object> values;
	/**
	 * The destination of the packet
	 */
	private volatile boolean isPacketForServer;

	/**
	 * Generic constructor, creates a blank packet object
	 */
	public StandardModPacket() {
		values = new HashMap<Object, Object>();
	}

	public StandardModPacket(WorldLocation origin) {
		values = new HashMap<Object, Object>();
		this.origin = origin;
	}

	/**
	 * Gets the type of the packet
	 * 
	 * @return The type of the packet
	 */
	@Override
	public String getType() {
		return packetType;
	}

	@Override
	public WorldLocation getOriginLocation() {
		return origin;
	}

	/**
	 * Determines if this packet contains a field
	 * 
	 * @param name
	 *            The name of the field
	 * @return If the field exists
	 */
	public boolean hasField(String name) {
		synchronized (values) {
			return values.containsKey(name);
		}
	}

	/**
	 * Determines if this packet contains a field with a value not null
	 * 
	 * @param name
	 *            The name of the field
	 * @return If the field exists and contains a non null value
	 */
	public boolean hasFieldWithValue(String name) {
		synchronized (values) {
			return values.containsKey(name) && values.get(name) != null;
		}
	}

	/**
	 * Gets a value of a field in the packet
	 * 
	 * @param name
	 *            The name of the field
	 * @return Any, or null, assigned value to the specified field
	 */
	public Object getValue(String name) {
		synchronized (values) {
			return values.get(name);
		}
	}

	/**
	 * Gets all fields and their values
	 * 
	 * @return All fields and their values
	 */
	public HashMap<Object, Object> getValues() {
		return values;
	}

	/**
	 * Sets a field and it's value
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The value to assign
	 */
	public void setValue(String name, Object value) {
		if (value == null)
			LanteaCraft.getLogger().log(Level.WARN,
					String.format("Attempt to pack null value into key %s, this is probably bad.", name));
		synchronized (values) {
			values.put(name, value);
		}
	}

	/**
	 * Sets all fields given the values in the map
	 * 
	 * @param map
	 *            A map of items. Each value in the map will be set as a field
	 *            inside the packet.
	 */
	public void setAllValues(Map<String, Object> map) {
		synchronized (values) {
			for (Entry<String, Object> item : map.entrySet())
				values.put(item.getKey(), item.getValue());
		}
	}

	/**
	 * Sets the type of the packet
	 * 
	 * @param typeof
	 *            The packet type
	 */
	public void setType(String typeof) {
		packetType = typeof;
	}

	/**
	 * Sets the packet destination
	 * 
	 * @param state
	 *            If the packet is for the server
	 */
	public void setIsForServer(boolean state) {
		isPacketForServer = state;
	}

	/**
	 * Gets the packet destination
	 * 
	 * @return If the packet is for the server
	 */
	@Override
	public boolean getPacketIsForServer() {
		return isPacketForServer;
	}

	/**
	 * Determines if a field with the given label is declared
	 * 
	 * @param name
	 *            The field name
	 * @return If such a field exists
	 */
	public boolean hasKey(String name) {
		synchronized (values) {
			return values.containsKey(name);
		}
	}

	/**
	 * Writes a generic value to a stream
	 * 
	 * @param o
	 *            The value to write
	 * @param data
	 *            The stream to write to
	 * @throws IOException
	 *             Any write exception
	 */
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

	/**
	 * Reads a generic value from a stream
	 * 
	 * @param data
	 *            The stream to read from
	 * @return The value read
	 * @throws IOException
	 *             Any read exception
	 */
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
						throw new IOException("Cannot unpack; unknown value.");
				} else
					throw new IOException("Cannot unpack; unknown value.");
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

	/**
	 * Writes a HashMap of any anonymous type to a stream. This method removes
	 * all null key or null value pairs from the iteration, only writing values
	 * which are not null or null-pointers
	 * 
	 * @param values
	 *            The HashMap of values to write
	 * @param data
	 *            The stream to write to
	 * @throws IOException
	 *             Any write exception
	 */
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

	/**
	 * Reads a HashMap of an anonymous type from a stream.
	 * 
	 * @param data
	 *            The stream to read from
	 * @return The HashMap of values
	 * @throws IOException
	 *             Any read exception
	 */
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

	/**
	 * Writes an ArrayList of any anonymous type to a stream. This method
	 * removes all null value items from the iteration, only writing values
	 * which are not null or null-pointers
	 * 
	 * @param array
	 *            The ArrayList of values to write
	 * @param data
	 *            The stream to write to
	 * @throws IOException
	 *             Any write exception
	 */
	public static void writeArrayList(ArrayList<?> array, DataOutputStream data) throws IOException {
		if (BuildInfo.NET_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO,
					String.format("Packing %s values in ArrayList to stream.", array.size()));
		data.writeInt(array.size());
		for (Object o : array)
			writeValue(o, data);
	}

	/**
	 * Reads an ArrayList of an anonymous type from a stream.
	 * 
	 * @param data
	 *            The stream to read from
	 * @return The ArrayList of values
	 * @throws IOException
	 *             Any read exception
	 */
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

	/**
	 * Dumps the packet to a string.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("LanteaPacket { ");
		result.append("typeof ").append(packetType).append(", ");
		result.append("payload ").append(values.size()).append(", ");
		result.append("isforserver ").append(isPacketForServer ? "yes" : "no").append(" }");

		result.append(" \n{ ");
		for (Entry<Object, Object> invariate : values.entrySet()) {
			result.append(invariate.getKey().toString());
			result.append(": ");
			if (invariate.getValue() != null)
				result.append(invariate.getValue().toString());
			else
				result.append("(null)");
			result.append(", ");
		}
		result.append("}.");
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(output);
		if (packetType == null || packetType.length() == 0)
			throw new IOException("Cannot pack blank packetType!");
		if (origin == null)
			throw new IOException("Cannot pack blank originType!");
		if (packetType.length() > 512)
			throw new IOException("packetType exceeds maximum length!");
		writeString(packetType, data);
		data.writeByte((isPacketForServer) ? 1 : 0);
		IStreamPackable<WorldLocation> packer = (IStreamPackable<WorldLocation>) ModPacket
				.findPacker(WorldLocation.class);
		packer.pack(origin, data);
		synchronized (values) {
			writeHashMap(values, data);
		}
		data.flush();
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
		packetType = readString(data, 512);
		isPacketForServer = (data.readByte() == 1);
		IStreamPackable<?> unpacker = ModPacket.findPacker(WorldLocation.class);
		origin = (WorldLocation) unpacker.unpack(data);
		synchronized (values) {
			values = (HashMap<Object, Object>) readHashMap(data);
		}
	}
}
