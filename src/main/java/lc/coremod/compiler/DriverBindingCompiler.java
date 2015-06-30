package lc.coremod.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lc.api.components.DriverMap;
import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers;
import lc.common.LCLog;
import lc.coremod.ASMAssist;
import lc.coremod.LCCompilerException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Takes {@link DeviceDrivers} rules in a load-time binary base class and
 * evaluates them.
 * 
 * @author AfterLifeLochie
 */
public class DriverBindingCompiler implements ICompilerFeature {

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

	@Override
	public byte[] compile(String name, String transformedName, byte[] basicClass) {
		if (!name.startsWith("lc."))
			return basicClass;

		ClassNode sourceNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(sourceNode, 0);
		/* If there are no annotations, there is nothing useful to do */
		List<AnnotationNode> annotations = sourceNode.visibleAnnotations;
		if (annotations == null)
			return basicClass;

		AnnotationNode providerNode = ASMAssist.findAnnotation(sourceNode,
				"Llc/api/jit/DeviceDrivers$DriverProvider;");
		if (providerNode != null) {
			LCLog.debug("Found definition driver class %s.", name);
			driverImplCache.put(name, basicClass.clone());
			return basicClass;
		}

		AnnotationNode candidateNode = ASMAssist.findAnnotation(sourceNode,
				"Llc/api/jit/DeviceDrivers$DriverCandidate;");
		if (candidateNode == null)
			return basicClass;

		ArrayList<IntegrationType> types = new ArrayList<IntegrationType>();
		HashMap<String, String> events = new HashMap<String, String>();
		ArrayList<Object> typeList = ASMAssist
				.findValue(candidateNode, "types");
		for (Object typeName : typeList)
			if (typeName instanceof String[]) {
				String[] params = (String[]) typeName;
				for (int q = 1; q < params.length; q += 2) {
					IntegrationType type = IntegrationType.valueOf(params[q]);
					if (!types.contains(type))
						types.add(type);
				}
			}

		for (IntegrationType type : types) {
			LCLog.debug("Adding drivers for type %s on class %s.", type, name);
			for (DriverMap mapping : DriverMap.mapOf(type)) {
				LCLog.debug("Binding mapping %s (mod %s)", mapping,
						mapping.modName);
				byte[] driverSrc = findClass(mapping.className);
				if (driverSrc == null) {
					LCLog.warn(
							"Can't find class %s for driver %s, skipping...",
							mapping.className, mapping);
					continue;
				}
				try {
					mapping.trySpinUpDriver();
				} catch (Exception failure) {
					LCLog.fatal(
							"Failed to spin up driver manager class %s for driver %s. Problems may occur.",
							mapping.managerClassName, mapping);
				}
				ClassNode driverClass = new ClassNode();
				ClassReader reader = new ClassReader(driverSrc);
				reader.accept(driverClass, 0);
				LCCompilerException[] errors = ClassMerger.mergeClasses(
						driverClass, sourceNode, false);
				if (errors != null && errors.length != 0) {
					LCLog.fatal("Problems encountered when recompiling class.");
					for (LCCompilerException exception : errors)
						LCLog.fatal(exception);
				}
			}
		}

		for (MethodNode method : sourceNode.methods) {
			AnnotationNode callback = ASMAssist.findAnnotation(method,
					"Llc/api/jit/DeviceDrivers$DriverRTCallback;");
			if (callback != null) {
				String callMethod = ASMAssist.findValue(callback, "event");
				if (callMethod != null)
					events.put(method.name, callMethod);
			}
		}

		if (events.size() > 0) {
			LCLog.debug("Adding %s event hooks.", events.size());
			boolean hasUserInit = false;
			for (Object o : sourceNode.methods) {
				MethodNode method = (MethodNode) o;
				if (method.name.equals("<clinit>")) {
					LCLog.debug("Moving user's <clinit> block to user_clinit...");
					method.name = "user_clinit";
					method.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
					hasUserInit = true;
					break;
				}
			}

			MethodNode classInitializer = new MethodNode(Opcodes.ACC_STATIC,
					"<clinit>", Type.getMethodDescriptor(Type.VOID_TYPE,
							new Type[0]), null, null);
			sourceNode.methods.add(0, classInitializer);
			classInitializer.visitCode();
			for (Entry<String, String> eventMapItem : events.entrySet()) {
				classInitializer.visitLdcInsn(Type.getObjectType(name.replace(
						".", "/")));
				classInitializer.visitLdcInsn(eventMapItem.getKey());
				classInitializer.visitLdcInsn(eventMapItem.getValue());
				classInitializer
						.visitMethodInsn(
								Opcodes.INVOKESTATIC,
								"lc/common/base/LCTile",
								"registerCallback",
								"(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)V",
								false);
			}
			if (hasUserInit)
				classInitializer.visitMethodInsn(Opcodes.INVOKESTATIC,
						sourceNode.name, "user_clinit",
						Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]),
						false);
			classInitializer.visitInsn(Opcodes.RETURN);
			classInitializer.visitMaxs(3, 0);
			classInitializer.visitEnd();
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		sourceNode.accept(writer);
		LCLog.debug("Successfully injected and recompiled class %s.", name);
		return writer.toByteArray();
	}
}
