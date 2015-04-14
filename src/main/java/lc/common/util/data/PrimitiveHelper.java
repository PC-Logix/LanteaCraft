package lc.common.util.data;

public class PrimitiveHelper {

	public static char[] unbox(Character[] boxed) {
		char[] result = new char[boxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = boxed[i].charValue();
		return result;
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
