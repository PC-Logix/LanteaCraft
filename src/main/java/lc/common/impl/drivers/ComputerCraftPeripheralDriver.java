package lc.common.impl.drivers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers.DriverProvider;
import lc.api.jit.ASMTag;
import lc.api.jit.Tag;
import lc.api.jit.TagMap;
import lc.common.LCLog;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

@DriverProvider(type = IntegrationType.COMPUTERS)
public class ComputerCraftPeripheralDriver implements IPeripheral {

	private String[] computercraft_methodcache;

	public ComputerCraftPeripheralDriver() {
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void detach(IComputerAccess computer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(IPeripheral other) {
		// TODO Auto-generated method stub
		return false;
	}

}
