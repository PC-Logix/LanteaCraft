package lc.coremod;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * ASM manipulation and search helpers
 * 
 * @author AfterLifeLochie
 *
 */
public class ASMAssist {

	/**
	 * Find an annotation node on a method of a specified type
	 * 
	 * @param method
	 *            The method node
	 * @param className
	 *            The annotation class name
	 * @return The matching annotation node, if any
	 */
	public static AnnotationNode findAnnotation(MethodNode method, String className) {
		return findAnnotation(method.visibleAnnotations, className);
	}

	/**
	 * Find an annotation node on a class of a specified type
	 * 
	 * @param clazz
	 *            The class node
	 * @param className
	 *            The annotation class name
	 * @return The matching annotation node, if any
	 */
	public static AnnotationNode findAnnotation(ClassNode clazz, String className) {
		return findAnnotation(clazz.visibleAnnotations, className);
	}

	/**
	 * Find an annotation node on a field of a specified type
	 * 
	 * @param field
	 *            The field node
	 * @param className
	 *            The annotation class name
	 * @return The matching annotation node, if any
	 */
	public static AnnotationNode findAnnotation(FieldNode field, String className) {
		return findAnnotation(field.visibleAnnotations, className);
	}

	/**
	 * Find an annotation node in a list of nodes
	 * 
	 * @param nodes
	 *            The list of nodes
	 * @param className
	 *            The annotation class name
	 * @return The matching annotation node, if any
	 */
	public static AnnotationNode findAnnotation(List<AnnotationNode> nodes, String className) {
		if (nodes == null || nodes.size() == 0)
			return null;
		Iterator<AnnotationNode> iq = nodes.iterator();
		while (iq.hasNext()) {
			AnnotationNode node = iq.next();
			if (node.desc.equals(className))
				return node;
		}
		return null;
	}

	/**
	 * Find a value in an annotation node
	 * 
	 * @param node
	 *            The node object
	 * @param name
	 *            The name key to fetch the value of
	 * @return The value associated with the specified name on the node, or null
	 *         if no such value is declared for the node
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findValue(AnnotationNode node, String name) {
		if (node.values.indexOf(name) == -1)
			return null;
		return (T) node.values.get(node.values.indexOf(name) + 1);
	}

}
