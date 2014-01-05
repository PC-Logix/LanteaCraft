package pcl.lc.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pcl.common.helpers.StreamHelper;

public class StargateAddress {

	private String address;
	private WorldLocation location;
	private short orientation;
	private short typeof;
	private String owner;

	public StargateAddress() {
	}

	public StargateAddress(String addr, WorldLocation loc, short orient, short typeof, String own) {
		address = addr;
		owner = own;
		location = loc;
		this.typeof = typeof;
		orientation = orient;
	}

	public String getAddress() {
		return address;
	}

	public WorldLocation getLocation() {
		return location;
	}

	public String getOwner() {
		return owner;
	}

	public void setAddress(String addr) {
		address = addr;
	}

	public void setLocation(WorldLocation loc) {
		location = loc;
	}

	public void setOwner(String own) {
		if (own.length() > 64 || own.length() <= 0)
			throw new RuntimeException("Invalid name length.");
		owner = own;
	}

	public static StargateAddress load(ByteArrayInputStream input) throws IOException {
		StargateAddress addressOf = new StargateAddress();
		addressOf.typeof = (short) input.read();
		byte[] name = new byte[9];
		input.read(name);
		addressOf.address = new String(name, "UTF-8");
		int dimid = StreamHelper.readIntFromStream(input);
		int worldx = StreamHelper.readIntFromStream(input);
		int worldy = StreamHelper.readIntFromStream(input);
		int worldz = StreamHelper.readIntFromStream(input);
		addressOf.location = new WorldLocation(dimid, worldx, worldy, worldz);
		addressOf.orientation = (short) input.read();
		addressOf.owner = StreamHelper.readStringFromStream(input, 64);
		return addressOf;
	}

	public static void save(StargateAddress address, ByteArrayOutputStream output) throws IOException {
		output.write(address.typeof);
		output.write(address.address.getBytes("UTF-8"));
		StreamHelper.writeIntToStream(output, address.location.dimension);
		StreamHelper.writeIntToStream(output, address.location.x);
		StreamHelper.writeIntToStream(output, address.location.y);
		StreamHelper.writeIntToStream(output, address.location.z);
		output.write(address.orientation);
		StreamHelper.writeStringToStream(output, address.owner);
	}

}
