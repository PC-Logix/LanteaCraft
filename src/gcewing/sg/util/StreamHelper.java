package gcewing.sg.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class StreamHelper {

	public static int readIntFromStream(InputStream stream) throws IOException {
		int j = 0;
		DataInputStream wrapper = new DataInputStream(stream);
		j = wrapper.readInt();
		wrapper.close();
		return j;
	}

	public static void writeIntToStream(OutputStream stream, int j) throws IOException {
		DataOutputStream wrapper = new DataOutputStream(stream);
		wrapper.writeInt(j);
		wrapper.close();
	}

	public static String readStringFromStream(InputStream stream, int len) throws IOException {
		byte[] data = new byte[len];
		int i = 0;
		while (true) {
			byte b = (byte) stream.read();
			if (b == 0x00)
				break;
			data[i] = b;
			i++;
			if (i > len)
				throw new IOException("String length longer than is permitted!");
		}
		return new String(data, "UTF-8");
	}

	public static void writeStringToStream(OutputStream stream, String s) throws IOException {
		byte[] sasb = s.getBytes("UTF-8");
		stream.write(sasb);
		stream.write(0x00);
	}

}
