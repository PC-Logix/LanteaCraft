package lc.common.impl.drivers;

import java.lang.reflect.Method;
import java.util.ArrayList;

import lc.api.components.IntegrationType;
import lc.api.jit.ASMTag;
import lc.api.jit.DeviceDrivers.DriverProvider;
import lc.api.jit.DeviceDrivers.DriverRTCallback;
import lc.api.jit.Tag;
import lc.common.LCLog;
import net.minecraft.tileentity.TileEntity;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@DriverProvider(type = IntegrationType.COMPUTERS)
public class ComputerCraftPeripheralDriver implements IPeripheral {

	private String[] computercraft_methodcache;
	private ArrayList<IComputerAccess> computercraft_icalist;

	public ComputerCraftPeripheralDriver() {
		// TODO Auto-generated constructor stub
	}

	private void computercraft_assertReady() {
		if (computercraft_icalist == null) {
			computercraft_icalist = new ArrayList<IComputerAccess>();
		}
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
		String label = computercraft_methodcache[method];
		Method foundMethod = null;
		for (Method m : getClass().getMethods())
			if (m.getName().equals(label))
				foundMethod = m;
		if (foundMethod == null)
			throw new LuaException("No such method.");
		try {
			Class<?>[] types = foundMethod.getParameterTypes();
			if (arguments.length != types.length)
				throw new Exception("Incorrect number of parameters.");
			Object[] aargs = new Object[arguments.length];
			for (int i = 0; i < aargs.length; i++)
				aargs[i] = ComputerCraftDriverManager.performCastToType(arguments[i], types[i]);
			Object aresult = foundMethod.invoke(this, aargs);
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
	}

	@Override
	public void detach(IComputerAccess computer) {
		computercraft_assertReady();
		synchronized (computercraft_icalist) {
			computercraft_icalist.remove(computer);
		}
	}

	@Override
	public boolean equals(IPeripheral other) {
		if (TileEntity.class.isAssignableFrom(this.getClass()) && other instanceof TileEntity) {
			TileEntity me = TileEntity.class.cast(this);
			TileEntity them = (TileEntity) other;
			if (!me.getWorld().equals(them.getWorld()))
				return false;
			return me.getPos().equals(them.getPos());
		}
		return false;
	}

}
