package lc.common.base.generation.scattered;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Random;

import lc.LCRuntime;
import lc.api.defs.IStructureDefinition;
import lc.common.LCLog;
import lc.common.impl.registry.StructureRegistry;
import lc.common.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * LanteaCraft scattered feature map generator class.
 *
 * @author AfterLifeLochie
 *
 */
public final class LCScatteredFeatureGenerator extends MapGenStructure {
	private final StructureRegistry registry;
	private HashMap<ChunkPos, IStructureDefinition> starts;

	/** Default constructor */
	public LCScatteredFeatureGenerator() {
		registry = (StructureRegistry) LCRuntime.runtime.registries().structures();
		starts = new HashMap<ChunkPos, IStructureDefinition>();
	}

	@Override
	public String func_143025_a() {
		return "LanteaCraft";
	}

	private IStructureDefinition findStructureStart(int x, int z) {
		ChunkPos p0 = new ChunkPos(x, z);
		if (starts.containsKey(p0))
			return starts.get(p0);

		IStructureDefinition[] defs = registry.allDefs(LCScatteredFeatureStart.class);
		for (IStructureDefinition def : defs) {
			if (def.canGenerateAt(worldObj, rand, p0.cx, p0.cz)) {
				starts.put(p0, def);
				return def;
			}
		}
		starts.put(p0, null);
		return null;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int par1, int par2) {
		return findStructureStart(par1, par2) != null;
	}

	@Override
	protected StructureStart getStructureStart(int cx, int cz) {
		IStructureDefinition def = findStructureStart(cx, cz);
		if (def != null)
			try {
				Class<? extends StructureStart> start = def.getStructureClass();
				Constructor<? extends StructureStart> ctr = start.getConstructor(World.class, Random.class, int.class,
						int.class);
				return ctr.newInstance(worldObj, rand, cx, cz);
			} catch (Throwable t) {
				LCLog.fatal("Couldn't initialize new structure start for type %s.", def.getName(), t);

			}
		return null;
	}

}
