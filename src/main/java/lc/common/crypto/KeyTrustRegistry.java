package lc.common.crypto;

import java.security.KeyStoreException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Certificate registry. Used to keep a chain of keys and to determine if a
 * particular key is known by the trust registry.
 * 
 * @author AfterLifeLochie
 *
 */
public class KeyTrustRegistry {

	/** The local registry key map */
	private final HashMap<String, PublicKey> keyMap = new HashMap<String, PublicKey>();

	/**
	 * Create a new key trust registry with a blank key chain.
	 */
	public KeyTrustRegistry() {
		/* Do nothing */
	}

	/**
	 * <p>
	 * Place a key into the registry. The key and the label must be unique in
	 * the registry. The label-key association is recorded until the key is
	 * forgotten.
	 * </p>
	 * 
	 * @param label
	 *            The label for the key
	 * @param pk
	 *            The public key
	 * @throws KeyStoreException
	 *             If the key store already contains a key of this label or of
	 *             this key.
	 */
	public final void placeKey(String label, PublicKey pk) throws KeyStoreException {
		synchronized (keyMap) {
			if (keyMap.containsKey(label))
				throw new KeyStoreException("Entry with label already exists");
			if (keyMap.containsValue(pk))
				throw new KeyStoreException("Public key already in key map");
			keyMap.put(label, pk);
		}
	}

	/**
	 * <p>
	 * Causes the registry to forget about a stored label-key association,
	 * removing the public key from the trust chain.
	 * </p>
	 * 
	 * @param label
	 *            The label of the key to forget
	 */
	public final void forget(String label) {
		synchronized (keyMap) {
			keyMap.remove(label);
		}
	}

	/**
	 * <p>
	 * Causes the registry to forget about all instances of a public key,
	 * removing it from any places in the trust chain.
	 * </p>
	 * 
	 * @param pk
	 *            The public key to forget
	 */
	public final void forget(PublicKey pk) {
		synchronized (keyMap) {
			String key = label(pk);
			if (key != null)
				keyMap.remove(key);
		}
	}

	/**
	 * <p>
	 * Request the label associated with a public key.
	 * </p>
	 * 
	 * @param pk
	 *            The public key
	 * @return The label, or null if the key is not known by the registry
	 */
	public final String label(PublicKey pk) {
		synchronized (keyMap) {
			String key = null;
			for (Entry<String, PublicKey> pair : keyMap.entrySet())
				if (pair.getValue().equals(pk))
					key = pair.getKey();
			return key;
		}
	}

	/**
	 * <p>
	 * Checks the chain to see if the public key provided is in the chain. If
	 * the public key is in the chain, true is returned; else false is returned.
	 * </p>
	 * 
	 * @param pk
	 *            The public key
	 * @return If the key is in the trust chain
	 */
	public final boolean checkTrust(PublicKey pk) {
		synchronized (keyMap) {
			for (Entry<String, PublicKey> pair : keyMap.entrySet())
				if (pair.getValue().equals(pk))
					return true;
			return false;
		}
	}

	/**
	 * <p>
	 * Empties the key chain. All keys are immediately released and forgotten
	 * from the key-chain
	 * </p>
	 */
	public final void purge() {
		synchronized (keyMap) {
			keyMap.clear();
		}
	}

	/**
	 * <p>
	 * Fetches the contents of all public keys in the chain.
	 * </p>
	 * 
	 * @return The contents of the key chain
	 */
	public final PublicKey[] contents() {
		synchronized (keyMap) {
			return keyMap.values().toArray(new PublicKey[0]);
		}
	}

}
