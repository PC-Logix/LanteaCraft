package pcl.lc.module.integration;

import pcl.lc.LanteaCraft;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import net.minecraft.world.World;
import li.cil.oc.api.driver.Block;
import li.cil.oc.api.network.ManagedEnvironment;

public class OpenComputersAgent implements Block {

	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		return (id == LanteaCraft.Blocks.stargateBaseBlock.blockID)
				|| (id == LanteaCraft.Blocks.naquadahGenerator.blockID);
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == LanteaCraft.Blocks.stargateBaseBlock.blockID) {
			IStargateAccess base = (IStargateAccess) world.getBlockTileEntity(x, y, z);
			return new OpenComputersWrapperPool.StargateAccessWrapper(base);
		} else if (id == LanteaCraft.Blocks.naquadahGenerator.blockID) {
			INaquadahGeneratorAccess generator = (INaquadahGeneratorAccess) world.getBlockTileEntity(x, y, z);
			return new OpenComputersWrapperPool.NaquadahGeneratorAccessWrapper(generator);
		} else
			throw new RuntimeException("Driver.Block handler specified invalid typeof!");
	}

}
