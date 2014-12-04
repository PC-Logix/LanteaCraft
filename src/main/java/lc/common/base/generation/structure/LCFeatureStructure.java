package lc.common.base.generation.structure;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import lc.core.ResourceAccess;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * LanteaCraft feature structure implementation.
 * 
 * @author AfterLifeLochie
 *
 */
public class LCFeatureStructure extends MapGenStructure {
	private int maxDistanceBetweenScatteredFeatures;
	private int minDistanceBetweenScatteredFeatures;

	/** Default constructor */
	public LCFeatureStructure() {
		maxDistanceBetweenScatteredFeatures = 32;
		minDistanceBetweenScatteredFeatures = 8;
	}

	/**
	 * Default constructor
	 * 
	 * @param params
	 *            The parameters
	 */
	@SuppressWarnings("rawtypes")
	public LCFeatureStructure(Map params) {
		this();
		Iterator i = params.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry param = (Map.Entry) i.next();
			if (((String) param.getKey()).equals("distance"))
				maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax((String) param.getValue(),
						maxDistanceBetweenScatteredFeatures, minDistanceBetweenScatteredFeatures + 1);
		}
	}

	@Override
	public String func_143025_a() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:LanteaCraft");
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

		if (var3 == var5 && var4 == var6)
			return true;

		return false;
	}

	@Override
	protected StructureStart getStructureStart(int par1, int par2) {
		return new LCFeatureStart(worldObj, rand, par1, par2);
	}
}