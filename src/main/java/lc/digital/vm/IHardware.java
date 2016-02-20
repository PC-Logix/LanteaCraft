package lc.digital.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IHardware {

	public boolean modified();

	public void serialize(DataOutputStream out) throws IOException;

	public void unserialize(DataInputStream in) throws IOException;

}
