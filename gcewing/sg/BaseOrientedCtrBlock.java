//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Oriented Block with Tile Entity
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.util.*;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.item.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.client.registry.*;

public class BaseOrientedCtrBlock<TE extends TileEntity>
	extends BaseContainerBlock<TE>
{
	
	public BaseOrientedCtrBlock(int id, Material material) {
		this(id, material, null);
	}

	public BaseOrientedCtrBlock(int id, Material material, Class<TE> teClass) {
		super(id, material, teClass);
	}

	@Override
	String getRendererClassName() {
		return "BaseOrientedCtrBlkRenderer";
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
	
	public int facingInWorld(int metadata, TE te) {
		return 0;
	}
	
	public int rotationInWorld(int metadata, TE te) {
		return 0;
	}
	
	Trans3 localToInventoryTransformation(int metadata) {
		int facing = facingInInventory(metadata);
		int rotation = rotationInInventory(metadata);
		return new Trans3(0, 0, 0).side(facing).turn(rotation);
	}

	Trans3 localToGlobalTransformation(IBlockAccess world, int x, int y, int z) {
		int data = world.getBlockMetadata(x, y, z);
		TE te = getTileEntity(world, x, y, z);
		return localToGlobalTransformation(x, y, z, data, te);
	}

	Trans3 localToGlobalTransformation(int x, int y, int z, int data, TE te) {
		int facing = facingInWorld(data, te);
		int rotation = rotationInWorld(data, te);
		//System.out.printf("BaseBlock.localToGlobalTransformation: data %s facing %s rotation %s\n",
		//	data, facing, rotation);
		return new Trans3(x + 0.5, y + 0.5, z + 0.5).side(facing).turn(rotation);
	}
	
	public void setLocalBlockBounds(IBlockAccess world, int x, int y, int z, AxisAlignedBB box) {
		Trans3 t = localToGlobalTransformation(world, x, y, z);
		setBlockBoundsFromGlobalBox(t.t(box), x, y, z);
	}
	
	void setBlockBoundsFromGlobalBox(AxisAlignedBB box, double x, double y, double z) {
		minX = box.minX - x; minY = box.minY - y; minZ = box.minZ - z;
		maxX = box.maxX - x; maxY = box.maxY - y; maxZ = box.maxZ - z;
	}
		
//	public Icon getBlockTextureFromLocalSideAndMetadata(int side, int data) {
//		return blockIcon;
//	}

}
