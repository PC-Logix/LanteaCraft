package lc.digital.vm.hardware;

import java.util.ArrayList;

import lc.digital.vm.IHardware;

public class LCDTerminal implements IHardware {

	public ArrayList<String> strings;

	public LCDTerminal() {
		this.strings = new ArrayList<String>();
	}

}
