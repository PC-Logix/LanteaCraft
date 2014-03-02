package pcl.lc.module.integration;

import pcl.lc.LanteaCraft;
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
		// TODO Auto-generated method stub
		return null;
	}

}
