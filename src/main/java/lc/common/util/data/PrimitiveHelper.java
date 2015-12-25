package lc.common.util.data;

public class PrimitiveHelper {

	public static Character[] box(char[] unboxed) {
		Character[] result = new Character[unboxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = unboxed[i];
		return result;
	}

	public static char[] unbox(Character[] boxed) {
		char[] result = new char[boxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = boxed[i].charValue();
		return result;
	}

	public static String flatten(char[] expanded) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < expanded.length; i++)
			result.append(expanded[i]);
		return result.toString();
	}

	public static int[] unbox(Integer[] boxed) {
		int[] result = new int[boxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = boxed[i].intValue();
		return result;
	}

	public static double[] unbox(Double[] boxed) {
		double[] result = new double[boxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = boxed[i].doubleValue();
		return result;
	}

	public static float[] unbox(Float[] boxed) {
		float[] result = new float[boxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = boxed[i].floatValue();
		return result;
	}

}
