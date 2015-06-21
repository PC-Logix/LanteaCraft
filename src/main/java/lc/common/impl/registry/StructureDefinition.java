package lc.common.impl.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import lc.api.defs.IDefinitionReference;
import lc.api.defs.IStructureDefinition;
import lc.api.jit.AnyPredicate;
import lc.common.LCLog;

/**
 * Structure definition implementation
 * 
 * @author AfterLifeLochie
 *
 */
public abstract class StructureDefinition implements IStructureDefinition {

	private final String name;
	private final Class<? extends StructureStart> startClazz;
	private final HashMap<String, Class<? extends StructureComponent>> components;

	/**
	 * Default constructor
	 * 
	 * @param name
	 *            The name of the structure type
	 * @param startClazz
	 *            The StructureStart class
	 */
	public StructureDefinition(String name, Class<? extends StructureStart> startClazz) {
		this.name = name;
		this.startClazz = startClazz;
		this.components = new HashMap<String, Class<? extends StructureComponent>>();
	}

	/**
	 * Add a new component to the definition
	 * 
	 * @param name
	 *            The name of the structure
	 * @param component
	 *            The component type
	 * @return The self definition
	 */
	public StructureDefinition addComp(String name, Class<? extends StructureComponent> component) {
		components.put(name, component);
		return this;
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<? extends StructureStart> getStructureClass() {
		return startClazz;
	}

	@Override
	public Map<String, Class<? extends StructureComponent>> getAllComponents() {
		return components;
	}

	@Override
	public boolean canGenerateAt(World world, Random rng, int x, int z) {
		AnyPredicate test = getGeneratorPredicate();
		if (test == null)
			return false;
		try {
			return test.test(new Object[] { world, rng, x, z });
		} catch (Throwable t) {
			LCLog.warn("Failed to test AnyPredicate rule for generation.", t);
			return false;
		}
	}

	/**
	 * <p>
	 * Get the predicate responsible for determining where structure generation
	 * is permitted.
	 * </p>
	 * <p>
	 * The predicate should expect the following parameters:
	 * <ol>
	 * <li>The world object</li>
	 * <li>The random number generator</li>
	 * <li>The x-coordinate of the chunk</li>
	 * <li>The y-coordinate of the chunk</li>
	 * </ol>
	 * </p>
	 * 
	 * @return The predicate responsible for determining where structure
	 *         generation is permitted or disallowed
	 */
	protected abstract AnyPredicate getGeneratorPredicate();

}
