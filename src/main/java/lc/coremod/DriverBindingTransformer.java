package lc.coremod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import lc.api.components.DriverMap;
import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers;
import lc.common.LCLog;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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

	/**
	 * Generate a signature for a method.
	 *
	 * @param aMethod
	 *            The method.
	 * @return A signature.
	 */
	private String signature(MethodNode aMethod) {
		return new StringBuilder().append(aMethod.name).append(aMethod.desc).toString();
	}

	/**
	 * Generate a signature for a field.
	 *
	 * @param aField
	 *            The field.
	 * @return A signature.
	 */
	private String signature(FieldNode aField) {
		return new StringBuilder().append(aField.name).append(aField.desc).toString();
	}

	/**
	 * Determines if a class provided already has a method of the type
	 * specified. This doesn't check for exceptions, but will detect identical
	 * signature issues.
	 *
	 * @param theMethod
	 *            The method node
	 * @param theClass
	 *            The class node
	 * @return If method type with return type and param args with name already
	 *         exists in classnode class provided.
	 */
	private boolean hasDuplicateMethod(MethodNode theMethod, ClassNode theClass) {
		if (theClass.methods == null || theClass.methods.size() == 0)
			return false;
		String signature = signature(theMethod);
		for (MethodNode method : theClass.methods)
			if (signature(method).equalsIgnoreCase(signature))
				return true;
		return false;
	}

	/**
	 * Determines if a class provided already has a field of the type specified.
	 *
	 * @param theField
	 *            The field node
	 * @param theClass
	 *            The class node
	 * @return If field type with name already exists in classnode class
	 *         provided.
	 */
	private boolean hasDuplicateField(FieldNode theField, ClassNode theClass) {
		if (theClass.fields == null || theClass.fields.size() == 0)
			return false;
		String signature = signature(theField);
		for (FieldNode field : theClass.fields)
			if (signature(field).equalsIgnoreCase(signature))
				return true;
		return false;
	}

	/**
	 * Remap a method from one owner container to another owner container.
	 * Performs all operations on the MethodNode provided directly (no copy).
	 *
	 * @param sourceName
	 *            The source container type.
	 * @param destName
	 *            The destination container type.
	 * @param master
	 *            The root MethodNode element.
	 * @return The remapped MethodNode, not a copy of the root provided.
	 */
	private MethodNode remapMethod(String sourceName, String destName, MethodNode master) {
		InsnList instructions = master.instructions;
		for (int i = 0, j = instructions.size(); i < j; i++) {
			AbstractInsnNode instruction = instructions.get(i);
			if (instruction instanceof MethodInsnNode) {
				MethodInsnNode callable = (MethodInsnNode) instruction;
				if (callable.owner.equals(sourceName))
					callable.owner = destName;
			} else if (instruction instanceof FieldInsnNode) {
				FieldInsnNode fieldop = (FieldInsnNode) instruction;
				if (fieldop.owner.equals(sourceName))
					fieldop.owner = destName;
			}
		}
		return master;
	}

	/**
	 *
	 * @param sourceName
	 * @param destName
	 * @param master
	 * @return
	 */
	private FieldNode remapField(String sourceName, String destName, FieldNode master) {
		return master;
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!name.startsWith("lc."))
			return basicClass;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		List<AnnotationNode> annotations = classNode.visibleAnnotations;
		if (annotations == null)
			return basicClass;

		AnnotationNode providerNode = ASMAssist.findAnnotation(classNode, "Llc/api/jit/DeviceDrivers$DriverProvider;");
		if (providerNode != null) {
			LCLog.debug("Found definition driver class %s.", name);
			driverImplCache.put(name, basicClass.clone());
			return basicClass;
		}

		AnnotationNode candidateNode = ASMAssist
				.findAnnotation(classNode, "Llc/api/jit/DeviceDrivers$DriverCandidate;");
		if (candidateNode == null)
			return basicClass;

		ArrayList<IntegrationType> types = new ArrayList<IntegrationType>();
		HashMap<String, String> events = new HashMap<String, String>();
		ArrayList<Object> typeList = ASMAssist.findValue(candidateNode, "types");
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
			LCLog.debug("Adding drivers for type %s.", type);
			for (DriverMap mapping : DriverMap.mapOf(type)) {
				LCLog.debug("Binding mapping %s (mod %s)", mapping, mapping.modName);
				byte[] driverSrc = findClass(mapping.className);
				if (driverSrc == null) {
					LCLog.warn("Can't find class %s for driver %s, skipping...", mapping.className, mapping);
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
				if (driverClass.methods != null)
					for (MethodNode method : driverClass.methods)
						if (!hasDuplicateMethod(method, classNode)) {
							classNode.methods.add(remapMethod(driverClass.name, classNode.name, method));
							AnnotationNode callback = ASMAssist.findAnnotation(method,
									"Llc/api/jit/DeviceDrivers$DriverRTCallback;");
							if (callback != null) {
								String callMethod = ASMAssist.findValue(callback, "event");
								if (callMethod != null)
									events.put(method.name, callMethod);
							}
						} else
							LCLog.warn("Skipping method %s, already present!", signature(method));
				if (driverClass.fields != null)
					for (FieldNode field : driverClass.fields)
						if (!hasDuplicateField(field, classNode))
							classNode.fields.add(remapField(driverClass.name, classNode.name, field));
						else
							LCLog.warn("Skipping field %s, already present!", signature(field));
			}
		}

		if (events.size() > 0) {
			LCLog.debug("Adding %s event hooks.", events.size());
			boolean hasUserInit = false;
			for (Object o : classNode.methods) {
				MethodNode method = (MethodNode) o;
				if (method.name.equals("<clinit>")) {
					LCLog.debug("Moving user's <clinit> block to user_clinit...");
					method.name = "user_clinit";
					method.access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;
					hasUserInit = true;
					break;
				}
			}

			MethodNode classInitializer = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", Type.getMethodDescriptor(
					Type.VOID_TYPE, new Type[0]), null, null);
			classNode.methods.add(0, classInitializer);
			classInitializer.visitCode();
			for (Entry<String, String> eventMapItem : events.entrySet()) {
				classInitializer.visitLdcInsn(Type.getObjectType(name.replace(".", "/")));
				classInitializer.visitLdcInsn(eventMapItem.getKey());
				classInitializer.visitLdcInsn(eventMapItem.getValue());
				classInitializer.visitMethodInsn(Opcodes.INVOKESTATIC, "lc/common/base/LCTile", "registerCallback",
						"(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)V");
			}
			if (hasUserInit)
				classInitializer.visitMethodInsn(Opcodes.INVOKESTATIC, classNode.name, "user_clinit",
						Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]));
			classInitializer.visitInsn(Opcodes.RETURN);
			classInitializer.visitMaxs(3, 0);
			classInitializer.visitEnd();
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
