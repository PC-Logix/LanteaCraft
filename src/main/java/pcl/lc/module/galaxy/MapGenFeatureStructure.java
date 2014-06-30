package pcl.lc.module.galaxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import pcl.lc.LanteaCraft;

public class MapGenFeatureStructure extends MapGenStructure {
	private List scatteredFeatureSpawnList;
	private int maxDistanceBetweenScatteredFeatures;
	private int minDistanceBetweenScatteredFeatures;

	public MapGenFeatureStructure() {
		scatteredFeatureSpawnList = new ArrayList();
		maxDistanceBetweenScatteredFeatures = 32;
		minDistanceBetweenScatteredFeatures = 8;
	}

	public MapGenFeatureStructure(Map par1Map) {
		this();
		Iterator var2 = par1Map.entrySet().iterator();
		while (var2.hasNext()) {
			Map.Entry var3 = (Map.Entry) var2.next();
			if (((String) var3.getKey()).equals("distance"))
				maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String) var3.getValue(),
						maxDistanceBetweenScatteredFeatures, minDistanceBetweenScatteredFeatures + 1);
		}
	}

	@Override
	public String func_143025_a() {
		return LanteaCraft.getAssetKey() + ":LanteaCraft";
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int par1, int par2) {
		int var3 = par1;
		int var4 = par2;

		if (par1 < 0)
			par1 -= maxDistanceBetweenScatteredFeatures - 1;

		if (par2 < 0)
			par2 -= maxDistanceBetweenScatteredFeatures - 1;

		int var5 = par1 / maxDistanceBetweenScatteredFeatures;
		int var6 = par2 / maxDistanceBetweenScatteredFeatures;
		Random var7 = worldObj.setRandomSeed(var5, var6, 14357617);
		var5 *= maxDistanceBetweenScatteredFeatures;
		var6 *= maxDistanceBetweenScatteredFeatures;
		var5 += var7.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
		var6 += var7.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);

		if ((var3 == var5) && (var4 == var6))
			return true;

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int par1, int par2) {
		return new MapGenFeatureStructureStart(worldObj, rand, par1, par2);
	}

	public List getScatteredFeatureSpawnList() {
		return scatteredFeatureSpawnList;
	}
}