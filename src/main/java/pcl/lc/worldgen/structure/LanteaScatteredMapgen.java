package pcl.lc.worldgen.structure;

import java.util.Random;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class LanteaScatteredMapgen extends MapGenStructure {

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
			i -= this.maxStructDensity - 1;
		if (j < 0)
			j -= this.maxStructDensity - 1;
		int imul = i, jmul = j;
		Random var7 = this.worldObj.setRandomSeed(i / this.maxStructDensity, j / this.maxStructDensity, 14357617);
		imul += var7.nextInt(this.maxStructDensity - this.minStructDensity);
		jmul += var7.nextInt(this.maxStructDensity - this.minStructDensity);
		return (iv == imul && jv == jmul);
	}

	@Override
	protected StructureStart getStructureStart(int i, int j) {
		return new LanteaScatteredFeatureStart(LanteaSurfaceStargate.class, worldObj, rand, i, j);
	}

}
