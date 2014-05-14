package pcl.lc.module.integration.computercraft;

import pcl.common.util.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockComputerCraftConnector extends Block implements ITileEntityProvider {

	public BlockComputerCraftConnector(int par1) {
		super(par1, Material.ground);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityComputerCraftConnector();
	}

	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		/*
		 * Because of the way ComputerCraftConnector works, we can only place
		 * this block if there is no other LanteaCraft components around it
		 * (that is, we can only to provide one set of API wrappings).
		 */
		Vector3 origin = new Vector3(par2, par3, par4);
		int legals = 0;
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			Vector3 target = origin.add(direction);
			if (par1World.getBlockId(target.floorX(), target.floorY(), target.floorZ()) > 0
					&& ComputerCraftWrapperPool.canWrap(par1World.getBlockTileEntity(target.floorX(), target.floorY(),
							target.floorZ())))
				legals++;
		}
		return (legals == 1);
	}

}
