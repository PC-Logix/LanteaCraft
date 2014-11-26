package lc.common.util.game;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldProxy implements IBlockAccess {

	IBlockAccess myWorld;
	int allMeta;
	int allBrightness = -1;

	public WorldProxy(IBlockAccess world, int meta) {
		myWorld = world;
		allMeta = meta;
	}

	public WorldProxy(IBlockAccess world, int meta, int brightness) {
		myWorld = world;
		allMeta = meta;
		allBrightness = brightness;
	}

	@Override
	public Block getBlock(int var1, int var2, int var3) {
		return myWorld.getBlock(var1, var2, var3);
	}

	@Override
	public TileEntity getTileEntity(int var1, int var2, int var3) {
		return myWorld.getTileEntity(var1, var2, var3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
		return allBrightness != -1 ? allBrightness : myWorld.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
	}

	@Override
	public int getBlockMetadata(int var1, int var2, int var3) {
		return allMeta;
	}

	@Override
	public boolean isAirBlock(int var1, int var2, int var3) {
		return myWorld.isAirBlock(var1, var2, var3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BiomeGenBase getBiomeGenForCoords(int var1, int var2) {
		return myWorld.getBiomeGenForCoords(var1, var2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getHeight() {
		return myWorld.getHeight();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean extendedLevelsInChunkCache() {
		return myWorld.extendedLevelsInChunkCache();
	}

	@Override
	public int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4) {
		return myWorld.isBlockProvidingPowerTo(var1, var2, var3, var4);
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
		return myWorld.isSideSolid(x, y, z, side, _default);
	}

}
