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
	public static Tag findTag(Method m, String name) {
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
		return null;
	}

}
