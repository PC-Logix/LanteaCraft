package lc.coremod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lc.api.components.IntegrationType;
import lc.api.drivers.DeviceDrivers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * Takes {@link DeviceDrivers} rules in a load-time binary base class and
 * evaluates them.
 * 
 * @author AfterLifeLochie
 */
public class DriverBindingTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName,
			byte[] basicClass) {
		if (!name.startsWith("lc."))
			return basicClass;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		List<AnnotationNode> annotations = classNode.visibleAnnotations;
		if (annotations != null)
			for (Iterator<AnnotationNode> i = annotations.iterator(); i
					.hasNext();) {
				AnnotationNode annotation = i.next();
				if (annotation.desc
						.equals("Llc/api/drivers/DeviceDrivers$DriverCandidate;")) {
					ArrayList<IntegrationType> types = new ArrayList<IntegrationType>();
					for (Object o : (ArrayList<Object>) annotation.values
							.get(annotation.values.indexOf("types") + 1)) {
						if (o instanceof String[]) {
							String[] params = (String[]) o;
							for (int q = 1; q < params.length; q += 2) {
								IntegrationType type = IntegrationType
										.valueOf(params[q]);
								if (!types.contains(type))
									types.add(type);
							}
						}
					}

					for (IntegrationType type : types)
						System.out.println(type);
				}
			}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
