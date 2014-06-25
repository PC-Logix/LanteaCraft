package pcl.common.base;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import pcl.common.util.Trans3;
import pcl.lc.LanteaCraft;

public abstract class OrientedBlock extends GenericContainerBlock {

	public OrientedBlock(Material material) {
		super(material);
	}

	@Override
	public int getRenderType() {
		if (LanteaCraft.Render.blockOrientedRenderer != null)
			return LanteaCraft.Render.blockOrientedRenderer.renderID;
		return -9001;
	}

	public int getFacing(IBlockAccess world, int x, int y, int z) {
		return facingInWorld(world.getBlockMetadata(x, y, z), getTileEntity(world, x, y, z));
	}

	public int getRotation(IBlockAccess world, int x, int y, int z) {
		return rotationInWorld(world.getBlockMetadata(x, y, z), getTileEntity(world, x, y, z));
	}

	public int facingInInventory(int metadata) {
		return 0;
	}

	public int rotationInInventory(int metadata) {
		return 0;
	}

	public int facingInWorld(int metadata, TileEntity te) {
		return 0;
	}

	public int rotationInWorld(int metadata, TileEntity te) {
		return 0;
	}

	public Trans3 localToInventoryTransformation(int metadata) {
		int facing = facingInInventory(metadata);
		int rotation = rotationInInventory(metadata);
		return new Trans3(0, 0, 0).side(facing).turn(rotation);
	}

	public Trans3 localToGlobalTransformation(IBlockAccess world, int x, int y, int z) {
		int data = world.getBlockMetadata(x, y, z);
		TileEntity te = getTileEntity(world, x, y, z);
		return localToGlobalTransformation(x, y, z, data, te);
	}

	public Trans3 localToGlobalTransformation(int x, int y, int z, int data, TileEntity te) {
		int facing = facingInWorld(data, te);
		int rotation = rotationInWorld(data, te);
		return new Trans3(x + 0.5, y + 0.5, z + 0.5).side(facing).turn(rotation);
	}

	public void setLocalBlockBounds(IBlockAccess world, int x, int y, int z, AxisAlignedBB box) {
		Trans3 t = localToGlobalTransformation(world, x, y, z);
		setBlockBoundsFromGlobalBox(t.t(box), x, y, z);
	}

	void setBlockBoundsFromGlobalBox(AxisAlignedBB box, double x, double y, double z) {
		minX = box.minX - x;
		minY = box.minY - y;
		minZ = box.minZ - z;
		maxX = box.maxX - x;
		maxY = box.maxY - y;
		maxZ = box.maxZ - z;
	}

}
