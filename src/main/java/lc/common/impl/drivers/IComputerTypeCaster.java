package lc.common.impl.drivers;

import lc.common.LCLog;

public interface IComputerTypeCaster {

	public static final ComputerCraftTypeCaster typeCastCC = new ComputerCraftTypeCaster();
	public static final OpenComputersTypeCaster typeCastOC = new OpenComputersTypeCaster();

	public Object performCastToType(Object xx, Class<?> yy) throws Exception;

	public Object castToComputerSafe(Object aresult) throws Exception;

	public static class ComputerCraftTypeCaster implements IComputerTypeCaster {

		@Override
		public Object performCastToType(Object xx, Class<?> yy) throws Exception {
			if (xx == null)
				return null;
			LCLog.debug("ComputerCraft driver: perform cast: %s to %s", xx.getClass().getName(), yy.getName());
			if (yy == Character.class || yy == char.class) {
				if (!(xx instanceof String))
					throw new Exception("Cannot cast " + xx.getClass().getSimpleName() + " to Character");
				String xxx = (String) xx;
				if (xxx.length() != 1)
					throw new Exception("Illegal Character length.");
				return xxx.charAt(0);
			}
			return xx;
		}

		@Override
		public Object castToComputerSafe(Object aresult) throws Exception {
			if (aresult == null)
				return null;
			LCLog.debug("ComputerCraft driver: perform 2luacast: %s", aresult.getClass().getName());
			if (aresult instanceof Enum<?>) {
				return ((Enum<?>) aresult).name();
			}
			return aresult;
		}

	}

	public static class OpenComputersTypeCaster implements IComputerTypeCaster {

		@Override
		public Object performCastToType(Object xx, Class<?> yy) throws Exception {
			if (xx == null)
				return null;
			LCLog.debug("OpenComputers driver: perform cast: %s to %s", xx.getClass().getName(), yy.getName());
			if (yy == Character.class || yy == char.class) {
				if (!(xx instanceof byte[]))
					throw new Exception("Cannot cast " + xx.getClass().getSimpleName() + " to Character");
				byte[] xxx = (byte[]) xx;
				if (xxx.length != 1)
					throw new Exception("Illegal Character length.");
				return (char) xxx[0];
			}
			if (yy == String.class) {
				if (!(xx instanceof byte[]))
					throw new Exception("Cannot cast " + xx.getClass().getSimpleName() + " to String");
				byte[] xxx = (byte[]) xx;
				StringBuilder m = new StringBuilder();
				for (int i = 0; i < xxx.length; i++)
					m.append(xxx[i]);
				return m.toString();
			}
			return xx;
		}

		@Override
		public Object castToComputerSafe(Object aresult) throws Exception {
			if (aresult == null)
				return null;
			LCLog.debug("OpenComputers driver: perform 2luacast: %s", aresult.getClass().getName());
			if (aresult instanceof Enum<?>) {
				return ((Enum<?>) aresult).name();
			}
			return aresult;
		}

	}
}