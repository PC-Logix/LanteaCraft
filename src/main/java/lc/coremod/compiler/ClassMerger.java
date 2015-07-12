package lc.coremod.compiler;

import java.util.ArrayList;
import java.util.List;

import lc.coremod.ASMAssist;
import lc.coremod.LCCompilerException;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Class merging utility.
 * 
 * @author AfterLifeLochie
 *
 */
public class ClassMerger {

	/**
	 * Merge two ClassNode objects together, writing directly to the destination
	 * ClassNode without making a copy.
	 * 
	 * @param source
	 *            The source class node
	 * @param destination
	 *            The destination class node
	 * @param force
	 *            If any pre-compile conditions should be ignored
	 * @return Any compile exceptions, if any.
	 */
	public static LCCompilerException[] mergeClasses(ClassNode source, ClassNode destination, boolean force) {
		ArrayList<LCCompilerException> errors = new ArrayList<LCCompilerException>();
		walkMergePreconditions(source, destination, errors);
		if (!force && errors.size() != 0) {
			errors.add(new LCCompilerException("Preconditions failed; aborting..."));
			return errors.toArray(new LCCompilerException[0]);
		}
		performMerge(source, destination, errors);
		if (errors.size() != 0)
			return errors.toArray(new LCCompilerException[0]);
		return null;
	}

	private static void walkMergePreconditions(ClassNode source, ClassNode dest, List<LCCompilerException> errors) {
		if (source.fields != null && source.fields.size() != 0) {
			for (FieldNode field : source.fields)
				if (hasDuplicateField(field, dest))
					errors.add(new LCCompilerException("Duplicate field: " + ASMAssist.signature(field)));
		}

		if (source.interfaces != null && source.interfaces.size() != 0) {
			for (String iface : source.interfaces)
				if (hasDuplicateInterface(iface, dest))
					errors.add(new LCCompilerException("Duplicate interface: " + iface));
		}

		if (source.methods != null && source.methods.size() != 0) {
			for (MethodNode srcMethod : source.methods) {
				if (hasDuplicateMethod(srcMethod, dest)) {
					if (ASMAssist.isMethodEmpty(srcMethod) || srcMethod.name.equals("<init>"))
						continue;
					MethodNode dstMethod = ASMAssist.findMethod(dest, srcMethod.name, srcMethod.desc);
					if (ASMAssist.isMethodEmpty(dstMethod))
						continue;
					errors.add(new LCCompilerException("Duplicate non-empty method import: "
							+ ASMAssist.signature(srcMethod)));
				}
			}
		}

	}

	private static boolean hasDuplicateMethod(MethodNode theMethod, ClassNode theClass) {
		return (ASMAssist.findMethod(theClass, theMethod.name, theMethod.desc) != null);
	}

	private static boolean hasDuplicateField(FieldNode theField, ClassNode theClass) {
		return (ASMAssist.findField(theClass, theField.name) != null);
	}

	private static boolean hasDuplicateInterface(String theInterface, ClassNode theClass) {
		if (theClass.interfaces == null || theClass.interfaces.size() == 0)
			return false;
		for (String iface : theClass.interfaces)
			if (theInterface.equals(iface))
				return true;
		return false;
	}

	private static void performMerge(ClassNode source, ClassNode dest, List<LCCompilerException> errors) {
		if (source.interfaces != null)
			for (String iface : source.interfaces) {
				if (dest.interfaces == null)
					dest.interfaces = new ArrayList<String>();
				if (!hasDuplicateInterface(iface, dest)) {
					dest.interfaces.add(iface);
				} else
					errors.add(new LCCompilerException(String.format(
							"Skipped interface %s, interface already applied in class %s.", iface, source.name)));
			}
		if (source.methods != null)
			for (MethodNode method : source.methods)
				if (!hasDuplicateMethod(method, dest)) {
					dest.methods.add(remapMethod(source.name, dest.name, method));
				} else
					errors.add(new LCCompilerException(String.format(
							"Skipped method %s#%s, method with duplicate signature already exists in class %s.",
							source.name, ASMAssist.signature(method), dest.name)));
		if (source.fields != null)
			for (FieldNode field : source.fields)
				if (!hasDuplicateField(field, dest))
					dest.fields.add(remapField(source.name, dest.name, field));
				else
					errors.add(new LCCompilerException(String.format(
							"Skipped field %s#%s, field with duplicate signature already exists in class %s.",
							source.name, ASMAssist.signature(field), dest.name)));
	}

	private static MethodNode remapMethod(String sourceName, String destName, MethodNode master) {
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

	private static FieldNode remapField(String sourceName, String destName, FieldNode master) {
		return master;
	}

}
