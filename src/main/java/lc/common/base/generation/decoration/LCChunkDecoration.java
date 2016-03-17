package lc.common.base.generation.decoration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import lc.api.defs.IDefinitionReference;
import lc.api.defs.IStructureDefinition;
import lc.common.base.generation.LCChunkData;
import lc.common.impl.registry.DefinitionReference;

public abstract class LCChunkDecoration extends StructureStart implements IStructureDefinition {

	public abstract void decorateChunk(Random random, World world, Chunk chunk, LCChunkData data);

	@Override
	public Class<? extends StructureStart> getStructureClass() {
		return this.getClass();
	}

	@Override
	public Map<String, Class<? extends StructureComponent>> getAllComponents() {
		return new HashMap<String, Class<? extends StructureComponent>>();
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}

}
