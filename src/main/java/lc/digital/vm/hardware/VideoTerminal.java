package lc.digital.vm.hardware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lc.digital.vm.IHardware;

public class VideoTerminal implements IHardware {

	public ArrayList<VideoTerminalObject> objects;

	public VideoTerminal() {
		this.objects = new ArrayList<VideoTerminalObject>();
	}

	@Override
	public boolean modified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void serialize(DataOutputStream out) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unserialize(DataInputStream in) throws IOException {
		// TODO Auto-generated method stub

	}

}
