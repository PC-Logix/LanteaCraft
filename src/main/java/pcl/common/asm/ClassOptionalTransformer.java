package pcl.common.asm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import net.minecraft.launchwrapper.IClassTransformer;

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
					if (a.desc.equals("Lpcl/common/asm/RuntimeAnnotation$RuntimeInterface;")) {
						int k = a.values.indexOf("modid"), l = a.values.indexOf("clazz");
						String modname = (String) a.values.get(k + 1), iface = (String) a.values.get(l + 1);
						if (Loader.isModLoaded(modname)) {
							if (!interfaceList.contains(iface)) {
								PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
										String.format("Adding interface %s because mod %s is loaded.", iface, modname));
								interfaceList.add(iface);
							}
						} else
							PCLCoreTransformerPlugin.getLogger()
									.log(Level.FINE,
											String.format("Skipping interface %s because mod %s is not loaded.", iface,
													modname));
					}
		}

		if (interfaceList.size() > 0) {
			PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
					String.format("Performing total %s ASM operations...", interfaceList.size()));
			for (String iface : interfaceList)
				addInterface(classNode, iface);
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		} else {
			return basicClass;
		}
	}

	private void addInterface(ClassNode clazz, String iface) {
		try {
			Class.forName(iface);
		} catch (ClassNotFoundException notfound) {
			PCLCoreTransformerPlugin.getLogger().log(
					Level.WARNING,
					String.format("Attempted to load interface %s into class %s, but it does not exist!", iface,
							clazz.name));
		}
		iface = iface.replace(".", "/");
		if (!clazz.interfaces.contains(iface))
			clazz.interfaces.add(iface);
	}

	/**
	 * Determines if a class currently exists (declared). This does not create
	 * an instance of the class.
	 * 
	 * @param classname
	 *            The class name to test
	 * @return If the class exists or not (declared)
	 */
	private boolean classExists(String classname) {
		PCLCoreTransformerPlugin.getLogger().log(Level.FINE, String.format("Evaluating class: %s", classname));
		try {
			Class.forName(classname, false, this.getClass().getClassLoader());
			return true;
		} catch (ClassNotFoundException notfound) {
			return false;
		}
	}
}
