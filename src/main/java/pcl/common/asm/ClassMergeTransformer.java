package pcl.common.asm;

import java.util.ArrayList;
import java.util.logging.Level;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.Loader;
import pcl.common.util.ImmutablePair;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class ClassMergeTransformer implements IClassTransformer {

	private ArrayList<String> merge = new ArrayList<String>();
	private ArrayList<ImmutablePair<String, String>> mergeConditionally = new ArrayList<ImmutablePair<String, String>>();

	LaunchClassLoader loader = (LaunchClassLoader) this.getClass().getClassLoader();

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		merge.clear();
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		if (classNode.visibleAnnotations != null) {
			for (AnnotationNode a : classNode.visibleAnnotations) {
				if (a.desc.equals("Lpcl/common/asm/ClassMerge$Merge;")) {
					String sourceClass = (String) a.values.get(a.values.indexOf("fromClass") + 1);
					if (!merge.contains(sourceClass))
						merge.add(sourceClass);
				}

				if (a.desc.equals("Lpcl/common/asm/ClassMerge$MergeMod;")) {
					String sourceClass = (String) a.values.get(a.values.indexOf("fromClass") + 1);
					String condMod = (String) a.values.get(a.values.indexOf("modName") + 1);
					if (Loader.isModLoaded(condMod) && !merge.contains(sourceClass))
						merge.add(sourceClass);
				}
			}
		}

		if (merge.size() > 0) {
			PCLCoreTransformerPlugin.getLogger().log(Level.INFO,
					String.format("Performing %s merge operations.", merge.size()));
			for (String classname : merge) {
				try {
					byte[] obj = loader.getClassBytes(classname.replace('.', '/'));
					ClassNode objNode = new ClassNode();
					ClassReader objRead = new ClassReader(obj);
					objRead.accept(objNode, 0);
					for (String oint : objNode.interfaces)
						if (!classNode.interfaces.contains(oint))
							classNode.interfaces.add(oint);
					for (FieldNode field : objNode.fields)
						if (!classNode.fields.contains(field))
							classNode.fields.add(field);
					for (MethodNode method : objNode.methods)
						if (!classNode.methods.contains(method))
							classNode.methods.add(method);
				} catch (Throwable t) {
					PCLCoreTransformerPlugin.getLogger().log(Level.INFO, "Class loading failure.", t);
					merge.clear();
					return basicClass;
				}
			}

			merge.clear();
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(writer);
			return writer.toByteArray();
		} else {
			merge.clear();
			return basicClass;
		}
	}
}
