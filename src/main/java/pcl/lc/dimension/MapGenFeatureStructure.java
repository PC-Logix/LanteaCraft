package pcl.lc.dimension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import pcl.lc.LanteaCraft;
import pcl.lc.dimension.abydos.AbydosPyramid;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenFeatureStructure extends MapGenStructure {
	private List scatteredFeatureSpawnList;
	private int maxDistanceBetweenScatteredFeatures;
	private int minDistanceBetweenScatteredFeatures;

	public MapGenFeatureStructure() {
		this.scatteredFeatureSpawnList = new ArrayList();
		this.maxDistanceBetweenScatteredFeatures = 32;
		this.minDistanceBetweenScatteredFeatures = 8;
	}

	public MapGenFeatureStructure(Map par1Map) {
		this();
		Iterator var2 = par1Map.entrySet().iterator();
		while (var2.hasNext()) {
			Map.Entry var3 = (Map.Entry) var2.next();
			if (((String) var3.getKey()).equals("distance")) {
				this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(
						(String) var3.getValue(), this.maxDistanceBetweenScatteredFeatures,
						this.minDistanceBetweenScatteredFeatures + 1);
			}
		}
	}

	public String func_143025_a() {
		return LanteaCraft.getAssetKey() + ":LanteaCraft";
	}

	protected boolean canSpawnStructureAtCoords(int par1, int par2) {
		int var3 = par1;
		int var4 = par2;

		if (par1 < 0) {
			par1 -= this.maxDistanceBetweenScatteredFeatures - 1;
		}

		if (par2 < 0) {
			par2 -= this.maxDistanceBetweenScatteredFeatures - 1;
		}

		int var5 = par1 / this.maxDistanceBetweenScatteredFeatures;
		int var6 = par2 / this.maxDistanceBetweenScatteredFeatures;
		Random var7 = this.worldObj.setRandomSeed(var5, var6, 14357617);
		var5 *= this.maxDistanceBetweenScatteredFeatures;
		var6 *= this.maxDistanceBetweenScatteredFeatures;
		var5 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
		var6 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

		if ((var3 == var5) && (var4 == var6)) {
			return true;
		}

		return false;
	}

	protected StructureStart getStructureStart(int par1, int par2) {
		return new MapGenFeatureStructureStart(this.worldObj, this.rand, par1, par2);
	}

	public List getScatteredFeatureSpawnList() {
		return this.scatteredFeatureSpawnList;
	}
}