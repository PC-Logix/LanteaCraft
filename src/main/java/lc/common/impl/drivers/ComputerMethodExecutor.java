package lc.common.impl.drivers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import lc.api.jit.ASMTag;
import lc.api.jit.Tag;
import lc.common.LCLog;

public class ComputerMethodExecutor {

	private static ComputerMethodExecutor executor;

	public static ComputerMethodExecutor executor() {
		return ComputerMethodExecutor.executor;
	}

	static {
		ComputerMethodExecutor.executor = new ComputerMethodExecutor();
	}

	private HashMap<String, ArrayList<String>> nametable;

	private ComputerMethodExecutor() {
		nametable = new HashMap<String, ArrayList<String>>();
	}

	private ArrayList<String> cacheMethods(Class<?> klass) {
		ArrayList<String> alist = new ArrayList<String>();
		Method[] methods = klass.getMethods();
		for (Method m : methods) {
			Tag foundTag = ASMTag.findTag(getClass(), m, "ComputerCallable");
			if (foundTag == null)
				continue;
			LCLog.debug("ComputerMethodExecutor: Caching method %s %s", klass.getName(), m.getName());
			alist.add(m.getName());
		}
		nametable.put(klass.getName(), alist);
		return alist;
	}

	public String[] getMethods(Class<?> klass) {
		ArrayList<String> table = nametable.get(klass.getName());
		if (table == null)
			return cacheMethods(klass).toArray(new String[0]);
		return table.toArray(new String[0]);
	}

	public Object invokeMethod(Class<?> klass, Object cle, IComputerTypeCaster tcs, String method, Object[] varargs)
			throws Exception {
		ArrayList<String> table = nametable.get(klass.getName());
		if (table == null)
			table = cacheMethods(klass);
		if (!table.contains(method))
			throw new Exception("No such method.");
		Method m0 = null;
		Method[] methods = klass.getMethods();
		for (Method m : methods) {
			Tag foundTag = ASMTag.findTag(getClass(), m, "ComputerCallable");
			if (foundTag == null)
				continue;
			if (m.getName().equals(method))
				m0 = m;
		}
		if (m0 == null)
			throw new Exception("No such method.");
		try {
			Class<?>[] types = m0.getParameterTypes();
			if (varargs.length != types.length)
				throw new Exception("Incorrect number of parameters.");
			Object[] aargs = new Object[varargs.length];
			for (int i = 0; i < aargs.length; i++)
				aargs[i] = tcs.performCastToType(varargs[i], types[i]);
			Object aresult = m0.invoke(cle, aargs);
			Object vrt = tcs.castToComputerSafe(aresult);
			return vrt;
		} catch (Exception exception) {
			LCLog.warn("Problem calling method from ComputerMethodExecutor!", exception);
			throw new Exception(exception.getMessage());
		}

	}

}
