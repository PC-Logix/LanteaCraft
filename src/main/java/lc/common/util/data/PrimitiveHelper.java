package lc.common.util.data;

public class PrimitiveHelper {

	public static char[] unboxChars(Character[] boxed) {
		char[] result = new char[boxed.length];
		for (int i = 0; i < result.length; i++)
			result[i] = boxed[i].charValue();
		return result;
	}

}
