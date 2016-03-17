package lc.common.base.generation.decoration;

import java.util.Random;

import lc.BuildInfo;
import lc.LCRuntime;
import lc.api.defs.IStructureDefinition;
import lc.common.base.generation.LCChunkData;
import lc.common.impl.registry.StructureRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class LCChunkDecorator {

	private final StructureRegistry registry;

	public LCChunkDecorator() {
		registry = (StructureRegistry) LCRuntime.runtime.registries().structures();
	}

	public void paint(Random random, World world, Chunk achunk, LCChunkData data) {
		IStructureDefinition[] defs = registry.allDefs(LCChunkDecoration.class);
		NBTTagCompound compound = data.compound.getCompoundTag("Decorators");
		for (IStructureDefinition def : defs) {
			if (compound.getInteger(def.getName()) == 0) {
				compound.setInteger(def.getName(), (BuildInfo.$.build() == 0) ? -1 : BuildInfo.$.build());
				if (def.canGenerateAt(world, random, achunk.xPosition, achunk.zPosition))
					((LCChunkDecoration) def).decorateChunk(random, world, achunk, data);
			}
		}
		data.compound.setTag("Decorators", compound);
		data.dirty();
	}

}
