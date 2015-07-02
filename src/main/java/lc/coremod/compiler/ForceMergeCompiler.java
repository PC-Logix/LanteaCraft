package lc.coremod.compiler;

import java.util.List;

import lc.common.LCLog;
import lc.coremod.ASMAssist;
import lc.coremod.LCCompilerException;
import lc.coremod.LCCoreTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

public class ForceMergeCompiler implements ICompilerFeature {

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

		AnnotationNode candidateNode = ASMAssist.findAnnotation(sourceNode, "Llc/api/jit/Merge;");
		if (candidateNode == null)
			return basicClass;

		String sourceClass = ASMAssist.findValue(candidateNode, "klass");
		if (sourceClass == null)
			return basicClass;

		byte[] klass = LCCoreTransformer.$.classCache.getCached(sourceClass);
		if (klass == null) {
			LCLog.warn("Failed to find class %s", sourceClass);
			return basicClass;
		}

		ClassNode srcClass = new ClassNode();
		ClassReader reader = new ClassReader(klass);
		reader.accept(srcClass, 0);

		LCCompilerException[] errors = ClassMerger.mergeClasses(srcClass, sourceNode, false);
		if (errors != null && errors.length != 0) {
			LCLog.warn("%s problems encountered when merging class %s into destination class %s:", errors.length,
					srcClass.name, sourceNode.name);
			for (LCCompilerException exception : errors)
				LCLog.warn("\t%s", exception.getMessage());
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		sourceNode.accept(writer);
		LCLog.debug("Successfully merged and recompiled class %s.", name);
		return writer.toByteArray();
	}

}
