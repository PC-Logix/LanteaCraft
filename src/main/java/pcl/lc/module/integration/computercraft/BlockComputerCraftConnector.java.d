package pcl.lc.module.integration.computercraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;

public class BlockComputerCraftConnector extends Block implements ITileEntityProvider {

	public BlockComputerCraftConnector() {
		super(Material.ground);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityComputerCraftConnector();
	}

	private int countAdaptableBlocks(World par1World, int par2, int par3, int par4) {
		/*
		 * Because of the way ComputerCraftConnector works, we can only place
		 * this block if there is no other LanteaCraft components around it
		 * (that is, we can only to provide one set of API wrappings).
		 */
		Vector3 origin = new Vector3(par2, par3, par4);
		int legals = 0;
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			Vector3 target = origin.add(direction);
			if (par1World.getBlock(target.floorX(), target.floorY(), target.floorZ()) != null
					&& ComputerCraftWrapperPool.canWrap(par1World.getTileEntity(target.floorX(), target.floorY(),
							target.floorZ())))
				legals++;
		}
		return legals;
	}

	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return countAdaptableBlocks(par1World, par2, par3, par4) == 1;
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
		if (countAdaptableBlocks(par1World, par2, par3, par4) != 1) {
			par1World.setBlockToAir(par2, par3, par4);
			dropBlockAsItem(par1World, par2, par3, par4, new ItemStack(this, 1, 0));
		}
	}

}
