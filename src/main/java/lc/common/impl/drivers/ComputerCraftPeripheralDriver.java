package lc.common.impl.drivers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers.DriverProvider;
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
		return getClass().getName().replace("Tile", "").replace("tile", "");
	}

	@Override
	public String[] getMethodNames() {
		if (computercraft_methodcache == null) {
			ArrayList<String> alist = new ArrayList<String>();
			Class<?> zz = getClass();
			Method[] methods = zz.getMethods();
			for (Method m : methods) {
				Annotation[] annotations = m.getAnnotations();
				if (annotations == null || annotations.length == 0)
					continue;
				Tag foundTag = null;
				for (int i = 0; i < annotations.length; i++) {
					Annotation annotation = annotations[i];
					if (annotation instanceof Tag && ((Tag) annotation).name().equals("ComputerCallable"))
						foundTag = (Tag) annotation;
					else if (annotation instanceof TagMap) {
						TagMap map = (TagMap) annotation;
						Tag[] tree = map.value();
						for (int k = 0; k < tree.length; k++) {
							Tag branch = tree[k];
							if (branch.name().equals("ComputerCallable"))
								foundTag = branch;
						}
					}
				}
				if (foundTag == null)
					continue;
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
			Object aresult = foundMethod.invoke(this, arguments);
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
