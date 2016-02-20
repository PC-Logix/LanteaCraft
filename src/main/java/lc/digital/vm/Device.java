package lc.digital.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import lc.digital.vm.kahlua.LuaMachine;
import lc.digital.vm.kahlua.LuaMachineException;

public class Device {

	private LuaMachine emulator;
	private HashMap<Integer, IHardware> hardware;

	public Device() {
		this.emulator = new LuaMachine();
		this.hardware = new HashMap<Integer, IHardware>();
	}

	public void addHardware(Integer spDevSlot, IHardware device) {
		this.hardware.put(spDevSlot, device);
	}

	public void init() {
		try {
			emulator.init();
		} catch (LuaMachineException exception) {
			// TODO: panic
		}
	}

	public void advance() {
		try {
			emulator.advance();
		} catch (LuaMachineException exception) {
			// TODO: panic
		}
	}

	public boolean modified() {
		for (Entry<Integer, IHardware> dev : hardware.entrySet())
			if (dev.getValue().modified())
				return true;
		return false;
	}

	public void serialize(DataOutputStream out) throws IOException {
		for (Entry<Integer, IHardware> dev : hardware.entrySet()) {
			if (dev.getValue().modified()) {
				out.writeInt(dev.getKey());
				dev.getValue().serialize(out);
			}
		}
	}

	public void unserialize(DataInputStream in) throws IOException {
		while (in.available() != 0) {
			int slot = in.readInt();
			hardware.get(slot).unserialize(in);
		}
	}

}
