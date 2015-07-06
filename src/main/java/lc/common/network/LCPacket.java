package lc.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * Generic packet implementation
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCPacket {

	/** Primitive type map */
	private enum PrimType {
		NULL, BOOLEAN, SHORT, CHARACTER, INTEGER, FLOAT, DOUBLE, STRING;
	}

	/**
	 * Encode a packet into the network stream.
	 * 
	 * @param buffer
	 *            The write buffer
	 * @throws IOException
	 *             If a problem occurs, an I/O exception may be thrown.
	 */
	public abstract void encodeInto(ByteBuf buffer) throws IOException;

	/**
	 * Encode a boxed primitive into the underlying stream
	 * 
	 * @param buffer
	 *            The buffer stream
	 * @param prim
	 *            The boxed primitive object
	 * @throws IOException
	 *             Any exception which occurs when writing, or if the boxed type
	 *             is unknown
	 */
	protected static void encodePrimitiveInto(ByteBuf buffer, Object prim) throws IOException {
		if (prim == null) {
			buffer.writeByte(PrimType.NULL.ordinal());
		} else if (prim instanceof Boolean) {
			buffer.writeByte(PrimType.BOOLEAN.ordinal());
			buffer.writeBoolean((Boolean) prim);
		} else if (prim instanceof Short) {
			buffer.writeByte(PrimType.SHORT.ordinal());
			buffer.writeShort((Short) prim);
		} else if (prim instanceof Character) {
			buffer.writeByte(PrimType.CHARACTER.ordinal());
			buffer.writeChar((Character) prim);
		} else if (prim instanceof Integer) {
			buffer.writeByte(PrimType.INTEGER.ordinal());
			buffer.writeInt((Integer) prim);
		} else if (prim instanceof Float) {
			buffer.writeByte(PrimType.FLOAT.ordinal());
			buffer.writeFloat((Float) prim);
		} else if (prim instanceof Double) {
			buffer.writeByte(PrimType.DOUBLE.ordinal());
			buffer.writeDouble((Double) prim);
		} else if (prim instanceof String) {
			buffer.writeByte(PrimType.STRING.ordinal());
			char[] data = ((String) prim).toCharArray();
			buffer.writeInt(data.length);
			for (int i = 0; i < data.length; i++)
				buffer.writeByte((byte) data[i]);
		} else
			throw new IOException("Unknown primitive type " + prim.getClass().getName());
	}

	/**
	 * Encode an array of boxed primitives into the underlying stream
	 * 
	 * @param buffer
	 *            The buffer stream
	 * @param arr
	 *            The array of boxed primitive objects
	 * @throws IOException
	 *             Any exception which occurs when writing, or if the boxed type
	 *             is unknown
	 */
	protected static void encodePrimitiveArrayInto(ByteBuf buffer, Object[] arr) throws IOException {
		buffer.writeInt(arr.length);
		for (int i = 0; i < arr.length; i++)
			encodePrimitiveInto(buffer, arr[i]);
	}

	/**
	 * Decode a packet from the network stream.
	 *
	 * @param buffer
	 *            The read buffer
	 * @throws IOException
	 *             If a problem occurs, an I/O exception may be thrown.
	 */
	public abstract void decodeFrom(ByteBuf buffer) throws IOException;

	/**
	 * Decode a boxed primitive from the underlying stream
	 * 
	 * @param buffer
	 *            The underlying stream
	 * @return The boxed type
	 * @throws IOException
	 *             Any exception which occurs when reading, or if the specified
	 *             boxed type on the stream is unsupported
	 */
	protected static Object decodePrimitiveFrom(ByteBuf buffer) throws IOException {
		byte typeof = buffer.readByte();
		switch (PrimType.values()[typeof]) {
		case BOOLEAN:
			return buffer.readBoolean();
		case DOUBLE:
			return buffer.readDouble();
		case FLOAT:
			return buffer.readFloat();
		case CHARACTER:
			return buffer.readChar();
		case INTEGER:
			return buffer.readInt();
		case NULL:
			return null;
		case SHORT:
			return buffer.readShort();
		case STRING:
			int str_sz = buffer.readInt();
			StringBuilder data = new StringBuilder();
			for (int i = 0; i < str_sz; i++) 
				data.append((char) buffer.readByte());
			return data.toString();
		}
		throw new IOException("Unknown primitive type " + typeof);
	}

	/**
	 * Decodes an array of boxed primitives from the underlying stream
	 * 
	 * @param buffer
	 *            The underlying stream
	 * @return The array of boxed primitives
	 * @throws IOException
	 *             Any exception which occurs when reading, or if the specified
	 *             boxed type on the stream is unsupported
	 */
	protected static Object[] decodePrimitiveArrayFrom(ByteBuf buffer) throws IOException {
		int sz = buffer.readInt();
		Object[] prims = new Object[sz];
		for (int i = 0; i < sz; i++)
			prims[i] = decodePrimitiveFrom(buffer);
		return prims;
	}

}
