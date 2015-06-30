package lc.coremod.compiler;

import java.util.List;

import lc.common.LCLog;
import lc.coremod.ASMAssist;

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

		String sourceClass = ASMAssist.findValue(candidateNode, "types");

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		sourceNode.accept(writer);
		LCLog.debug("Successfully injected and recompiled class %s.", name);
		return writer.toByteArray();
	}

}
