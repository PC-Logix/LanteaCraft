package lc.coremod.compiler;

import java.util.ArrayList;
import java.util.Iterator;

import lc.common.LCLog;
import lc.coremod.RuntimeAnnotation;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.Loader;

/**
 * Takes {@link RuntimeAnnotation} optional rules in a load-time binary base
 * class and evaluates them.
 *
 * @author AfterLifeLochie
 */
public class ClassOptionalCompiler implements ICompilerFeature {

	@Override
	public byte[] compile(String name, String transformedName, byte[] basicClass) {
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

	/**
	 * Add an interface to a class node. Modifies the class node directly (no
	 * copy). If the interface has not been loaded by the classloader yet, it
	 * will be loaded. If the interface cannot be loaded by the classloader, the
	 * interface will not be added to the class.
	 * 
	 * @param clazz
	 *            The class node
	 * @param iface
	 *            The interface to add
	 */
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
}
