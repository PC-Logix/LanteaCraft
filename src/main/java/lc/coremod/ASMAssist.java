package lc.coremod;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMAssist {

	public static AnnotationNode findAnnotation(MethodNode method, String className) {
		return findAnnotation(method.visibleAnnotations, className);
	}

	public static AnnotationNode findAnnotation(ClassNode clazz, String className) {
		return findAnnotation(clazz.visibleAnnotations, className);
	}

	public static AnnotationNode findAnnotation(FieldNode field, String className) {
		return findAnnotation(field.visibleAnnotations, className);
	}

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

	public static <T> T findValue(AnnotationNode node, String name) {
		if (node.values.indexOf(name) == -1)
			return null;
		return (T) node.values.get(node.values.indexOf(name) + 1);
	}

}
