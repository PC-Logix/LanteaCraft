package lc.dimensions.abydos;

import lc.LCRuntime;
import lc.biomes.BiomeAbydosDesert;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Abydos world provider implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class AbydosWorldProvider extends WorldProvider {

	private float[] colorsSunriseSunset = new float[4];
	private BiomeAbydosDesert abydosBiome;

	/** Default constructor. */
	public AbydosWorldProvider() {
		abydosBiome = (BiomeAbydosDesert) LCRuntime.runtime.biomes().abydosDesert;
		worldChunkMgr = new AbydosChunkManager(abydosBiome, 0);
		hasNoSky = false;
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new AbydosChunkProvider(worldObj, abydosBiome);
	}

	@Override
	public int getAverageGroundLevel() {
		return 0;
	}

	@Override
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}

	@Override
	public String getDimensionName() {
		return "LanteaCraft:Abydos";
	}

	@Override
	public boolean isSkyColored() {
		return true;
	}

	@Override
	public boolean canRespawnHere() {
		return true;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@Override
	public float getCloudHeight() {
		return 128.0F;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return false;
	}

	@Override
	protected void generateLightBrightnessTable() {
		float f = 0.0F;
		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - i / 15.0F;
			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	public String getWelcomeMessage() {
		return "Entering Abydos";
	}

	@Override
	public float[] calcSunriseSunsetColors(float par1, float par2) {
		float f2 = 0.4F;
		float f3 = MathHelper.cos(par1 * 3.141593F * 2.0F) - 0.0F;
		float f4 = -0.0F;
		if (f3 >= f4 - f2 && f3 <= f4 + f2) {
			float f5 = (f3 - f4) / f2 * 0.5F + 0.5F;
			float f6 = 1.0F - (1.0F - MathHelper.sin(f5 * 3.141593F)) * 0.99F;
			f6 *= f6;
			colorsSunriseSunset[0] = f5 * 0.3F + 0.7F;
			colorsSunriseSunset[1] = f5 * f5 * 0.7F + 0.2F;
			colorsSunriseSunset[2] = f5 * f5 * 0.0F + 0.2F;
			colorsSunriseSunset[3] = f6;
			return colorsSunriseSunset;
		}
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		int j = (int) (worldTime % 24000L);
		float f1 = (j + partialTicks) / 24000.0F - 0.25F;
		if (f1 < 0.0F)
			f1 += 1.0F;
		if (f1 > 1.0F)
			f1 -= 1.0F;
		float f2 = f1;
		f1 = 1.0F - (float) ((Math.cos(f1 * 3.141592653589793D) + 1.0D) / 2.0D);
		f1 = f2 + (f1 - f2) / 3.0F;
		return f1;
	}

	@Override
	public Vec3 getFogColor(float celestialAngle, float partialTicks) {
		int i = 10518688;
		float f2 = MathHelper.cos(celestialAngle * 3.141593F * 2.0F) * 2.0F + 0.5F;
		if (f2 < 0.0F)
			f2 = 0.0F;
		if (f2 > 1.0F)
			f2 = 1.0F;
		float f3 = (i >> 16 & 0xFF) / 255.0F;
		float f4 = (i >> 8 & 0xFF) / 255.0F;
		float f5 = (i & 0xFF) / 255.0F;
		f3 *= f2 * 0.0F + 0.15F;
		f4 *= f2 * 0.0F + 0.15F;
		f5 *= f2 * 0.0F + 0.15F;

		return Vec3.createVectorHelper(f3, f4, f5);
	}

	@Override
	public String getInternalNameSuffix() {
		// TODO Auto-generated method stub
		return null;
	}

}
