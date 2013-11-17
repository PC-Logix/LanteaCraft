package pcl.lc.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class UniversalAddressDatabase {

	private static final byte[] DBHEADER = new byte[] { (byte) 0x81, 0x53, 0x47, 0x43, 0x2D, 0x52, 0x1A, 0x0A };

	private volatile RandomAccessFile bits;
	private final File outFile;
	private ArrayList<StargateAddress> addressPool;

	public UniversalAddressDatabase(File fileOf) {
		outFile = fileOf;
		addressPool = new ArrayList<StargateAddress>();
	}

	public void registerAddres(StargateAddress address) throws AddressingException.AddressAlreadyInUseException {
		synchronized (addressPool) {
			if (addressPool.contains(address))
				throw new AddressingException.AddressAlreadyInUseException("The address object is already registered.");
			addressPool.add(address);
		}
	}

	public StargateAddress getAddress(String name) throws AddressingException.AddressNotFoundException {
		synchronized (addressPool) {
			for (StargateAddress entry : addressPool)
				if (entry.getAddress().equals(name))
					return entry;
			return null;
		}
	}

	public void readAddresses() throws AddressingException.DatabaseException {
		try {
			bits = new RandomAccessFile(outFile, "r");
			bits.seek(0);
			for (byte b : UniversalAddressDatabase.DBHEADER) {
				byte b1 = bits.readByte();
				if (b1 != b)
					throw new AddressingException.DatabaseException("Invalid database; expected " + b + ", got " + b1);
			}

			long rem = bits.length() - bits.getFilePointer();
			if ((rem % 92) != 0)
				throw new AddressingException.DatabaseException("Invalid remainder, expected absolute 92.");
			int addressSize = (int) Math.floor(rem / 92);
			for (int i = 0; i < addressSize; i++) {
				byte[] data = new byte[92];
				bits.read(data);
				StargateAddress addr = StargateAddress.load(new ByteArrayInputStream(data));
				if (addr != null)
					addressPool.add(addr);
			}
		} catch (IOException ioex) {
			throw new AddressingException.DatabaseException("Database file access exception.", ioex);
		} finally {
			if (bits != null) {
				try {
					bits.close();
				} catch (Throwable t) {
				}
				bits = null;
			}
		}
	}

	public void saveAddresses() throws AddressingException.DatabaseException {
		try {
			outFile.delete();
			bits = new RandomAccessFile(outFile, "rwd");
			bits.seek(0);
			for (byte b : UniversalAddressDatabase.DBHEADER)
				bits.write(b);
			for (StargateAddress entry : addressPool) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				StargateAddress.save(entry, buffer);
				bits.write(buffer.toByteArray());
			}
		} catch (IOException ioex) {
			throw new AddressingException.DatabaseException("Database file access exception.", ioex);
		} finally {
			if (bits != null) {
				try {
					bits.close();
				} catch (Throwable t) {
				}
				bits = null;
			}
		}
	}
}
