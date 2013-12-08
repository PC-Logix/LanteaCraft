package pcl.lc.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents an object which can be packed to and from a stream, or can be used to pack an
 * object to and from a stream.
 * 
 * @author AfterLifeLochie
 * 
 * @param <T>
 *            The type of object this packer serves
 */
public abstract class IStreamPackable<T> {

	/**
	 * The Class<T> value for which this packer serves
	 */
	private final Class<T> classof;

	/**
	 * Declares an IStreamPackable
	 * 
	 * @param classof
	 *            The class T which this packer serves
	 */
	public IStreamPackable(Class<T> classof) {
		this.classof = classof;
	}

	/**
	 * Gets the class of object for which this packer serves
	 * 
	 * @return The class of object for which this packer serves
	 */
	public Class<?> getClassOf() {
		return this.classof;
	}

	/**
	 * Gets an ID representing the type of object. This is universal inside the stream writing
	 * - that is, this ID must match between clients and hosts.
	 * 
	 * @return The ID of this packer, as declared by the registry
	 */
	public abstract int getTypeOf();

	/**
	 * Packs a passed object of class T to the stream provided. The packer can perform any
	 * operations it requires to serialize the entire object to the stream. If the packer fails
	 * for any reason, or if a stream exception is thrown, the packer can raise an IOException
	 * and fail. Packers should not fail silently.
	 * 
	 * @param valueOf
	 *            The value object. This should not be considered immutable to the packer.
	 * @param streamOf
	 *            The stream to write to.
	 * @throws IOException
	 *             Any stream or packing exception
	 */
	public abstract void pack(T valueOf, DataOutputStream streamOf) throws IOException;

	/**
	 * Unpacks an object of class T from a stream provided. The packer can perform any
	 * operations it requires to unserialize the entire object from the stream. If the
	 * unpacking fails for any reason, or if a stream exception is thrown, the packer can
	 * reaise and IOException and fail. Unpackers should not fail silently.
	 * 
	 * @param streamOf
	 *            The stream to read from.
	 * @return The resulting value of type T
	 * @throws IOException
	 *             Any stream or unpacking exception
	 */
	public abstract T unpack(DataInputStream streamOf) throws IOException;
}
