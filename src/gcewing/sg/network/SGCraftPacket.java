package gcewing.sg.network;

import gcewing.sg.BuildInfo;
import gcewing.sg.SGCraft;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class SGCraftPacket {

	// Typeof.PacketType helper
	public enum PacketType {
		InvalidPacket(0), TileUpdate(1);

		private final byte id;

		PacketType(int id) {
			this.id = (byte) id;
		}

		public byte getPacketID() {
			return this.id;
		}
	}

	// Registers
	private static final Class<?>[] classReferences = { int.class, Integer.class, // integers
			boolean.class, Boolean.class, // booleans
			double.class, Double.class, // doubles
			float.class, Float.class, // floats
			String.class, // string

			ArrayList.class, HashMap.class // generic containers
	};
	private static ArrayList<IStreamPackable> packableHelpers = new ArrayList<IStreamPackable>();

	public static int registerPackable(IStreamPackable<?> agent) {
		synchronized (packableHelpers) {
			packableHelpers.add(agent);
			int id = packableHelpers.indexOf(agent);
			SGCraft.getLogger()
					.log(Level.INFO,
							"SGCraft network helper registering packer: " + agent.getClass().getCanonicalName()
									+ ", uid " + id);
			return id;
		}
	}

	private static IStreamPackable<?> findPacker(int idx) {
		synchronized (packableHelpers) {
			for (IStreamPackable<?> packer : packableHelpers) {
				if (packer.getTypeOf() == idx)
					return packer;
			}
		}
		return null;
	}

	private static IStreamPackable<?> findPacker(Class<?> clazz) {
		synchronized (packableHelpers) {
			for (IStreamPackable<?> packer : packableHelpers) {
				if (packer.getClassOf().equals(clazz))
					return packer;
			}
		}
		return null;
	}

	private static int getGenericID(Class<?> clazz) {
		for (int i = 0; i < classReferences.length; i++)
			if (classReferences[i].equals(clazz))
				return i;
		return -1;
	}

	private static Class<?> getGeneric(int id) {
		if (id >= 0 && id < classReferences.length)
			return classReferences[id];
		return null;
	}

	// Packet instanceof generators
	public static SGCraftPacket parse(byte bytes[]) throws IOException {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
		SGCraftPacket pkt = new SGCraftPacket();
		pkt.readData(data);
		return pkt;
	}

	// Packet instanceof values
	private PacketType packetType = PacketType.InvalidPacket;
	private HashMap<Object, Object> values;
	private volatile boolean isPacketForServer;

	public SGCraftPacket() {
		packetType = PacketType.InvalidPacket;
		values = new HashMap<Object, Object>();
	}

	public PacketType getType() {
		return packetType;
	}

	public Object getValue(String name) {
		synchronized (values) {
			return values.get(name);
		}
	}

	public HashMap<Object, Object> getValues() {
		return values;
	}

	public void setValue(String name, Object value) {
		synchronized (values) {
			values.put(name, value);
		}
	}

	public void setAllValues(Map<String, Object> map) {
		synchronized (values) {
			for (Entry<String, Object> item : map.entrySet())
				values.put(item.getKey(), item.getValue());
		}
	}

	public void setType(PacketType typeof) {
		this.packetType = typeof;
	}

	public void setIsForServer(boolean state) {
		this.isPacketForServer = state;
	}

	public boolean getPacketIsForServer() {
		return isPacketForServer;
	}

	public boolean hasKey(String name) {
		synchronized (values) {
			return values.containsKey(name);
		}
	}

	public void writeData(DataOutputStream data) throws IOException {
		data.writeByte((byte) 1);
		data.writeByte(packetType.getPacketID());
		data.writeByte((isPacketForServer) ? 1 : 0);
		synchronized (values) {
			writeHashMap(values, data);
		}
	}

	public void readData(DataInputStream data) throws IOException {
		if (data.readByte() != (byte) 1)
			throw new IOException("Malformed packet!!");
		byte typeof = data.readByte();
		for (PacketType typeenum : PacketType.values())
			if (typeenum.getPacketID() == typeof)
				packetType = typeenum;
		isPacketForServer = (data.readByte() == 1);

		synchronized (values) {
			values = readHashMap(data);
		}
	}

	public static void writeValue(Object o, DataOutputStream data) throws IOException {
		int intValueOf = getGenericID(o.getClass());
		if (intValueOf == -1) {
			IStreamPackable packer = null;
			if (o instanceof IStreamPackable)
				packer = (IStreamPackable<?>) o;
			if (packer == null)
				packer = SGCraftPacket.findPacker(o.getClass());
			if (packer != null) {
				data.writeInt(255);
				data.writeInt(packer.getTypeOf());
				packer.pack(o, data);
			} else {
				throw new IOException("Cannot pack " + o.getClass().getName() + "; unknown value.");
			}
		} else {
			data.writeInt(intValueOf);
			if (intValueOf != -1) {
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
					writeArrayList((ArrayList) o, data);
					break;
				case 10:
					writeHashMap((HashMap) o, data);
					break;
				default:
					throw new IOException("Don't know what to do with typeof " + intValueOf);
				}
			}
		}
	}

	public static Object readValue(DataInputStream data) throws IOException {
		int typeAsInt = data.readInt();
		if (typeAsInt == -1) {
			return null;
		} else {
			Class<?> classValueOf = getGeneric(typeAsInt);
			if (classValueOf == null) {
				if (typeAsInt == 255) {
					int packerTypeAsInt = data.readInt();
					IStreamPackable<?> packer = SGCraftPacket.findPacker(packerTypeAsInt);
					if (packer != null) {
						return packer.unpack(data);
					} else
						throw new IOException("Cannot unpack; unknown value.");
				} else
					throw new IOException("Cannot unpack; unknown value.");
			}
			if (classValueOf.equals(int.class) || classValueOf.equals(Integer.class)) {
				return data.readInt();
			} else if (classValueOf.equals(boolean.class) || classValueOf.equals(Boolean.class)) {
				return (data.readByte() != 0);
			} else if (classValueOf.equals(double.class) || classValueOf.equals(Double.class)) {
				return data.readDouble();
			} else if (classValueOf.equals(float.class) || classValueOf.equals(Float.class)) {
				return data.readFloat();
			} else if (classValueOf.equals(String.class)) {
				return Packet.readString(data, 8192);
			} else if (classValueOf.equals(HashMap.class)) {
				return readHashMap(data);
			} else if (classValueOf.equals(ArrayList.class)) {
				return readArrayList(data);
			} else {
				throw new IOException("Do not know what to do with " + classValueOf.getName());
			}

		}
	}

	public static void writeHashMap(HashMap<?, ?> values, DataOutputStream data) throws IOException {
		int sign = 0;
		for (Entry<?, ?> entry : values.entrySet())
			if (entry.getKey() != null && entry.getValue() != null)
				sign++;
		data.writeInt(sign);
		for (Entry<?, ?> entry : values.entrySet()) {
			if (entry.getKey() != null && entry.getValue() != null) {
				writeValue(entry.getKey(), data);
				writeValue(entry.getValue(), data);
			}
		}
	}

	public static HashMap readHashMap(DataInputStream data) throws IOException {
		int size = data.readInt();
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		for (int i = 0; i < size; i++) {
			Object key = readValue(data);
			Object value = readValue(data);
			result.put(key, value);
		}
		return result;
	}

	public static void writeArrayList(ArrayList<?> array, DataOutputStream data) throws IOException {
		data.writeInt(array.size());
		for (Object o : array)
			writeValue(o, data);
	}

	public static ArrayList<?> readArrayList(DataInputStream data) throws IOException {
		int size = data.readInt();
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < size; i++)
			result.add(readValue(data));
		return result;
	}

	public Packet250CustomPayload toPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(((java.io.OutputStream) (bytes)));
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		try {
			SGCraft.getLogger().log(Level.INFO, "SGCraft writing packet data...");
			writeData(data);
			SGCraft.getLogger().log(Level.INFO, "SGCraft done packet data!");
		} catch (IOException e) {
			SGCraft.getLogger().log(Level.INFO, "Exception when writing packet!", e);
		}
		pkt.channel = BuildInfo.modID;
		pkt.data = bytes.toByteArray();
		pkt.length = pkt.data.length;
		return pkt;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("SGCraftPacket { ");
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
