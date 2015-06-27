package lc.coremod.compiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import lc.api.defs.HintProvider;
import lc.common.LCLog;
import lc.coremod.ASMAssist;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * Takes {@link HintProvider} rules in a load-time binary base class and
 * evaluates them.
 *
 * @author AfterLifeLochie
 */
public class HintInjectionCompiler implements ICompilerFeature {

	@Override
	public byte[] compile(String name, String transformedName, byte[] basicClass) {
		if (!name.startsWith("lc."))
			return basicClass;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		List<FieldNode> fields = classNode.fields;
		if (fields == null || fields.size() == 0)
			return basicClass;
		HashMap<String, String> fieldToClazzMap = new HashMap<String, String>();
		Iterator<FieldNode> iq = fields.iterator();
		while (iq.hasNext()) {
			FieldNode field = iq.next();
			AnnotationNode hintNode = ASMAssist.findAnnotation(field, "Llc/api/defs/HintProvider;");
			if (hintNode != null) {
				Side theSide = FMLCommonHandler.instance().getSide();
				if (theSide == Side.CLIENT) {
					String className = ASMAssist.findValue(hintNode, "clientClass");
					if (className != null)
						fieldToClazzMap.put(field.name, className.replace(".", "/"));
				} else {
					String className = ASMAssist.findValue(hintNode, "serverClass");
					if (className != null)
						fieldToClazzMap.put(field.name, className.replace(".", "/"));
				}
			}
		}

		if (fieldToClazzMap.size() != 0) {
			LCLog.debug("Adding %s hint field initializer mappings.", fieldToClazzMap.size());
			Iterator<MethodNode> methods = classNode.methods.iterator();
			MethodNode initMethod = null;
			while (methods.hasNext()) {
				MethodNode method = methods.next();
				if (method.name.equals("<init>")) {
					initMethod = method;
					break;
				}
			}

			AbstractInsnNode lastInsn = null;
			if (initMethod == null) {
				initMethod = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", "", null);
				lastInsn = new InsnNode(Opcodes.RETURN);
				initMethod.instructions.add(lastInsn);
			} else
				for (Iterator<AbstractInsnNode> insns = initMethod.instructions.iterator(); insns.hasNext();) {
					AbstractInsnNode node = insns.next();
					if (node.getOpcode() == Opcodes.RETURN)
						lastInsn = node;
				}

			for (Entry<String, String> mapping : fieldToClazzMap.entrySet()) {
				LCLog.debug("Adding mapping for hint %s to class initializer %s.", mapping.getKey(), mapping.getValue());
				initMethod.instructions.insertBefore(lastInsn, new VarInsnNode(Opcodes.ALOAD, 0));
				initMethod.instructions.insertBefore(lastInsn, new TypeInsnNode(Opcodes.NEW, mapping.getValue()));
				initMethod.instructions.insertBefore(lastInsn, new InsnNode(Opcodes.DUP));
				initMethod.instructions.insertBefore(lastInsn,
						new MethodInsnNode(Opcodes.INVOKESPECIAL, mapping.getValue(), "<init>", "()V", false));
				initMethod.instructions.insertBefore(lastInsn,
						new FieldInsnNode(Opcodes.PUTFIELD, name.replace(".", "/"), mapping.getKey(),
								"Llc/common/IHintProvider;"));
			}

			if (initMethod.maxStack < fieldToClazzMap.size() * 4)
				initMethod.maxStack += fieldToClazzMap.size() * 4;
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
