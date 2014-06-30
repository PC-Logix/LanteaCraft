package pcl.lc.module.galaxy.abydos;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class AbydosBiome extends BiomeGenBase {

	public AbydosBiome(int par1) {
		super(par1);
		topBlock = Blocks.sand;
		biomeName = "AbydosSurface";
		fillerBlock = Blocks.sand;
		theBiomeDecorator.generateLakes = false;
		theBiomeDecorator.treesPerChunk = -999;
		theBiomeDecorator.deadBushPerChunk = -999;
		theBiomeDecorator.reedsPerChunk = -999;
		theBiomeDecorator.cactiPerChunk = -999;
		spawnableCreatureList.clear();
	}

	@Override
	public void decorate(World par1World, Random par2Random, int par3, int par4) {
		super.decorate(par1World, par2Random, par3, par4);
		if (par2Random.nextInt(1000) == 0) {
			int k = par3 + par2Random.nextInt(16) + 8;
			int l = par4 + par2Random.nextInt(16) + 8;
			WorldGenDesertWells worldgendesertwells = new WorldGenDesertWells();
			worldgendesertwells.generate(par1World, par2Random, k, par1World.getHeightValue(k, l) + 1, l);
		}
	}

}
