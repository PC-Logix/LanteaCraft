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
	private final char[] radix = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-+").toCharArray();

	/**
	 * Radix size reference.
	 */
	public final int radixSize;

	/**
	 * test: (each char in s) => (char in radix)
	 */
	public void legal(String s) {
		for (char c : s.toCharArray())
			index(c);
	}

	/**
	 * test: (each char in s) => (char in radix); throws no unchecked
	 * exceptions.
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
	 */
	public boolean isLegal(char c) {
		try {
			return (index(c) > -1);
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * index of c in radix; exception if not radix-char
	 */
	public int index(char c) {
		for (int i = 0; i < radix.length; i++)
			if (radix[i] == c)
				return i;
		throw new NumberFormatException("Illegal radix value.");
	}

	/**
	 * value of index i in radix; exception if out of radix bounds
	 */
	public char index(int i) {
		if (0 > i || i > radix.length)
			throw new NumberFormatException("Illegal radix value.");
		return radix[i];
	}

	/**
	 * boolean[4] to String(1)
	 */
	private String btos(boolean[] flags) {
		int accum = 0;
		if (flags.length != 4)
			throw new NumberFormatException("Illegal btos dimension.");
		for (int i = 0; i < 4; i++)
			if (flags[i])
				accum |= 1 << i;
		return itos(accum, 1);
	}

	/**
	 * String(1) to boolean[4]
	 */
	private boolean[] stob(String value) {
		boolean[] result = new boolean[4];
		int accum = stoi(value);
		for (int i = 0; i < 4; i++)
			if ((accum & (1 << i)) != 0)
				result[i] = true;
		return result;
	}

	/**
	 * int to String(width)
	 */
	private String itos(int value, int width) {
		final char[] buf = new char[width];
		while (width > 0) {
			buf[--width] = index(value % radix.length);
			value /= radix.length;
		}
		if (value != 0)
			throw new NumberFormatException("Number too large.");
		return new String(buf);
	}

	/**
	 * String(?) to int
	 */
	private int stoi(String value) {
		int result = 0, multmin, digit;
		int i = 0, len = value.length(), limit = -Integer.MAX_VALUE;
		if (len > 0) {
			multmin = limit / radix.length;
			while (i < len) {
				digit = index(value.charAt(i++));
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