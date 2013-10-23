//------------------------------------------------------------------------------------------------
//
//   Mod Base - Generic 4-way rotatable container block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class Base4WayCtrBlock<TE extends TileEntity> extends BaseOrientedCtrBlock<TE> {

	int rotationShift = 0;
	int rotationMask = 0x3;

	public Base4WayCtrBlock(int id, Material material) {
		this(id, material, null);
	}
	
	public Base4WayCtrBlock(int id, Material material, Class<TE> teClass) {
		super(id, material, teClass);
	}
	
	public void setRotation(World world, int x, int y, int z, int rotation, int flags) {
		int data = world.getBlockMetadata(x, y, z);
		data = insertRotation(data, rotation);
		world.setBlockMetadataWithNotify(x, y, z, data, flags);
	}
	
	@Override
	public int rotationInWorld(int data, TE te) {
		return extractRotation(data);
	}

	public int extractRotation(int data) {
		return (data & rotationMask) >> rotationShift;
	}
	
	public int insertRotation(int data, int rotation) {
		return (data & ~rotationMask) | (rotation << rotationShift);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		int rotation = Math.round((180 - player.rotationYaw) / 90) & 3;
		setRotation(world, x, y, z, rotation, 0x3);
	}

//	@Override
//	public Icon getBlockTextureFromSideAndMetadata(int side, int data) {
//		int rotation = extractRotation(data);
//		int localSide = Directions.globalToLocalSide(side, rotation);
//		return getBlockTextureFromLocalSideAndMetadata(localSide, data);
//	}
	
//	int getBlockTextureFromLocalSideAndMetadata(int side, int data) {
//		return blockIndexInTexture + side;
//	}

}
