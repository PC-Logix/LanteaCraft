package lc.common.stargate;

import lc.api.stargate.MessagePayload;

public class MessageStringPayload extends MessagePayload {

	private final byte[] data;

	public MessageStringPayload(String s) {
		char[] blob = s.toCharArray();
		data = new byte[blob.length];
		for (int i = 0; i < blob.length; i++)
			data[i] = (byte) blob[i];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.length; i++)
			builder.append((char) data[i]);
		return builder.toString();
	}

	@Override
	public byte[] data() {
		return data;
	}

}
