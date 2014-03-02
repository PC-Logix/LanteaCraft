package pcl.lc.module.integration;

import pcl.lc.LanteaCraft;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import net.minecraft.world.World;
import li.cil.oc.api.driver.Block;
import li.cil.oc.api.network.ManagedEnvironment;

@Agent(modname = "OpenComputers")
public class OpenComputersAgent implements IIntegrationAgent, Block {
	public OpenComputersAgent() {
		li.cil.oc.api.Driver.add(this);
	}

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

	@Override
	public String modName() {
		return "OpenComputers";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
