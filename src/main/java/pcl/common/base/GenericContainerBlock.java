package pcl.common.base;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class GenericContainerBlock extends BlockContainer {

	static Random random = new Random();
	String[] iconNames = null;
	IIcon[] icons;

	public GenericContainerBlock(Material material) {
		super(material);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return getRenderType() == 0;
	}

	@Override
	public abstract void registerBlockIcons(IIconRegister reg);

	@Override
	public abstract TileEntity createNewTileEntity(World world, int metadata);

	public TileEntity getTileEntity(IBlockAccess blockaccess, int x, int y, int z) {
		return blockaccess.getTileEntity(x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int blockMeta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IInventory) {
			IInventory inventory = (IInventory) te;
			if (inventory != null)
				for (int i = 0; i < inventory.getSizeInventory(); ++i) {
					ItemStack stack = inventory.getStackInSlot(i);
					if (stack != null) {
						float objectDropX = random.nextFloat() * 0.8F + 0.1F;
						float objectDropY = random.nextFloat() * 0.8F + 0.1F;
						EntityItem itemEntity;
						for (float objectDropHeight = random.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world
								.spawnEntityInWorld(itemEntity)) {
							int stackSize = random.nextInt(21) + 10;
							if (stackSize > stack.stackSize)
								stackSize = stack.stackSize;
							stack.stackSize -= stackSize;
							itemEntity = new EntityItem(world, x + objectDropX, y + objectDropY, z + objectDropHeight,
									new ItemStack(stack.getItem(), stackSize, stack.getItemDamage()));
							float motionMul = 0.05F;
							itemEntity.motionX = (float) random.nextGaussian() * motionMul;
							itemEntity.motionY = (float) random.nextGaussian() * motionMul + 0.2F;
							itemEntity.motionZ = (float) random.nextGaussian() * motionMul;
							if (stack.hasTagCompound())
								itemEntity.getEntityItem().setTagCompound(
										(NBTTagCompound) stack.getTagCompound().copy());
						}
					}
				}
		}
		super.breakBlock(world, x, y, z, block, blockMeta);
	}
}
