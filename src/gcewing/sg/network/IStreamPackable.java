package gcewing.sg.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IStreamPackable<T> {

	public int getTypeOf();

	public void pack(T valueOf, DataOutputStream streamOf) throws IOException;

	public T unpack(DataInputStream streamOf) throws IOException;
}
