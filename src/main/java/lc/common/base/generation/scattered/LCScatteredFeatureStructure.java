package lc.common.base.generation.scattered;

import java.util.Random;

import lc.generation.LanteaSurfaceStargate;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class LCScatteredFeatureStructure extends MapGenStructure {

	private int maxStructDensity = 32;
	private int minStructDensity = 8;

	@Override
	public String func_143025_a() {
		return "LanteaCraft";
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int i, int j) {
		int iv = i, jv = j;
		if (i < 0)
			i -= maxStructDensity - 1;
		if (j < 0)
			j -= maxStructDensity - 1;
		int imul = i, jmul = j;
		Random var7 = worldObj.setRandomSeed(i / maxStructDensity, j / maxStructDensity, 14357617);
		imul += var7.nextInt(maxStructDensity - minStructDensity);
		jmul += var7.nextInt(maxStructDensity - minStructDensity);
		return iv == imul && jv == jmul;
	}

	@Override
	protected StructureStart getStructureStart(int i, int j) {
		return new LCScatteredFeatureStart(LanteaSurfaceStargate.class, worldObj, rand, i, j);
	}

}
