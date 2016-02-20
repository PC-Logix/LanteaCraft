package lc.digital.vm.hardware;

import java.util.ArrayList;

import lc.digital.vm.IHardware;

public class VideoTerminal implements IHardware {

	public ArrayList<VideoTerminalObject> objects;

	public VideoTerminal() {
		this.objects = new ArrayList<VideoTerminalObject>();
	}

}
