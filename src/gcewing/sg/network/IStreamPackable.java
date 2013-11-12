package gcewing.sg.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class IStreamPackable<T> {

	private final Class<T> classof;

	public IStreamPackable(Class<T> classof) {
		this.classof = classof;
	}

	public Class<?> getClassOf() {
		return this.classof;
	}

	public abstract int getTypeOf();

	public abstract void pack(T valueOf, DataOutputStream streamOf) throws IOException;

	public abstract T unpack(DataInputStream streamOf) throws IOException;
}
