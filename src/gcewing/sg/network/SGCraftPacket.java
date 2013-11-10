package gcewing.sg.network;

import gcewing.sg.BuildInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	private static final Class<?>[] classReferences = { int.class, Integer.class, boolean.class, Boolean.class,
			double.class, Double.class, float.class, Float.class, String.class };
	private static ArrayList<IStreamPackable> packableHelpers = new ArrayList<IStreamPackable>();

	public static void registerPackable(IStreamPackable<?> agent) {
		synchronized (packableHelpers) {
			packableHelpers.add(agent);
		}
	}

	public static IStreamPackable<?> getPackerForID(int idx) {
		synchronized (packableHelpers) {
			for (IStreamPackable<?> packer : packableHelpers) {
				if (packer.getTypeOf() == idx)
					return packer;
			}
		}
		return null;
	}

	private static int getIDForClassOf(Class<?> clazz) {
		for (int i = 0; i < classReferences.length; i++)
			if (classReferences[i].equals(clazz))
				return i;
		return -1;
	}

	private static Class<?> getClassOfForID(int id) {
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
	private HashMap<String, Object> values;
	private volatile boolean isPacketForServer;

	public SGCraftPacket() {
		packetType = PacketType.InvalidPacket;
		values = new HashMap<String, Object>();
	}

	public PacketType getType() {
		return packetType;
	}

	public Object getValue(String name) {
		synchronized (values) {
			return values.get(name);
		}
	}

	public HashMap<String, Object> getValues() {
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

	private void writeData(DataOutputStream data) throws IOException {
		data.writeByte((byte) 1);
		data.writeByte(packetType.getPacketID());
		data.writeByte((isPacketForServer) ? 1 : 0);
		synchronized (values) {
			data.writeInt(values.size());
			for (Entry<String, Object> entry : values.entrySet()) {
				int intValueOf = -1;
				if (entry.getValue() != null)
					intValueOf = getIDForClassOf(entry.getValue().getClass());
				if (intValueOf < 0 && intValueOf != -1) {
					Object o1 = entry.getValue();
					if (o1 instanceof IStreamPackable) {
						IStreamPackable objectAsPackable = (IStreamPackable) o1;
						String name = entry.getKey();
						Packet.writeString(name, data);
						data.writeInt(255);
						data.writeInt(objectAsPackable.getTypeOf());
						objectAsPackable.pack(o1, data);
					} else
						throw new IOException("Cannot pack " + entry.getValue().getClass().getName()
								+ "; unknown value.");
				} else {
					String name = entry.getKey();
					Packet.writeString(name, data);
					data.writeInt(intValueOf);
					if (intValueOf != -1) {
						switch (intValueOf) {
						case 0:
						case 1:
							data.writeInt((Integer) entry.getValue());
							break;
						case 2:
						case 3:
							data.writeByte((Boolean) entry.getValue() ? 1 : 0);
							break;
						case 4:
						case 5:
							data.writeDouble((Double) entry.getValue());
							break;
						case 6:
						case 7:
							data.writeFloat((Float) entry.getValue());
							break;
						case 8:
							Packet.writeString((String) entry.getValue(), data);
							break;
						default:
							throw new IOException("Don't know what to do with typeof " + intValueOf);
						}
					}
				}
			}
		}
	}

	private void readData(DataInputStream data) throws IOException {
		if (data.readByte() != (byte) 1)
			throw new IOException("Malformed packet!!");
		byte typeof = data.readByte();
		for (PacketType typeenum : PacketType.values())
			if (typeenum.getPacketID() == typeof)
				packetType = typeenum;
		isPacketForServer = (data.readByte() == 1);
		int size = data.readInt();

		synchronized (values) {
			for (int i = 0; i < size; i++) {
				String name = Packet.readString(data, 1024);
				int typeAsInt = data.readInt();
				if (typeAsInt == -1) {
					values.put(name, null);
				} else {
					Class<?> classValueOf = getClassOfForID(typeAsInt);
					if (classValueOf == null) {
						if (typeAsInt == 255) {
							int packerTypeAsInt = data.readInt();
							IStreamPackable<?> packer = SGCraftPacket.getPackerForID(packerTypeAsInt);
							if (packer != null) {
								Object o1 = packer.unpack(data);
								values.put(name, o1);
							} else
								throw new IOException("Cannot unpack; unknown value.");
						} else
							throw new IOException("Cannot unpack; unknown value.");
					}
					if (classValueOf.equals(int.class) || classValueOf.equals(Integer.class)) {
						int valueAsInteger = data.readInt();
						values.put(name, valueAsInteger);
					} else if (classValueOf.equals(boolean.class) || classValueOf.equals(Boolean.class)) {
						boolean valueAsBoolean = (data.readByte() != 0);
						values.put(name, valueAsBoolean);
					} else if (classValueOf.equals(double.class) || classValueOf.equals(Double.class)) {
						double valueAsDouble = data.readDouble();
						values.put(name, valueAsDouble);
					} else if (classValueOf.equals(float.class) || classValueOf.equals(Float.class)) {
						float valueAsFloat = data.readFloat();
						values.put(name, valueAsFloat);
					} else if (classValueOf.equals(String.class)) {
						String valueAsString = Packet.readString(data, 8192);
						values.put(name, valueAsString);
					} else {
						throw new IOException("Do not know what to do with " + classValueOf.getName());
					}

				}
			}
		}
	}

	public Packet250CustomPayload toPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(((java.io.OutputStream) (bytes)));
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		try {
			writeData(data);
		} catch (IOException e) {
			e.printStackTrace();
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
		return result.toString();
	}
}
