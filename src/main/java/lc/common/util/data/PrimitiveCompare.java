package lc.common.util.data;

public class PrimitiveCompare {

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
