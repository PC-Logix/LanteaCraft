package lc.coremod.compiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import lc.coremod.ASMAssist;
import lc.coremod.LCCompilerException;

public class InterfaceInspector {

	public static LCCompilerException[] introspectImplementation(
			ClassNode intDec, ClassNode impl) {
		ArrayList<LCCompilerException> errors = new ArrayList<LCCompilerException>();
		introspectMethods(intDec, impl, errors);
		return errors.toArray(new LCCompilerException[0]);
	}

	private static void introspectMethods(ClassNode intDec, ClassNode impl,
			List<LCCompilerException> errors) {
		if (intDec.methods == null || intDec.methods.size() == 0)
			return;
		for (MethodNode absIntMethod : intDec.methods) {
			if (ASMAssist
					.findMethod(impl, absIntMethod.name, absIntMethod.desc) == null)
				errors.add(new LCCompilerException(
						"Missing interface functional implementation: "
								+ ASMAssist.signature(absIntMethod)));
		}
	}
}
