package lc.common.util.data;

/**
 * Primitive value comparison tool
 * 
 * @author AfterLifeLochie
 */
public class PrimitiveCompare {

	/**
	 * <p>
	 * Compare an array of two int.
	 * </p>
	 * <p>
	 * This method follows the following truth rules:
	 * <ul>
	 * <li>If a is null and b is null, the arrays are equal.</li>
	 * <li>If a or b is null and b or a is not null, the arrays are not equal.</li>
	 * <li>If a and b are not the same length, the arrays are not equal.</li>
	 * <li>If any element of a does not match the same indexed element in b, the
	 * arrays are not equal.</li>
	 * <li>If the arrays are the same length and all elements math, the arrays
	 * are truly equal.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param a
	 *            The first array
	 * @param b
	 *            The second array
	 * @return If the two arrays are equal
	 */
	public static boolean compareInt(int[] a, int[] b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null || a.length != b.length)
			return false;
		int q = a.length;
		for (int i = 0; i < q; i++)
			if (a[i] != b[i])
				return false;
		return true;
	}

	/**
	 * <p>
	 * Compare an array of two byte.
	 * </p>
	 * <p>
	 * This method follows the following truth rules:
	 * <ul>
	 * <li>If a is null and b is null, the arrays are equal.</li>
	 * <li>If a or b is null and b or a is not null, the arrays are not equal.</li>
	 * <li>If a and b are not the same length, the arrays are not equal.</li>
	 * <li>If any element of a does not match the same indexed element in b, the
	 * arrays are not equal.</li>
	 * <li>If the arrays are the same length and all elements math, the arrays
	 * are truly equal.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param a
	 *            The first array
	 * @param b
	 *            The second array
	 * @return If the two arrays are equal
	 */
	public static boolean compareByte(byte[] a, byte[] b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null || a.length != b.length)
			return false;
		int q = a.length;
		for (int i = 0; i < q; i++)
			if (a[i] != b[i])
				return false;
		return true;
	}

	/**
	 * <p>
	 * Compare an array of two char.
	 * </p>
	 * <p>
	 * This method follows the following truth rules:
	 * <ul>
	 * <li>If a is null and b is null, the arrays are equal.</li>
	 * <li>If a or b is null and b or a is not null, the arrays are not equal.</li>
	 * <li>If a and b are not the same length, the arrays are not equal.</li>
	 * <li>If any element of a does not match the same indexed element in b, the
	 * arrays are not equal.</li>
	 * <li>If the arrays are the same length and all elements math, the arrays
	 * are truly equal.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param a
	 *            The first array
	 * @param b
	 *            The second array
	 * @return If the two arrays are equal
	 */
	public static boolean compareChar(char[] a, char[] b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null || a.length != b.length)
			return false;
		int q = a.length;
		for (int i = 0; i < q; i++)
			if (a[i] != b[i])
				return false;
		return true;
	}

}
