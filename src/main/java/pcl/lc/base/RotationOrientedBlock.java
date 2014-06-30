package pcl.lc.base;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class RotationOrientedBlock extends OrientedBlock {

	int rotationShift = 0;
	int rotationMask = 0x3;

	public RotationOrientedBlock(Material material) {
		super(material);
	}

	public void setRotation(World world, int x, int y, int z, int rotation, int flags) {
		int data = world.getBlockMetadata(x, y, z);
		data = insertRotation(data, rotation);
		world.setBlockMetadataWithNotify(x, y, z, data, flags);
	}

	@Override
	public int rotationInWorld(int data, TileEntity te) {
		return extractRotation(data);
	}

	public int extractRotation(int data) {
		return (data & rotationMask) >> rotationShift;
	}

	public int insertRotation(int data, int rotation) {
		return data & ~rotationMask | rotation << rotationShift;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int rotation = Math.round((180 - player.rotationYaw) / 90) & 3;
		setRotation(world, x, y, z, rotation, 0x3);
	}

}
