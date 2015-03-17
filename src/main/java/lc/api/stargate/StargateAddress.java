package lc.api.stargate;

import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.data.PrimitiveCompare;
import lc.common.util.data.PrimitiveHelper;

public class StargateAddress {

	private char[] bytes;

	public StargateAddress(String address) {
		this(address.trim().toCharArray());
	}

	public StargateAddress(Character[] boxedBytes) {
		this(PrimitiveHelper.unboxChars(boxedBytes));
	}

	public StargateAddress(char[] bytes) {
		if (bytes.length != 7 && bytes.length != 9)
			throw new ExceptionInInitializerError("Address must be 7 or 9 characters long, got " + bytes.length);
		this.bytes = bytes;
	}

	public char[] getAddress() {
		return bytes;
	}

	public String getAddressString() {
		return new String(bytes);
	}

	public long getILongValue() {
		return StargateCharsetHelper.singleton().addressToLong(bytes);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StargateAddress))
			return false;
		StargateAddress that = (StargateAddress) o;
		return PrimitiveCompare.compareChar(that.bytes, this.bytes);
	}

}
