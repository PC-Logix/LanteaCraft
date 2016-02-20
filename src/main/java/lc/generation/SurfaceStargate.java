package lc.generation;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lc.api.rendering.IBlockSkinnable;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCTile;
import lc.common.base.generation.scattered.LCScatteredFeature;
import lc.common.base.generation.scattered.LCScatteredFeatureStart;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.util.game.BlockFilter;
import lc.common.util.math.Matrix3;
import lc.common.util.math.Orientations;
import lc.common.util.math.Vector3;
import lc.common.util.math.VectorAABB;
import lc.tiles.TileStargateBase;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

/**
 * Surface Stargate feature implementation.
 * 
 * @author AfterLifeLochie
 *
 */
public class SurfaceStargate extends LCScatteredFeatureStart {

	/** Default constructor */
	public SurfaceStargate() {
	}

	public SurfaceStargate(World world, Random rand, int x, int y) {
		super(world, rand, x, y);

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addComponents(World world, Random rng, int cx, int cz) {
		components.add(new SurfaceStargateFeature(rng, cx, cz));
	}

	/**
	 * Surface Stargate core feature
	 * 
	 * @author AfterLifeLochie
	 *
	 */
	public static class SurfaceStargateFeature extends LCScatteredFeature {

		private Orientations rotation;

		/** Default constructor */
		public SurfaceStargateFeature() {
		}

		/**
		 * @param rng
		 *            The random number generator
		 * @param x
		 *            The x-coordinate
		 * @param z
		 *            The z-coordinate
		 */
		public SurfaceStargateFeature(Random rng, int x, int z) {
			super(rng, x + rng.nextInt(7), 0, z + rng.nextInt(7), 9, 64, 9);
			rotation = Orientations.randomCardinal(rng);
		}

		/**
		 * func_143012_a: saveToNBT(NBTTagCompound compound);
		 */
		@Override
		protected void func_143012_a(NBTTagCompound tag) {
			super.func_143012_a(tag);
			tag.setInteger("Rotation", rotation.ordinal());
		}

		/**
		 * func_143011_b: loadFromNBT(NBTTagCompound compound);
		 */
		@Override
		protected void func_143011_b(NBTTagCompound tag) {
			super.func_143011_b(tag);
			if (tag.hasKey("Rotation"))
				rotation = Orientations.values()[tag.getInteger("Rotation")];
		}

		@Override
		public boolean addComponentParts(World w, Random r, StructureBoundingBox bb) {
			recalcHeightOffsets(w, bb, 1);
			Vector3 v0 = new Vector3(0, -4, 0);
			Vector3 v1 = new Vector3(scatteredFeatureSizeX - 1, 0, scatteredFeatureSizeZ - 1);
			Vector3 vMin = Vector3.zero;
			Vector3 vMax = new Vector3(scatteredFeatureSizeX - 1, scatteredFeatureSizeY - 1, scatteredFeatureSizeZ - 1);
			fill(w, bb, vMin, vMax, net.minecraft.init.Blocks.air);
			fill(w, bb, v0, v1, net.minecraft.init.Blocks.sandstone);
			StructureConfiguration config = TileStargateBase.milkyStructure;

			Vector3 center = new Vector3(Math.floor(scatteredFeatureSizeX / 2), 0,
					Math.floor(scatteredFeatureSizeZ / 2));
			paintStructure(config, w, bb, center.fx(), center.fy(), center.fz(), rotation,
					net.minecraft.init.Blocks.sandstone, 0);
			return true;
		}

		private void paintStructure(StructureConfiguration config, World w, StructureBoundingBox bb, int x, int y,
				int z, Orientations orientation, Block baseBlock, int baseMeta) {
			BlockFilter[] mappings = config.getBlockMappings();
			Matrix3 rotation = orientation.rotation();
			Vector3 origin = new Vector3(x, y, z).sub(rotation.mul(config.getStructureCenter()));
			VectorAABB box = VectorAABB.boxOf(origin, config.getStructureDimensions());
			List<Vector3> elems = box.contents();
			Iterator<Vector3> each = elems.iterator();
			while (each.hasNext()) {
				Vector3 mapping = each.next();
				Vector3 tile = origin.add(rotation.mul(mapping));
				try {
					int cell = config.getStructureLayout()[mapping.fx()][mapping.fy()][mapping.fz()];
					BlockFilter filter = mappings[cell];
					Block what = filter.getBlock();
					int metadata = Math.max(0, filter.getMetadata());
					fill(w, bb, tile, tile, what, metadata);
					if (what instanceof LCBlock) {
						int tx = getXWithOffset(tile.fx(), tile.fz());
						int tz = getZWithOffset(tile.fx(), tile.fz());
						LCTile te = (LCTile) w.getTileEntity(tx, getYWithOffset(tile.fy()), tz);
						if (te != null && ((LCBlock) what).canRotate())
							te.setRotation(orientation.forge());
						if (tile.fy() == y && te instanceof IBlockSkinnable) {
							IBlockSkinnable skinnable = (IBlockSkinnable) te;
							skinnable.setSkinBlock(baseBlock, baseMeta);
						}
					}
				} catch (IndexOutOfBoundsException bounds) {
					LCLog.fatal("Access out of bounds: " + bounds.getMessage() + ": "
							+ String.format("%s %s %s", mapping.fx(), mapping.fy(), mapping.fz()));
				}
			}
		}
	}
}