package pcl.lc.module.integration.computercraft;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockComputerCraftConnector extends Block implements ITileEntityProvider {

	public BlockComputerCraftConnector(int par1) {
		super(par1, Material.ground);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityComputerCraftConnector();
	}

}
