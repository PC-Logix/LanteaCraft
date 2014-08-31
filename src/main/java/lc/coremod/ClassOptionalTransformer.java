package lc.coremod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import lc.common.LCLog;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

/**
 * Takes {@link RuntimeAnnotation} optional rules in a load-time binary base
 * class and evaluates them.
 * 
 * @author AfterLifeLochie
 */
public class ClassOptionalTransformer implements IClassTransformer {

	/**
	 * Transforms a provided class.
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		ArrayList<String> interfaceList = new ArrayList<String>();

		Iterator<MethodNode> decMethods = classNode.methods.iterator();
		while (decMethods.hasNext()) {
			MethodNode method = decMethods.next();
			if (method.visibleAnnotations != null)
				for (AnnotationNode a : method.visibleAnnotations)
					if (a.desc.equals("Llc/coremod/RuntimeAnnotation$RuntimeInterface;")) {
						int k = a.values.indexOf("modid"), l = a.values.indexOf("clazz");
						String modname = (String) a.values.get(k + 1), iface = (String) a.values.get(l + 1);
						if (Loader.isModLoaded(modname)) {
							if (!interfaceList.contains(iface)) {
								LCLog.debug("Adding interface %s because mod %s is loaded.", iface, modname);
								interfaceList.add(iface);
							}
						} else
							LCLog.debug("Skipping interface %s because mod %s is not loaded.", iface, modname);
					}
		}

		if (interfaceList.size() > 0) {
			LCLog.debug("Performing total %s ASM operations...", interfaceList.size());
			for (String iface : interfaceList)
				addInterface(classNode, iface);
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		} else
			return basicClass;
	}

	private void addInterface(ClassNode clazz, String iface) {
		try {
			Class.forName(iface);
			iface = iface.replace(".", "/");
			if (!clazz.interfaces.contains(iface))
				clazz.interfaces.add(iface);
		} catch (ClassNotFoundException notfound) {
			LCLog.debug("Attempted to load interface %s into class %s, but it does not exist!", iface, clazz.name);
		}
	}

	private boolean modLoaded(String name, String minVersion, String exactVersion, String maxVersion) {
		Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
		if (!mods.keySet().contains(name))
			return false;
		ModContainer container = mods.get(name);
		if (exactVersion != null)
			return container.getVersion().equals(exactVersion);
		else {
			Object[] v0 = parseVersion(container.getVersion()), v1 = null, v2 = null;
			if (minVersion != null)
				v1 = parseVersion(minVersion);
			if (maxVersion != null)
				v2 = parseVersion(maxVersion);

			if (v1 != null && v0.length != v1.length)
				return false;
			if (v2 != null && v0.length != v2.length)
				return false;

			if (v1 != null)
				for (int k = 0; k < v0.length; k++)
					if (!v0[k].getClass().equals(v1[k].getClass()))
						return false;
			if (v2 != null)
				for (int k = 0; k < v0.length; k++)
					if (!v0[k].getClass().equals(v2[k].getClass()))
						return false;

			if (v1 != null)
				for (int k = 0; k < v0.length; k++) {
					Object o1 = v0[k], o2 = v1[k];
					if (o1 instanceof String)
						continue; // can't compare strings reliably
					else if (o1 instanceof Integer)
						if (((Integer) o2) > ((Integer) o1))
							return false;
						else
							return false;
				}
			if (v2 != null)
				for (int k = 0; k < v0.length; k++) {
					Object o1 = v0[k], o2 = v2[k];
					if (o1 instanceof String)
						continue; // can't compare strings reliably
					else if (o1 instanceof Integer)
						if (((Integer) o2) < ((Integer) o1))
							return false;
						else
							return false;
				}
			return true;
		}
	}

	private Object[] parseVersion(String version) {
		ArrayList<Object> result = new ArrayList<Object>();
		boolean flag0;
		char[] data = version.trim().toCharArray();
		int c0 = countOf(data, '.'), c1 = countOf(data, '-'), c2 = 0;
		char sep;
		if (c0 > c1)
			sep = '.';
		else
			sep = '-';
		while (true) {
			flag0 = (!Character.isDigit(data[0]));
			if (flag0) {
				StringBuilder b0 = new StringBuilder();
				while (c2 < data.length && data[c2] != sep)
					b0.append(data[c2++]);
				c2++;
				result.add(b0.toString());
			} else {
				StringBuilder b1 = new StringBuilder();
				while (c2 < data.length && data[c2] != sep)
					b1.append(data[c2++]);
				c2++;
				result.add(Integer.parseInt(b1.toString()));
			}
			if (c2 >= data.length)
				break;
		}
		return result.toArray();
	}

	private int countOf(char[] d, char c) {
		int i = 0, j = 0;
		for (; i < d.length; i++)
			if (d[i] == c)
				j++;
		return j;
	}
}
