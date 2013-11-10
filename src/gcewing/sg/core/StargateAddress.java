package gcewing.sg.core;

import gcewing.sg.util.StreamHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StargateAddress {

	private String address;
	private WorldLocation location;
	private short orientation;
	private short typeof;
	private String owner;

	public StargateAddress() {
	}

	public StargateAddress(String addr, WorldLocation loc, short orient, short typeof, String own) {
		this.address = addr;
		this.owner = own;
		this.location = loc;
		this.typeof = typeof;
		this.orientation = orient;
	}

	public String getAddress() {
		return this.address;
	}

	public WorldLocation getLocation() {
		return this.location;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setAddress(String addr) {
		this.address = addr;
	}

	public void setLocation(WorldLocation loc) {
		this.location = loc;
	}

	public void setOwner(String own) {
		if (own.length() > 64 || own.length() <= 0)
			throw new RuntimeException("Invalid name length.");
		this.owner = own;
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
