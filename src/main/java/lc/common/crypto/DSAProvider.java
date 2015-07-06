package lc.common.crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Wrapper around Sun's DSA implementation. Includes facilities to generate,
 * save and load keys and sign/validate payloads.
 * 
 * @author AfterLifeLochie
 *
 */
public class DSAProvider {

	/**
	 * <p>
	 * Generate a Sun-based DSA-SHA1PRNG 1024-byte key.
	 * </p>
	 * 
	 * @return A public-private key pair
	 * @throws NoSuchAlgorithmException
	 *             If the system does not support the algorithm, a
	 *             NoSuchAlgorithmException exception is raised. This may occur
	 *             only in countries where cryptographic controls are not
	 *             permitted to be used.
	 * @throws NoSuchProviderException
	 *             If the VM environment does not support the Sun-provided
	 *             cryptography system, a NoSuchProviderException is raised.
	 */
	public static KeyPair generate() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("DSA", "SUN");
		gen.initialize(1024, SecureRandom.getInstance("SHA1PRNG", "SUN"));
		return gen.generateKeyPair();
	}

	/**
	 * <p>
	 * Sign a payload of data with a key-pair.
	 * </p>
	 * 
	 * @param payload
	 *            The data to sign
	 * @param chain
	 *            The key-chain to sign with
	 * @return The signature of the payload data
	 * @throws InvalidKeyException
	 *             If the key provided is invalid or is not a public-private key
	 *             pair, an InvalidKeyException will be raised.
	 * @throws SignatureException
	 *             If the DSA algorithm or the Sun cryptography provider, a
	 *             SignatureException will be raised.
	 */
	public static byte[] digest(byte[] payload, KeyPair chain) throws InvalidKeyException, SignatureException {
		try {
			Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
			signature.initSign(chain.getPrivate());
			signature.update(payload);
			return signature.sign();
		} catch (NoSuchAlgorithmException e) {
			throw new SignatureException("Failed to find DSA algorithm.", e);
		} catch (NoSuchProviderException e) {
			throw new SignatureException("Failed to find Sun crypto provider.", e);
		}
	}

	/**
	 * <p>
	 * Validates a payload data matches a signature digest.
	 * </p>
	 * 
	 * @param digest
	 *            The signature digest
	 * @param payload
	 *            The payload data
	 * @param pk
	 *            The public key
	 * @return If the payload data plus public key produce the same logical
	 *         signature digest; that is, if the message matches the original
	 *         signature and if the signature is from the public key owner as
	 *         prescribed.
	 * @throws InvalidKeyException
	 *             If the key provided is invalid or is not a public key pair,
	 *             an InvalidKeyException will be raised.
	 * @throws SignatureException
	 *             If the DSA algorithm or the Sun cryptography provider, a
	 *             SignatureException will be raised.
	 */
	public static boolean verify(byte[] digest, byte[] payload, PublicKey pk) throws InvalidKeyException,
			SignatureException {
		try {
			Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
			signature.initVerify(pk);
			signature.update(payload);
			return signature.verify(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new SignatureException("Failed to find DSA algorithm.", e);
		} catch (NoSuchProviderException e) {
			throw new SignatureException("Failed to find Sun crypto provider.", e);
		}
	}

	/**
	 * Convert a private key to a PKCS8-format payload
	 * 
	 * @param key
	 *            The private key
	 * @return The key data
	 */
	public static byte[] toPKCS8(PrivateKey key) {
		return new PKCS8EncodedKeySpec(key.getEncoded()).getEncoded();
	}

	/**
	 * Convert a public key to a X509-format payload
	 * 
	 * @param key
	 *            The public key
	 * @return The key data
	 */
	public static byte[] toX509(PublicKey key) {
		return new X509EncodedKeySpec(key.getEncoded()).getEncoded();
	}

	/**
	 * Load a private key from a PKCS8-format payload
	 * 
	 * @param data
	 *            The payload data
	 * @return The private key result
	 * @throws NoSuchAlgorithmException
	 *             If the system does not support the algorithm, a
	 *             NoSuchAlgorithmException exception is raised. This may occur
	 *             only in countries where cryptographic controls are not
	 *             permitted to be used.
	 * @throws InvalidKeySpecException
	 *             If the key in not valid or is otherwise corrupt, an
	 *             InvalidKeySpecException
	 */
	public static PrivateKey fromPKCS8(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(data));
	}

	/**
	 * Load a public key from an X509-format payload
	 * 
	 * @param data
	 *            The payload data
	 * @return The public key result
	 * @throws NoSuchAlgorithmException
	 *             If the system does not support the algorithm, a
	 *             NoSuchAlgorithmException exception is raised. This may occur
	 *             only in countries where cryptographic controls are not
	 *             permitted to be used.
	 * @throws InvalidKeySpecException
	 *             If the key in not valid or is otherwise corrupt, an
	 *             InvalidKeySpecException
	 */
	public static PublicKey fromX509(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		return keyFactory.generatePublic(new X509EncodedKeySpec(data));
	}

}
