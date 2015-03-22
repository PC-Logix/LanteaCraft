package lc.common.crypto;

import java.security.KeyStoreException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map.Entry;

public class KeyTrustRegistry {

	private final HashMap<String, PublicKey> keyMap = new HashMap<String, PublicKey>();

	public final void placeKey(String label, PublicKey pk) throws KeyStoreException {
		synchronized (keyMap) {
			if (keyMap.containsKey(label))
				throw new KeyStoreException("Entry with label already exists");
			if (keyMap.containsValue(pk))
				throw new KeyStoreException("Public key already in key map");
			keyMap.put(label, pk);
		}
	}

	public final void forget(String label) {
		synchronized (keyMap) {
			keyMap.remove(label);
		}
	}

	public final void forget(PublicKey pk) {
		synchronized (keyMap) {
			String key = label(pk);
			if (key != null)
				keyMap.remove(key);
		}
	}

	public final String label(PublicKey pk) {
		synchronized (keyMap) {
			String key = null;
			for (Entry<String, PublicKey> pair : keyMap.entrySet())
				if (pair.getValue().equals(pk))
					key = pair.getKey();
			return key;
		}
	}

	public final boolean checkTrust(PublicKey pk) {
		synchronized (keyMap) {
			for (Entry<String, PublicKey> pair : keyMap.entrySet())
				if (pair.getValue().equals(pk))
					return true;
			return false;
		}
	}

}
