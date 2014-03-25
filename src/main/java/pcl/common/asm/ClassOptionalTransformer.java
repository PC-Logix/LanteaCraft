package pcl.common.asm;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Takes {@link ClassOptional} optional rules in a load-time binary base class
 * and evaluates them. This removes interfaces and methods from the class
 * provided if the interfaces and methods meet the criteria specified in the
 * {@link ClassOptional} rules.
 * 
 * @author AfterLifeLochie
 */
public class ClassOptionalTransformer implements IClassTransformer {

	private ArrayList<String> interfaceQueue = new ArrayList<String>();
	private ArrayList<String> methodQueue = new ArrayList<String>();

	/**
	 * Transforms a provided class.
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		// Even though tailcode is responsible for this, do it anyway
		interfaceQueue.clear();
		methodQueue.clear();

		// Figure out if there are any visible annotations
		if (classNode.visibleAnnotations != null)
			for (AnnotationNode a : classNode.visibleAnnotations) {
				// Annotation belongs to a ClassFilterInterfaceList, iterate
				if (a.desc.equals("Lpcl/common/asm/ClassOptional$ClassFilterInterfaceList;")) {
					PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
							"Found ClassFilterInterfaceList annotation data, processing...");
					int j = a.values.indexOf("value");
					ArrayList<AnnotationNode> nodes = (ArrayList<AnnotationNode>) a.values.get(j + 1);
					// Annotation child is a single ClassFilterInterface, queue
					for (AnnotationNode node : nodes) {
						int k = node.values.indexOf("classname"), l = node.values.indexOf("iface");
						String classname = (String) node.values.get(k + 1), iface = (String) node.values.get(l + 1);
						if (!classExists(classname))
							interfaceQueue.add(iface);
					}
				}

				// Annotation is a single ClassFilterInterface, queue
				if (a.desc.equals("Lpcl/common/asm/ClassOptional$ClassFilterInterface;")) {
					PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
							"Found ClassFilterInterface annotation data, processing...");
					int k = a.values.indexOf("classname"), l = a.values.indexOf("iface");
					String classname = (String) a.values.get(k + 1), iface = (String) a.values.get(l + 1);
					if (!classExists(classname))
						interfaceQueue.add(iface);
				}

				// Annotation belongs to a ClassFilterInterfaceSelfList, iterate
				if (a.desc.equals("Lpcl/common/asm/ClassOptional$ClassFilterInterfaceSelfList;")) {
					PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
							"Found ClassFilterInterfaceSelfList annotation data, processing...");
					int j = a.values.indexOf("value");
					ArrayList<AnnotationNode> nodes = (ArrayList<AnnotationNode>) a.values.get(j + 1);
					// Annotation child is a single ClassFilterInterfaceSelf,
					// queue
					for (AnnotationNode node : nodes) {
						int k = node.values.indexOf("iface");
						String classname = (String) node.values.get(k + 1);
						if (!classExists(classname))
							interfaceQueue.add(classname);
					}
				}

				// Annotation is a single ClassFilterInterfaceSelf, queue
				if (a.desc.equals("Lpcl/common/asm/ClassOptional$ClassFilterInterfaceSelf;")) {
					PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
							"Found ClassFilterInterfaceSelf annotation data, processing...");
					int k = a.values.indexOf("iface");
					String classname = (String) a.values.get(k + 1);
					if (!classExists(classname))
						interfaceQueue.add(classname);
				}
			}

		// Figure out if there are any methods
		if (classNode.methods != null)
			for (MethodNode n : classNode.methods)
				if (n.visibleAnnotations != null)
					for (AnnotationNode a : n.visibleAnnotations)
						// Annotation on method is a single ClassFilterMethod,
						// queue
						if (a.desc.equals("Lpcl/common/asm/ClassOptional$ClassFilterMethod;")) {
							PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
									"Found ClassFilterMethod annotation data, processing...");
							int k = a.values.indexOf("classname");
							String classname = (String) a.values.get(k + 1);
							if (!classExists(classname))
								methodQueue.add(n.name + n.desc);
						}

		// Figure out if there is anything to do
		int ops = interfaceQueue.size() + methodQueue.size();
		if (ops > 0) {
			PCLCoreTransformerPlugin.getLogger().log(Level.FINE,
					String.format("Performing total %s ASM operations...", ops));
			// Apply all interface queue operations
			for (String iface : interfaceQueue)
				stripInterface(classNode, iface);
			// Apply all method queue operations
			for (String method : methodQueue)
				stripMethod(classNode, method);

			// Do some cleanup
			interfaceQueue.clear();
			methodQueue.clear();

			// Flush out the class
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			// Return the modified class
			return writer.toByteArray();
		} else {
			// Do some cleanup
			interfaceQueue.clear();
			methodQueue.clear();
			// Just return what we had originally
			return basicClass;
		}
	}

	/**
	 * Removes a method from a given ClassNode object, described by the
	 * descriptor provided
	 * 
	 * @param classNode
	 *            The ClassNode object to remove the method from
	 * @param methodDescriptor
	 *            The description of the Method to remove
	 */
	private void stripMethod(ClassNode classNode, String methodDescriptor) {
		for (ListIterator<MethodNode> iterator = classNode.methods.listIterator(); iterator.hasNext();) {
			MethodNode method = iterator.next();
			if (methodDescriptor.equals(method.name + method.desc)) {
				iterator.remove();
				PCLCoreTransformerPlugin.getLogger().log(Level.INFO,
						String.format("ClassOptional removal - method `%s` removed", methodDescriptor));
				return;
			}
		}
		PCLCoreTransformerPlugin.getLogger().log(Level.WARNING,
				String.format("ClassOptional removal - method `%s` NOT removed - method not found", methodDescriptor));
	}

	/**
	 * Removes an interface from a given ClassNode object, described the the
	 * interface name provided
	 * 
	 * @param classNode
	 *            The ClassNode object to remove the interface from
	 * @param interfaceName
	 *            The name of the Interface to remove
	 */
	private void stripInterface(ClassNode classNode, String interfaceName) {
		String ifaceName = interfaceName.replace('.', '/');
		boolean found = classNode.interfaces.remove(ifaceName);
		if (found)
			PCLCoreTransformerPlugin.getLogger().log(Level.INFO,
					String.format("ClassOptional removal - interface `%s` removed", interfaceName));
		if (!found)
			PCLCoreTransformerPlugin.getLogger().log(
					Level.WARNING,
					String.format("ClassOptional removal - interface `%s` NOT removed - interface not found",
							interfaceName));
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
