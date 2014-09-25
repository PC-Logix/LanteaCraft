package lc.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Reflection utilities
 * 
 * @author AfterLifeLochie
 * 
 */
public class ReflectionHelper {

	/**
	 * Gets a list of all interfaces a class currently implements.
	 * 
	 * @param clazz
	 *            The class.
	 * @param recursive
	 *            If the search is recursive (includes superclasses, and
	 *            interfaces which extend other interfaces).
	 * @return The list of all visible interfaces.
	 */
	public static List<String> getInterfacesOf(Class<?> clazz, boolean recursive) {
		ArrayList<String> ifaces = new ArrayList<String>();
		for (Class<?> i : clazz.getInterfaces()) {
			if (!ifaces.contains(i.getName()))
				ifaces.add(i.getName());
			if (recursive && i.getSuperclass() != null) {
				List<String> super_iface_ifaces = getInterfacesOf(i.getSuperclass(), recursive);
				for (String super_iface_iface : super_iface_ifaces)
					if (!ifaces.contains(super_iface_iface))
						ifaces.add(super_iface_iface);
			}
		}
		if (recursive && clazz.getSuperclass() != null) {
			List<String> super_ifaces = getInterfacesOf(clazz.getSuperclass(), recursive);
			for (String super_iface : super_ifaces)
				if (!ifaces.contains(super_iface))
					ifaces.add(super_iface);
		}
		return ifaces;
	}
}
