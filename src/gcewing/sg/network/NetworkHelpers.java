package gcewing.sg.network;

import gcewing.sg.multiblock.EnumOrientations;
import gcewing.sg.util.ImmutablePair;
import gcewing.sg.util.Vector3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetworkHelpers {

	private static Vector3NetworkPacker vec3Network = new Vector3NetworkPacker();
	private static EnumOrientationsNetworkPacker orientationNetwork = new EnumOrientationsNetworkPacker();
	private static ImmutablePairPacker pairNetwork = new ImmutablePairPacker();

	public void init() {
		Vector3NetworkPacker.packId = SGCraftPacket.registerPackable(Vector3NetworkPacker.instance);
		EnumOrientationsNetworkPacker.packId = SGCraftPacket.registerPackable(EnumOrientationsNetworkPacker.instance);
		ImmutablePairPacker.packId = SGCraftPacket.registerPackable(ImmutablePairPacker.instance);
	}

	public static class ImmutablePairPacker extends IStreamPackable<ImmutablePair> {
		private static ImmutablePairPacker instance;
		private static int packId;

		public ImmutablePairPacker() {
			super(ImmutablePair.class);
			ImmutablePairPacker.instance = this;
		}

		@Override
		public int getTypeOf() {
			return packId;
		}

		@Override
		public void pack(ImmutablePair valueOf, DataOutputStream streamOf) throws IOException {
			SGCraftPacket.writeValue(valueOf.getA(), streamOf);
			SGCraftPacket.writeValue(valueOf.getB(), streamOf);
		}

		@Override
		public ImmutablePair unpack(DataInputStream streamOf) throws IOException {
			Object valA = SGCraftPacket.readValue(streamOf);
			Object valB = SGCraftPacket.readValue(streamOf);
			return new ImmutablePair<Object, Object>(valA, valB);
		}
	}

	public static class Vector3NetworkPacker extends IStreamPackable<Vector3> {
		private static Vector3NetworkPacker instance;
		private static int packId;

		public Vector3NetworkPacker() {
			super(Vector3.class);
			Vector3NetworkPacker.instance = this;
		}

		@Override
		public int getTypeOf() {
			return packId;
		}

		@Override
		public void pack(Vector3 valueOf, DataOutputStream streamOf) throws IOException {
			streamOf.writeDouble(valueOf.x);
			streamOf.writeDouble(valueOf.y);
			streamOf.writeDouble(valueOf.z);
		}

		@Override
		public Vector3 unpack(DataInputStream streamOf) throws IOException {
			double dx = streamOf.readDouble();
			double dy = streamOf.readDouble();
			double dz = streamOf.readDouble();
			return new Vector3(dx, dy, dz);
		}
	}

	public static class EnumOrientationsNetworkPacker extends IStreamPackable<EnumOrientations> {
		private static EnumOrientationsNetworkPacker instance;
		private static int packId;

		public EnumOrientationsNetworkPacker() {
			super(EnumOrientations.class);
			EnumOrientationsNetworkPacker.instance = this;
		}

		@Override
		public int getTypeOf() {
			return packId;
		}

		@Override
		public void pack(EnumOrientations valueOf, DataOutputStream streamOf) throws IOException {
			streamOf.writeInt(valueOf.getID());
		}

		@Override
		public EnumOrientations unpack(DataInputStream streamOf) throws IOException {
			int id = streamOf.readInt();
			return EnumOrientations.getOrientationFromID(id);
		}
	}

}
