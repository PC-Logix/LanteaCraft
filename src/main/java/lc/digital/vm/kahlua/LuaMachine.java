package lc.digital.vm.kahlua;

import java.io.IOException;

import lc.repack.se.krka.kahlua.converter.KahluaConverterManager;
import lc.repack.se.krka.kahlua.integration.LuaCaller;
import lc.repack.se.krka.kahlua.integration.annotations.Desc;
import lc.repack.se.krka.kahlua.integration.annotations.LuaMethod;
import lc.repack.se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import lc.repack.se.krka.kahlua.integration.expose.ReturnValues;
import lc.repack.se.krka.kahlua.luaj.compiler.LuaCompiler;
import lc.repack.se.krka.kahlua.vm.KahluaTable;
import lc.repack.se.krka.kahlua.vm.KahluaThread;
import lc.repack.se.krka.kahlua.vm.LuaClosure;

public class LuaMachine {

	private final J2SEMCPlatform platform = J2SEMCPlatform.getInstance();

	private KahluaConverterManager converterManager;
	private KahluaTable env;
	private KahluaThread thread;
	private LuaCaller caller;
	private LuaJavaClassExposer exposer;

	public void init() throws LuaMachineException {
		try {
			this.converterManager = new KahluaConverterManager();
			this.caller = new LuaCaller(converterManager);
			this.env = platform.newEnvironment();
			this.thread = new KahluaThread(platform, env);
			this.exposer = new LuaJavaClassExposer(converterManager, platform, env);
		} catch (RuntimeException kaex) {
			throw new LuaMachineException(kaex.getMessage(), kaex);
		}
	}

	public void advance() throws LuaMachineException {

	}

	public void destroy() throws LuaMachineException {

	}

}
