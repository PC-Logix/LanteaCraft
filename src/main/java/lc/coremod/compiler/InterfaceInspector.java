package lc.coremod.compiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import lc.coremod.ASMAssist;
import lc.coremod.LCCompilerException;

/**
 * Interface introspector utility.
 * 
 * @author AfterLifeLochie
 *
 */
public class InterfaceInspector {

	/**
	 * <p>
	 * Introspect an implementation of an interface in a class. Given the
	 * ClassNode for the Interface and the Class in question, the introspector
	 * will assess whether the given Class is a valid implementation (and thus
	 * instanceof) an Interface.
	 * </p>
	 * <p>
	 * The introspector returns a list of missing interface functional
	 * implementations which can be used to detect and avoid
	 * {@link IncompatibleClassChangeError}s from being raised at launch-time.
	 * </p>
	 * 
	 * @param intDec
	 *            The interface declaration
	 * @param impl
	 *            The class declaration
	 * @return The list of errors when asserting that class is a well-formed
	 *         instance of the provided class, or an empty list if no mismatch
	 *         and no other errors were detected.
	 */
	public static LCCompilerException[] introspectImplementation(ClassNode intDec, ClassNode impl) {
		ArrayList<LCCompilerException> errors = new ArrayList<LCCompilerException>();
		introspectMethods(intDec, impl, errors);
		return errors.toArray(new LCCompilerException[0]);
	}

	private static void introspectMethods(ClassNode intDec, ClassNode impl, List<LCCompilerException> errors) {
		if (intDec.methods == null || intDec.methods.size() == 0)
			return;
		for (MethodNode absIntMethod : intDec.methods) {
			if (ASMAssist.findMethod(impl, absIntMethod.name, absIntMethod.desc) == null)
				errors.add(new LCCompilerException("Missing interface functional implementation: "
						+ ASMAssist.signature(absIntMethod)));
		}
	}
}
