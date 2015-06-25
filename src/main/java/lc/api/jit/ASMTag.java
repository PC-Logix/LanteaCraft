package lc.api.jit;

import java.lang.reflect.Method;

/**
 * ASM and reflection-based tag-finding helpers.
 * 
 * @author AfterLifeLochie
 *
 */
public class ASMTag {

	/**
	 * Find a Tag on a Method with a specified name. If the Tag does not exist
	 * on the Method, then null is returned.
	 * 
	 * @param m
	 *            The Method object
	 * @param name
	 *            The name of the tag
	 * @return The Tag instance with the specified name, or null if no tag
	 *         matching is attached to the method
	 */
	public static Tag findTag(Class<?> z, Method m, String name) {
		if (m.isAnnotationPresent(Tag.class)) {
			Tag tag = (Tag) m.getAnnotation(Tag.class);
			if (tag.name().equals(name))
				return tag;
		}
		if (m.isAnnotationPresent(TagMap.class)) {
			TagMap map = (TagMap) m.getAnnotation(TagMap.class);
			Tag[] zt = map.value();
			for (int i = 0; i < zt.length; i++)
				if (zt[i].name().equals(name))
					return zt[i];
		}
		if (m.getDeclaringClass() != null && m.getDeclaringClass() != Object.class && m.getDeclaringClass() != z) {
			try {
				Class<?> z1 = m.getDeclaringClass();
				Method m1 = z1.getMethod(m.getName(), m.getParameterTypes());
				Tag result = findTag(z1, m1, name);
				if (result != null)
					return result;
			} catch (Exception e) {
			}
		}
		return null;
	}

}
