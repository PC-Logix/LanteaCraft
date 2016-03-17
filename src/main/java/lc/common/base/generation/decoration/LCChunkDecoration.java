package lc.common.base.generation.decoration;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureStart;
import lc.api.defs.IStructureDefinition;
import lc.common.base.generation.LCChunkData;
import lc.common.base.multiblock.StructureConfiguration;

public abstract class LCChunkDecoration extends StructureStart implements IStructureDefinition {

	public abstract void decorateChunk(Random random, World world, Chunk chunk, LCChunkData data);

}
