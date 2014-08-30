package lc.coremod;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lc.api.components.DriverMap;
import lc.api.components.IntegrationType;
import lc.api.drivers.DeviceDrivers;
import lc.common.LCLog;
import lc.core.BuildInfo;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * Takes {@link DeviceDrivers} rules in a load-time binary base class and
 * evaluates them.
 * 
 * @author AfterLifeLochie
 */
public class DriverBindingTransformer implements IClassTransformer {

	/** Cache of all byte[] implementations for drivers */
	private HashMap<String, byte[]> driverImplCache = new HashMap<String, byte[]>();

	/**
	 * Find a class in the cache. This is cheaty because it tricks the runtime
	 * into feeding us the byte[] of the class we're interested in if we haven't
	 * seen them yet. If we have seen them, we can just return what we already
	 * have.
	 * 
	 * @param className
	 *            The class to find.
	 * @return The byte[] of the class definition or null if the class doesn't
	 *         exist or can't be loaded.
	 */
	private byte[] findClass(String className) {
		if (!driverImplCache.containsKey(className))
			try {
				getClass().getClassLoader().loadClass(className);
			} catch (ClassNotFoundException err) {
				LCLog.warn("Can't find class %s.", className);
			}
		return driverImplCache.get(className);
	}

	private String signature(MethodNode aMethod) {
		return new StringBuilder().append(aMethod.name).append(aMethod.desc).toString();
	}

	private boolean hasDuplicateMethod(MethodNode theMethod, ClassNode theClass) {
		if (theClass.methods == null || theClass.methods.size() == 0)
			return false;
		String signature = signature(theMethod);
		for (MethodNode method : theClass.methods) {
			if (signature(method).equalsIgnoreCase(signature))
				return true;
		}
		return false;
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!name.startsWith("lc."))
			return basicClass;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		List<AnnotationNode> annotations = classNode.visibleAnnotations;
		if (annotations != null)
			for (Iterator<AnnotationNode> i = annotations.iterator(); i.hasNext();) {
				AnnotationNode annotation = i.next();
				if (annotation.desc.equals("Llc/api/drivers/DeviceDrivers$DriverProvider;")) {
					LCLog.debug("Found definition driver class %s.", name);
					driverImplCache.put(name, basicClass.clone());
					return basicClass;
				} else if (annotation.desc.equals("Llc/api/drivers/DeviceDrivers$DriverCandidate;")) {
					ArrayList<IntegrationType> types = new ArrayList<IntegrationType>();
					for (Object o : (ArrayList<Object>) annotation.values.get(annotation.values.indexOf("types") + 1)) {
						if (o instanceof String[]) {
							String[] params = (String[]) o;
							for (int q = 1; q < params.length; q += 2) {
								IntegrationType type = IntegrationType.valueOf(params[q]);
								if (!types.contains(type))
									types.add(type);
							}
						}
					}

					for (IntegrationType type : types) {
						LCLog.debug("Adding drivers for type %s.", type);
						EnumSet<DriverMap> mappings = DriverMap.mapOf(type);
						for (DriverMap mapping : mappings) {
							LCLog.debug("Binding mapping %s (mod %s)", mapping, mapping.modName);
							byte[] driverSrc = findClass(mapping.className);
							if (driverSrc == null) {
								LCLog.warn("Can't find class %s for driver %s, abort.", mapping.className, mapping);
								continue;
							}
							ClassNode driverClass = new ClassNode();
							ClassReader reader = new ClassReader(driverSrc);
							reader.accept(driverClass, 0);

							if (driverClass.interfaces != null)
								for (String iface : driverClass.interfaces) {
									if (classNode.interfaces == null)
										classNode.interfaces = new ArrayList<String>();
									if (!classNode.interfaces.contains(iface))
										classNode.interfaces.add(iface);
								}
							if (driverClass.methods != null) {
								for (MethodNode method : driverClass.methods)
									if (!hasDuplicateMethod(method, classNode)) {
										LCLog.debug("Adding method %s to destination class.", signature(method));
										classNode.methods.add(method);
									} else
										LCLog.debug(
												"Skipping method %s because it already exists in destination class.",
												signature(method));
							}
						}
					}
				}
			}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		if (BuildInfo.DEBUG) {
			try {
				if (!new File("vm/").exists())
					new File("vm/").mkdir();
				File saveObj = new File("vm/" + name.replace("/", "_").replace(".", "_") + ".class");
				if (saveObj.exists())
					saveObj.delete();
				FileOutputStream output = new FileOutputStream(saveObj);
				output.write(writer.toByteArray());
				output.close();
			} catch (Throwable t) {
				LCLog.fatal("Failed to save runtime implementation of class %s.", t);
			}
		}
		return writer.toByteArray();
	}
}
