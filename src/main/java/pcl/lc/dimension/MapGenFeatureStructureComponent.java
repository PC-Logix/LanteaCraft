package pcl.lc.dimension;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class MapGenFeatureStructureComponent extends StructureComponent {
	/** The size of the bounding box for this feature in the X axis */
	protected int scatteredFeatureSizeX;
	/** The size of the bounding box for this feature in the Y axis */
	protected int scatteredFeatureSizeY;
	/** The size of the bounding box for this feature in the Z axis */
	protected int scatteredFeatureSizeZ;
	protected int field_74936_d = -1;

	public MapGenFeatureStructureComponent() {
	}

	protected MapGenFeatureStructureComponent(Random par1Random, int par2, int par3, int par4, int sx, int sy, int sz) {
		super(0);
		scatteredFeatureSizeX = sx;
		scatteredFeatureSizeY = sy;
		scatteredFeatureSizeZ = sz;
		coordBaseMode = par1Random.nextInt(4);

		switch (coordBaseMode) {
		case 0:
		case 2:
			boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + sx - 1, par3 + sy - 1, par4 + sz - 1);
			break;
		default:
			boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + sz - 1, par3 + sy - 1, par4 + sx - 1);
		}
	}

	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setInteger("Width", scatteredFeatureSizeX);
		par1NBTTagCompound.setInteger("Height", scatteredFeatureSizeY);
		par1NBTTagCompound.setInteger("Depth", scatteredFeatureSizeZ);
		par1NBTTagCompound.setInteger("HPos", field_74936_d);
	}

	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		scatteredFeatureSizeX = par1NBTTagCompound.getInteger("Width");
		scatteredFeatureSizeY = par1NBTTagCompound.getInteger("Height");
		scatteredFeatureSizeZ = par1NBTTagCompound.getInteger("Depth");
		field_74936_d = par1NBTTagCompound.getInteger("HPos");
	}

	protected boolean func_74935_a(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3) {
		if (field_74936_d >= 0)
			return true;
		else {
			int j = 0;
			int k = 0;

			for (int l = boundingBox.minZ; l <= boundingBox.maxZ; ++l)
				for (int i1 = boundingBox.minX; i1 <= boundingBox.maxX; ++i1)
					if (par2StructureBoundingBox.isVecInside(i1, 64, l)) {
						j += Math.max(par1World.getTopSolidOrLiquidBlock(i1, l),
								par1World.provider.getAverageGroundLevel());
						++k;
					}

			if (k == 0)
				return false;
			else {
				field_74936_d = j / k;
				boundingBox.offset(0, field_74936_d - boundingBox.minY + par3, 0);
				return true;
			}
		}
	}
}