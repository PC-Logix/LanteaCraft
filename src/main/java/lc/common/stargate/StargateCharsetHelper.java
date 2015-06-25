package lc.common.stargate;

/**
 * Stargate address parser.
 *
 * @author AfterLifeLochie
 *
 */
public class StargateCharsetHelper {

	/**
	 * The singleton StargateCharsetHelper object.
	 */
	private static StargateCharsetHelper singleton = new StargateCharsetHelper();

	/**
	 * Fetches the singleton StargateCharsetHelper object.
	 *
	 * @return The singleton {@link StargateCharsetHelper} object.
	 */
	public static StargateCharsetHelper singleton() {
		return singleton;
	}

	/**
	 * Initializer.
	 */
	private StargateCharsetHelper() {
		radixSize = radix.length;
	}

	/**
	 * Radix declaration; order sensitive.
	 */
	private final char[] radix = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-+".toCharArray();

	/**
	 * Radix size reference.
	 */
	public final int radixSize;

	/**
	 * test: (each char in s) => (char in radix)
	 *
	 * @param s
	 *            The string to test
	 */
	public void legal(String s) {
		for (char c : s.toCharArray())
			index(c);
	}

	/**
	 * test: (each char in s) => (char in radix); throws no unchecked
	 * exceptions.
	 *
	 * @param s
	 *            The string of characters
	 * @return if the string is legal in the radix
	 */
	public boolean isLegal(String s) {
		try {
			legal(s);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	/**
	 * Test: char c => char in radix; throws no unchecked exceptions.
	 *
	 * @param c
	 *            The character
	 * @return If the character is legal in the radix
	 */
	public boolean isLegal(char c) {
		try {
			return index(c) > -1;
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * index of c in radix; exception if not radix-char
	 *
	 * @param c
	 *            the character
	 * @return the index of the character in the radix
	 */
	public int index(char c) {
		for (int i = 0; i < radix.length; i++)
			if (radix[i] == c)
				return i;
		throw new NumberFormatException(String.format("Illegal radix value `%s`.", c));
	}

	/**
	 * value of index i in radix; exception if out of radix bounds
	 *
	 * @param i
	 *            the index of the character in the radix
	 * @return the character
	 */
	public char index(int i) {
		if (0 > i || i > radix.length)
			throw new NumberFormatException(String.format("Illegal radix value `%s`.", i));
		return radix[i];
	}

	public String longToAddress(long value, int width) {
		final char[] buf = new char[width];
		while (width > 0) {
			buf[--width] = index((int) (value % radix.length));
			value /= radix.length;
		}
		if (value != 0)
			throw new NumberFormatException("Number too large.");
		return new String(buf);
	}

	public long addressToLong(char[] address) {
		long result = 0, limit = -Long.MAX_VALUE, multmin, digit;
		int i = 0, len = address.length;
		if (len > 0) {
			multmin = limit / radix.length;
			while (i < len) {
				digit = index(address[i++]);
				if (digit < 0)
					throw new NumberFormatException("Not a legal radix-38 symbol.");
				if (result < multmin)
					throw new NumberFormatException("Out of legal radix-multiplication range.");
				result *= radix.length;
				if (result < limit + digit)
					throw new NumberFormatException("Out of legal radix range.");
				result += digit;
			}
		} else
			throw new NumberFormatException("Not a legal radix-38 number.");
		return result;
	}

}