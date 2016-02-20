package lc.digital.vm.kahlua;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import lc.repack.se.krka.kahlua.Version;
import lc.repack.se.krka.kahlua.j2se.KahluaTableImpl;
import lc.repack.se.krka.kahlua.j2se.MathLib;
import lc.repack.se.krka.kahlua.luaj.compiler.LuaCompiler;
import lc.repack.se.krka.kahlua.stdlib.*;
import lc.repack.se.krka.kahlua.test.UserdataArray;
import lc.repack.se.krka.kahlua.threading.BlockingKahluaThread;
import lc.repack.se.krka.kahlua.vm.*;

public class J2SEMCPlatform implements Platform {
	private static J2SEMCPlatform INSTANCE = new J2SEMCPlatform();

	public static J2SEMCPlatform getInstance() {
		return INSTANCE;
	}

	public static String USRC_PATH = "/assets/pcl_lc/drivers/firmware/";

	public static LuaClosure loadByteCodeFromResource(String name, KahluaTable environment) {
		InputStream stream = environment.getClass().getResourceAsStream(USRC_PATH + name);
		if (stream == null)
			return null;
		try {
			return LuaCompiler.loadis(stream, "kahlua.lua", environment);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static void setupLibrary(KahluaTable env, KahluaThread workerThread, String library) {
		LuaClosure closure = loadByteCodeFromResource(library, env);
		if (closure == null)
			throw new RuntimeException("Could not load " + library);
		workerThread.call(closure, null, null, null);
	}

	@Override
	public double pow(double x, double y) {
		return Math.pow(x, y);
	}

	@Override
	public KahluaTable newTable() {
		return new KahluaTableImpl(new ConcurrentHashMap<Object, Object>());
	}

	@Override
	public KahluaTable newEnvironment() {
		KahluaTable env = newTable();
		setupEnvironment(env);
		return env;
	}

	@Override
	public void setupEnvironment(KahluaTable env) {
		env.wipe();
		env.rawset("_G", env);
		env.rawset("_VERSION", Version.VERSION + " (J2SE_MC)");

		MathLib.register(this, env);
		BaseLib.register(env);
		RandomLib.register(this, env);
		UserdataArray.register(this, env);
		StringLib.register(this, env);
		CoroutineLib.register(this, env);
		OsLib.register(this, env);
		TableLib.register(this, env);
		LuaCompiler.register(env);

		KahluaThread workerThread = setupWorkerThread(env);
		setupLibrary(env, workerThread, "kahlua.lua");
	}

	private KahluaThread setupWorkerThread(KahluaTable env) {
		BlockingKahluaThread thread = new BlockingKahluaThread(this, env);
		KahluaUtil.setWorkerThread(env, thread);
		return thread;
	}

}
