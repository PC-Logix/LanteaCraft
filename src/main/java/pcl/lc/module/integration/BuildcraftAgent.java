package pcl.lc.module.integration;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.api.internal.ITickAgent;

@Agent(modname = "BuildCraft|Core")
public class BuildcraftAgent implements IIntegrationAgent {

	public static class BuildcraftPowerInterop implements ITickAgent, IPowerEmitter, IPowerReceptor {
		
		private PowerHandler handler;
		public BuildcraftPowerInterop() {
			handler = new PowerHandler(this, Type.MACHINE);
		}
		
		@Override
		public void advance() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public PowerReceiver getPowerReceiver(ForgeDirection side) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void doWork(PowerHandler workProvider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public World getWorld() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean canEmitPowerFrom(ForgeDirection side) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
	
	@Override
	public String modName() {
		return "BuildCraft|Core";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
