package pcl.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class StandardModPacket extends ModPacket {

	/**
	 * Reads a new StandardModPacket
	 * 
	 * @param data
	 *            The data stream
	 * @return The StandardModPacket result
	 * @throws IOException
	 *             Any network or read exception
	 */
	public static StandardModPacket createPacket(DataInputStream data) throws IOException {
		StandardModPacket pkt = new StandardModPacket();
		pkt.readData(data);
		return pkt;
	}

	/**
	 * The type of the packet
	 */
	private String packetType;
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

	/**
	 * Gets the type of the packet
	 * 
	 * @return The type of the packet
	 */
	@Override
	public String getType() {
		return packetType;
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
	 * Writes the packet to a stream
	 * 
	 * @param data
	 *            The stream to write to
	 * @throws IOException
	 *             Any write exception
	 */
	public void writeData(DataOutputStream data) throws IOException {
		data.writeByte((byte) 0); // packet typeof
		data.writeByte((byte) 1);
		if (packetType.length() > 512)
			throw new IOException("packetType exceeds maximum length!");
		Packet.writeString(packetType, data);
		data.writeByte((isPacketForServer) ? 1 : 0);
		synchronized (values) {
			writeHashMap(values, data);
		}
	}

	/**
	 * Reads the packet from a stream
	 * 
	 * @param data
	 *            A stream to read from
	 * @throws IOException
	 *             Any read exception
	 */
	public void readData(DataInputStream data) throws IOException {
		if (data.readByte() != (byte) 1)
			throw new IOException("Malformed packet!!");
		packetType = Packet.readString(data, 512);
		isPacketForServer = (data.readByte() == 1);
		synchronized (values) {
			values = (HashMap<Object, Object>) readHashMap(data);
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
				packer.pack(o, data);
			} else
				throw new IOException("Cannot pack " + o.getClass().getName() + "; unknown value.");
		} else {
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
					Packet.writeString((String) o, data);
					break;
				case 9:
					writeArrayList((ArrayList<?>) o, data);
					break;
				case 10:
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
		if (typeAsInt == -1)
			return null;
		else {
			Class<?> classValueOf = getGeneric(typeAsInt);
			if (classValueOf == null)
				if (typeAsInt == 255) {
					int packerTypeAsInt = data.readInt();
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
			else if (classValueOf.equals(String.class))
				return Packet.readString(data, 8192);
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
		int sign = 0;
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null)
				sign++;
		data.writeInt(sign);
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null) {
				writeValue(entry.getKey(), data);
				writeValue(entry.getValue(), data);
			}
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
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < size; i++)
			result.add(readValue(data));
		return result;
	}

	/**
	 * Converts this packet instance into a Forge payload packet
	 * 
	 * @return A custom Packet250CustomPayload packet for Forge networking
	 */
	@Override
	public Packet250CustomPayload toPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(((bytes)));
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		try {
			writeData(data);
		} catch (IOException e) {
			Logger.getLogger("pcl.common").log(Level.WARNING, "Exception when writing packet!", e);
		}
		pkt.data = bytes.toByteArray();
		pkt.length = pkt.data.length;
		return pkt;
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
}
