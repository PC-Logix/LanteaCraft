package lc.coremod;

import java.util.ArrayList;
import java.util.HashMap;

import lc.common.LCLog;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class HotClassCache {

	private HashMap<String, byte[]> klassBlobs = new HashMap<String, byte[]>();
	private ArrayList<String> forcedKlasses = new ArrayList<String>();

	public void forceCache(String suggestedName, byte[] blob) {
		klassBlobs.put(suggestedName, blob);
	}

	public void suggestCache(String suggestedName, byte[] blob) {
		ClassNode klass = new ClassNode();
		ClassReader classReader = new ClassReader(blob);
		classReader.accept(klass, 0);
		String internalName = klass.name.replace("/", ".");
		if (!suggestedName.equals(internalName)) {
			LCLog.warn("Suggested class name %s invalid, expected %s. Renaming...", suggestedName, internalName);
			suggestedName = internalName;
		}
		boolean doCache = forcedKlasses.contains(suggestedName);
		if (suggestedName.startsWith("lc.") || (klass.access & Opcodes.ACC_INTERFACE) != 0)
			doCache = true;
		if (doCache)
			forceCache(suggestedName, blob);
	}

	public byte[] getCached(String klass) {
		klass = klass.replace("/", ".");
		if (!forcedKlasses.contains(klass))
			forcedKlasses.add(klass);
		if (!klassBlobs.containsKey(klass)) {
			try {
				getClass().getClassLoader().loadClass(klass);
			} catch (ClassNotFoundException err) {
				LCLog.warn("Can't find class %s.", klass);
			}
		}
		return klassBlobs.get(klass);
	}

	public String[] forcedKlasses() {
		return forcedKlasses.toArray(new String[0]);
	}

	public String[] cachedKlasses() {
		return klassBlobs.keySet().toArray(new String[0]);
	}
}
