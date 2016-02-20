package lc.digital.vm;

import java.util.HashMap;

import lc.digital.vm.kahlua.LuaMachine;
import lc.digital.vm.kahlua.LuaMachineException;

public class Device {

	private LuaMachine emulator;
	private HashMap<String, IHardware> hardware;

	public Device() {
		this.emulator = new LuaMachine();
		this.hardware = new HashMap<String, IHardware>();
	}

	public void addHardware(String spName, IHardware device) {
		this.hardware.put(spName, device);
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

}
