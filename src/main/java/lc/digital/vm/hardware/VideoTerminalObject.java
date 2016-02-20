package lc.digital.vm.hardware;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class VideoTerminalObject {

	public abstract void serialize(DataOutputStream out) throws IOException;

	public abstract void unserialize(DataInputStream in) throws IOException;

}
