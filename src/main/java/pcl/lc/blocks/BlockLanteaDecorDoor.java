package pcl.lc.blocks;

import java.util.Random;

import pcl.lc.module.ModuleDecor.EnumDecorMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLanteaDecorDoor extends Block {
	private EnumDecorMaterials doorMaterial;

	@SideOnly(Side.CLIENT)
	private Icon[] texTop;
	@SideOnly(Side.CLIENT)
	private Icon[] texBottom;

	protected BlockLanteaDecorDoor(int id, EnumDecorMaterials material) {
		super(id, Material.ground);
		this.doorMaterial = material;
		float f = 0.5F, f1 = 1.0F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}

	@SideOnly(Side.CLIENT)
	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	public Icon getIcon(int side, int meta) {
		return this.texBottom[0];
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	 */
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		if (par5 != 1 && par5 != 0) {
			int i1 = this.getFullMetadata(par1IBlockAccess, par2, par3, par4);
			int j1 = i1 & 3;
			boolean flag = (i1 & 4) != 0;
			boolean flag1 = false;
			boolean flag2 = (i1 & 8) != 0;

			if (flag) {
				if (j1 == 0 && par5 == 2) {
					flag1 = !flag1;
				} else if (j1 == 1 && par5 == 5) {
					flag1 = !flag1;
				} else if (j1 == 2 && par5 == 3) {
					flag1 = !flag1;
				} else if (j1 == 3 && par5 == 4) {
					flag1 = !flag1;
				}
			} else {
				if (j1 == 0 && par5 == 5) {
					flag1 = !flag1;
				} else if (j1 == 1 && par5 == 3) {
					flag1 = !flag1;
				} else if (j1 == 2 && par5 == 4) {
					flag1 = !flag1;
				} else if (j1 == 3 && par5 == 2) {
					flag1 = !flag1;
				}

				if ((i1 & 16) != 0) {
					flag1 = !flag1;
				}
			}

			return flag2 ? this.texTop[flag1 ? 1 : 0] : this.texBottom[flag1 ? 1 : 0];
		} else {
			return this.texBottom[0];
		}
	}

	@SideOnly(Side.CLIENT)
	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This
	 * is the only chance you get to register icons.
	 */
	public void registerIcons(IconRegister par1IconRegister) {
		this.texTop = new Icon[2];
		this.texBottom = new Icon[2];
		this.texTop[0] = par1IconRegister.registerIcon(doorMaterial.label() + "_door_upper");
		this.texBottom[0] = par1IconRegister.registerIcon(doorMaterial.label() + "_door_lower");
		this.texTop[1] = new IconFlipped(this.texTop[0], true, false);
		this.texBottom[1] = new IconFlipped(this.texBottom[0], true, false);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		return (getFullMetadata(par1IBlockAccess, par2, par3, par4) & 4) != 0;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 7;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		setDoorRotation(getFullMetadata(par1IBlockAccess, par2, par3, par4));
	}

	public int getDoorOrientation(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		return this.getFullMetadata(par1IBlockAccess, par2, par3, par4) & 3;
	}

	public boolean isDoorOpen(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		return (getFullMetadata(par1IBlockAccess, par2, par3, par4) & 4) != 0;
	}

	private void setDoorRotation(int par1) {
		float f = 0.1875F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int j = par1 & 3;
		boolean flag = (par1 & 4) != 0;
		boolean flag1 = (par1 & 16) != 0;

		if (j == 0) {
			if (flag) {
				if (!flag1) {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				}
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
			}
		} else if (j == 1) {
			if (flag) {
				if (!flag1) {
					this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				}
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
			}
		} else if (j == 2) {
			if (flag) {
				if (!flag1) {
					this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
				}
			} else {
				this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		} else if (j == 3) {
			if (flag) {
				if (!flag1) {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				}
			} else {
				this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
	}

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer,
			int par6, float par7, float par8, float par9) {
		if (this.blockMaterial == Material.iron) {
			return false; // Allow items to interact with the door
		} else {
			int i1 = this.getFullMetadata(par1World, par2, par3, par4);
			int j1 = i1 & 7;
			j1 ^= 4;

			if ((i1 & 8) == 0) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, j1, 2);
				par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
			} else {
				par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, j1, 2);
				par1World.markBlockRangeForRenderUpdate(par2, par3 - 1, par4, par2, par3, par4);
			}

			par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
			return true;
		}
	}

	public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5) {
		int l = this.getFullMetadata(par1World, par2, par3, par4);
		boolean flag1 = (l & 4) != 0;

		if (flag1 != par5) {
			int i1 = l & 7;
			i1 ^= 4;

			if ((l & 8) == 0) {
				par1World.setBlockMetadataWithNotify(par2, par3, par4, i1, 2);
				par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
			} else {
				par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, i1, 2);
				par1World.markBlockRangeForRenderUpdate(par2, par3 - 1, par4, par2, par3, par4);
			}

			par1World.playAuxSFXAtEntity((EntityPlayer) null, 1003, par2, par3, par4, 0);
		}
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		int i1 = par1World.getBlockMetadata(par2, par3, par4);

		if ((i1 & 8) == 0) {
			boolean flag = false;

			if (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID) {
				par1World.setBlockToAir(par2, par3, par4);
				flag = true;
			}

			if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4)) {
				par1World.setBlockToAir(par2, par3, par4);
				flag = true;

				if (par1World.getBlockId(par2, par3 + 1, par4) == this.blockID) {
					par1World.setBlockToAir(par2, par3 + 1, par4);
				}
			}

			if (flag) {
				if (!par1World.isRemote) {
					this.dropBlockAsItem(par1World, par2, par3, par4, i1, 0);
				}
			} else {
				boolean flag1 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)
						|| par1World.isBlockIndirectlyGettingPowered(par2, par3 + 1, par4);

				if ((flag1 || par5 > 0 && Block.blocksList[par5].canProvidePower()) && par5 != this.blockID) {
					this.onPoweredBlockChange(par1World, par2, par3, par4, flag1);
				}
			}
		} else {
			if (par1World.getBlockId(par2, par3 - 1, par4) != this.blockID) {
				par1World.setBlockToAir(par2, par3, par4);
			}

			if (par5 > 0 && par5 != this.blockID) {
				this.onNeighborBlockChange(par1World, par2, par3 - 1, par4, par5);
			}
		}
	}

	public int idDropped(int par1, Random par2Random, int par3) {
		return (par1 & 8) != 0 ? 0
				: (this.blockMaterial == Material.iron ? Item.doorIron.itemID : Item.doorWood.itemID);
	}

	/**
	 * Ray traces through the blocks collision from start vector to end vector
	 * returning a ray trace hit. Args: world, x, y, z, startVec, endVec
	 */
	public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3,
			Vec3 par6Vec3) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
	}

	/**
	 * Checks to see if its valid to put this block at the specified
	 * coordinates. Args: world, x, y, z
	 */
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return par3 >= 255 ? false : par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4)
				&& super.canPlaceBlockAt(par1World, par2, par3, par4)
				&& super.canPlaceBlockAt(par1World, par2, par3 + 1, par4);
	}

	/**
	 * Returns the mobility information of the block, 0 = free, 1 = can't push
	 * but can move over, 2 = total immobility and stop pistons
	 */
	public int getMobilityFlag() {
		return 1;
	}

	/**
	 * Returns the full metadata value created by combining the metadata of both
	 * blocks the door takes up.
	 */
	public int getFullMetadata(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
		boolean flag = (l & 8) != 0;
		int i1;
		int j1;

		if (flag) {
			i1 = par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4);
			j1 = l;
		} else {
			i1 = l;
			j1 = par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4);
		}

		boolean flag1 = (j1 & 1) != 0;
		return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
	}

	@SideOnly(Side.CLIENT)
	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return this.blockMaterial == Material.iron ? Item.doorIron.itemID : Item.doorWood.itemID;
	}

	/**
	 * Called when the block is attempted to be harvested
	 */
	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
		if (par6EntityPlayer.capabilities.isCreativeMode && (par5 & 8) != 0
				&& par1World.getBlockId(par2, par3 - 1, par4) == this.blockID) {
			par1World.setBlockToAir(par2, par3 - 1, par4);
		}
	}

}
