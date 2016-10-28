package lc.common.impl.drivers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers.DriverProvider;
import lc.api.jit.DeviceDrivers.DriverRTCallback;
import lc.api.jit.ASMTag;
import lc.api.jit.Tag;
import lc.common.LCLog;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@DriverProvider(type = IntegrationType.COMPUTERS)
public class ComputerCraftPeripheralDriver implements IPeripheral {

	private String[] computercraft_methodcache;
	private ArrayList<IComputerAccess> computercraft_icalist;
	private HashMap<IComputerAccess, ComputerCraftScuffMount> computercraft_mounts;

	public ComputerCraftPeripheralDriver() {
		// TODO Auto-generated constructor stub
	}

	private void computercraft_assertReady() {
		if (computercraft_icalist == null) {
			computercraft_icalist = new ArrayList<IComputerAccess>();
			computercraft_mounts = new HashMap<IComputerAccess, ComputerCraftScuffMount>();
		}
	}

	@DriverRTCallback(event = "isSideSolid")
	public void computerCraft_checkIsSideSolid(Object[] args) {
		String[] klasses = (String[]) args[1];
		for (String klass : klasses)
			if (klass.startsWith("dan200"))
				args[0] = true;
	}

	@DriverRTCallback(event = "computerEvent")
	public void computerCraft_handleEvent(String event, Object... args) {
		computercraft_assertReady();
		synchronized (computercraft_icalist) {
			for (IComputerAccess azz : computercraft_icalist)
				azz.queueEvent(event, args);
		}
	}

	@Override
	public String getType() {
		return getClass().getSimpleName().replace("Tile", "").replace("tile", "");
	}

	@Override
	public String[] getMethodNames() {
		if (computercraft_methodcache == null) {
			ArrayList<String> alist = new ArrayList<String>();
			Class<?> zz = getClass();
			Method[] methods = zz.getMethods();
			for (Method m : methods) {
				LCLog.debug("ComputerCraft driver: assessing method %s (class %s).", m.getName(), zz.getSimpleName());
				Tag foundTag = ASMTag.findTag(getClass(), m, "ComputerCallable");
				if (foundTag == null)
					continue;
				LCLog.debug("ComputerCraft driver: adding method %s", m.getName());
				alist.add(m.getName());
			}
			computercraft_methodcache = alist.toArray(new String[0]);
		}
		return (computercraft_methodcache == null) ? new String[0] : computercraft_methodcache;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		String[] methods = ComputerMethodExecutor.executor().getMethods(getClass());
		if (method < 0 || method >= methods.length) {
			LCLog.warn("ComputerCraft driver: callMethod requesting method %s but only have %s methods!", method,
					methods.length);
			throw new LuaException("Error invoking.");
		}
		String label = methods[method];
		try {
			Object aresult = ComputerMethodExecutor.executor().invokeMethod(getClass(), this,
					IComputerTypeCaster.typeCastCC, label, arguments);
			return new Object[] { aresult };
		} catch (Exception exception) {
			LCLog.warn("Problem calling method from ComputerCraft driver!", exception);
			throw new LuaException(exception.getMessage());
		}
	}

	@Override
	public void attach(IComputerAccess computer) {
		computercraft_assertReady();
		synchronized (computercraft_icalist) {
			computercraft_icalist.add(computer);
		}
		try {
			ComputerCraftScuffMount mount = ComputerCraftScuffMount.generateMount();
			mount.init();
			computercraft_mounts.put(computer, mount);
			computer.mount("/lanteacraft", mount);
		} catch (Exception ex) {
			LCLog.fatal("Unable to generate Computer mount.", ex);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		computercraft_assertReady();
		synchronized (computercraft_icalist) {
			computercraft_icalist.remove(computer);
		}
		try {
			computer.unmount("/lanteacraft");
			ComputerCraftScuffMount mount = computercraft_mounts.get(computer);
			if (mount != null)
				mount.shutdown();
		} catch (Exception ex) {
			LCLog.fatal("Unable to garbage collect Computer mount.", ex);
		}

	}

	@Override
	public boolean equals(IPeripheral other) {
		if (TileEntity.class.isAssignableFrom(this.getClass()) && other instanceof TileEntity) {
			TileEntity me = TileEntity.class.cast(this);
			TileEntity them = (TileEntity) other;
			if (!me.getWorldObj().equals(them.getWorldObj()))
				return false;
			return me.xCoord == them.xCoord && me.yCoord == them.yCoord && me.zCoord == them.zCoord;
		}
		return false;
	}

}
