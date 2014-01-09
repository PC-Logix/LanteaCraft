package pcl.common.helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pcl.common.multiblock.EnumOrientations;
import pcl.common.network.IStreamPackable;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.ImmutablePair;
import pcl.common.util.ImmutableTuple;
import pcl.common.util.Vector3;
import pcl.lc.api.EnumStargateState;

/**
 * Generic network helper registry, this is where I chose to dump them as I write jazz.
 * 
 * @author AfterLifeLochie
 * 
 */
public class NetworkHelpers {
	private static Vector3NetworkPacker vec3Network = new Vector3NetworkPacker();
	private static EnumOrientationsNetworkPacker orientationNetwork = new EnumOrientationsNetworkPacker();
	private static EnumStargateStateNetworkPacker stargateStateNetwork = new EnumStargateStateNetworkPacker();
	private static ImmutablePairPacker pairNetwork = new ImmutablePairPacker();
	private static ImmutableTuplePacker tupleNetwork = new ImmutableTuplePacker();

	public void init() {
		Vector3NetworkPacker.packId = ModPacket.registerPackable(Vector3NetworkPacker.instance);
		EnumOrientationsNetworkPacker.packId = ModPacket.registerPackable(EnumOrientationsNetworkPacker.instance);
		EnumStargateStateNetworkPacker.packId = ModPacket.registerPackable(EnumStargateStateNetworkPacker.instance);
		ImmutablePairPacker.packId = ModPacket.registerPackable(ImmutablePairPacker.instance);
		ImmutableTuplePacker.packId = ModPacket.registerPackable(ImmutableTuplePacker.instance);
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
			StandardModPacket.writeValue(valueOf.getA(), streamOf);
			StandardModPacket.writeValue(valueOf.getB(), streamOf);
		}

		@Override
		public ImmutablePair unpack(DataInputStream streamOf) throws IOException {
			Object valA = StandardModPacket.readValue(streamOf);
			Object valB = StandardModPacket.readValue(streamOf);
			return new ImmutablePair<Object, Object>(valA, valB);
		}
	}

	public static class ImmutableTuplePacker extends IStreamPackable<ImmutableTuple> {
		private static ImmutableTuplePacker instance;
		private static int packId;

		public ImmutableTuplePacker() {
			super(ImmutableTuple.class);
			ImmutableTuplePacker.instance = this;
		}

		@Override
		public int getTypeOf() {
			return packId;
		}

		@Override
		public void pack(ImmutableTuple valueOf, DataOutputStream streamOf) throws IOException {
			StandardModPacket.writeValue(valueOf.getA(), streamOf);
			StandardModPacket.writeValue(valueOf.getB(), streamOf);
			StandardModPacket.writeValue(valueOf.getC(), streamOf);
		}

		@Override
		public ImmutableTuple unpack(DataInputStream streamOf) throws IOException {
			Object valA = StandardModPacket.readValue(streamOf);
			Object valB = StandardModPacket.readValue(streamOf);
			Object valC = StandardModPacket.readValue(streamOf);
			return new ImmutableTuple<Object, Object, Object>(valA, valB, valC);
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
			return EnumOrientations.getOrientationFromID(streamOf.readInt());
		}
	}

	public static class EnumStargateStateNetworkPacker extends IStreamPackable<EnumStargateState> {
		private static EnumStargateStateNetworkPacker instance;
		private static int packId;

		public EnumStargateStateNetworkPacker() {
			super(EnumStargateState.class);
			EnumStargateStateNetworkPacker.instance = this;
		}

		@Override
		public int getTypeOf() {
			return packId;
		}

		@Override
		public void pack(EnumStargateState valueOf, DataOutputStream streamOf) throws IOException {
			streamOf.writeInt(valueOf.ordinal());
		}

		@Override
		public EnumStargateState unpack(DataInputStream streamOf) throws IOException {
			return EnumStargateState.fromOrdinal(streamOf.readInt());
		}
	}

}
