package pcl.lc.module.integration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.access.INaquadahGeneratorAccess;
import pcl.lc.api.access.IStargateAccess;
import pcl.lc.api.access.IStargateControllerAccess;
import pcl.lc.util.RegistrationHelper;
import pcl.lc.util.RegistrationHelper.BlockItemMapping;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class WailaHook implements IWailaDataProvider {

	public WailaHook(Class<? extends Block> blockClass) {
		// TODO: Something useful?
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		if (accessor.getTileEntity() instanceof IStargateAccess) {
			IStargateAccess access = (IStargateAccess) accessor.getTileEntity();
		} else if (accessor.getTileEntity() instanceof IStargateControllerAccess) {
			IStargateControllerAccess access = (IStargateControllerAccess) accessor.getTileEntity();
		} else if (accessor.getTileEntity() instanceof INaquadahGeneratorAccess) {
			INaquadahGeneratorAccess access = (INaquadahGeneratorAccess) accessor.getTileEntity();
			currenttip.add(access.isEnabled() ? "Generator Enabled" : "Generator Disabled");
			currenttip.add(String.format("Power: %.2f%%",
					100.0f * access.getStoredEnergy() / access.getMaximumStoredEnergy()));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

}
