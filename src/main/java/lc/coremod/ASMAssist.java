package lc.coremod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
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

	/**
	 * Generate a signature for a method.
	 * 
	 * @param aMethod
	 *            The method.
	 * @return A signature.
	 */
	public static String signature(MethodNode aMethod) {
		return signature(aMethod.name, aMethod.desc);
	}

	/**
	 * Generate a signature for a field.
	 * 
	 * @param aField
	 *            The field.
	 * @return A signature.
	 */
	public static String signature(FieldNode aField) {
		return signature(aField.name, aField.desc);
	}

	/**
	 * Generate a signature on a patter1n
	 * 
	 * @param aName
	 *            The object path
	 * @param aDesc
	 *            The object description
	 * @return The formalized signature, usually path + description
	 */
	public static String signature(String aName, String aDesc) {
		return new StringBuilder().append(aName).append(aDesc).toString();
	}

	/**
	 * Find all instances of a method with a given name in a class.
	 * 
	 * @param clazz
	 *            The class to search
	 * @param name
	 *            The method name
	 * @return All instances of the method in the class.
	 */
	public static MethodNode[] findMethod(ClassNode clazz, String name) {
		ArrayList<MethodNode> result = new ArrayList<MethodNode>();
		if (clazz.methods.size() != 0) {
			for (MethodNode method : clazz.methods)
				if (method.name.equals(name))
					result.add(method);
		}
		return result.toArray(new MethodNode[0]);
	}

	/**
	 * Find a method with a particular signature in a class.
	 * 
	 * @param clazz
	 *            The class to search
	 * @param name
	 *            The method name
	 * @param desc
	 *            The method description
	 * @return The method, or null.
	 */
	public static MethodNode findMethod(ClassNode clazz, String name, String desc) {
		MethodNode result = null;
		if (clazz.methods.size() != 0) {
			String signature = ASMAssist.signature(name, desc);
			for (MethodNode method : clazz.methods)
				if (ASMAssist.signature(method).equals(signature))
					result = method;
		}
		return result;
	}

	/**
	 * Find a field in a class
	 * 
	 * @param clazz
	 *            The class
	 * @param name
	 *            The field name
	 * @return The field, or null.
	 */
	public static FieldNode findField(ClassNode clazz, String name) {
		FieldNode result = null;
		if (clazz.fields.size() != 0) {
			for (FieldNode field : clazz.fields)
				if (field.name.equals(name))
					result = field;
		}
		return result;
	}

	/**
	 * Determine if a provided method is empty. A method is empty if it only
	 * contains a return op (ARETURN, RETURN) and does not include any
	 * operations.
	 * 
	 * @param method
	 *            The method to inspect
	 * @return If the method is empty or not
	 */
	public static boolean isMethodEmpty(MethodNode method) {
		if (method.instructions == null || method.instructions.size() == 0)
			return true;
		Iterator<AbstractInsnNode> itx = method.instructions.iterator();
		while (itx.hasNext()) {
			AbstractInsnNode insn = itx.next();
			int opcode = insn.getOpcode();
			if (opcode != Opcodes.ARETURN && opcode != Opcodes.RETURN)
				return false;
		}
		return true;
	}

}