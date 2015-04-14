package lc.api.jit;

import java.lang.reflect.Method;

public class ASMTag {
	
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
