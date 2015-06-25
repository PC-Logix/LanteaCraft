package lc.common.impl.drivers;

import java.util.List;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaDriverManager implements IWailaDataProvider {

	private static WailaDriverManager manager;

	public WailaDriverManager() {
		WailaDriverManager.manager = this;
		FMLInterModComms.sendMessage("Waila", "register", "lc.common.impl.drivers.WailaDriverManager.callbackRegister");
	}

	public static void callbackRegister(IWailaRegistrar register) {
		LCLog.debug("Waila driver manager: setting up owned type registrations.");
		register.registerBodyProvider(manager, LCBlock.class);
		register.registerNBTProvider(manager, LCBlock.class);
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
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
			int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

}
