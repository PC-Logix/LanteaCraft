package lc.common.crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DSAProvider {

	public byte[] digest(byte[] payload, KeyPair chain) throws InvalidKeyException, SignatureException {
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

	public boolean verify(byte[] digest, byte[] payload, PublicKey pk) throws InvalidKeyException, SignatureException {
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

	public byte[] toPKCS8(PrivateKey key) {
		return new PKCS8EncodedKeySpec(key.getEncoded()).getEncoded();
	}

	public byte[] toX509(PublicKey key) {
		return new X509EncodedKeySpec(key.getEncoded()).getEncoded();
	}

	public PrivateKey fromPKCS8(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(data));
	}

	public PublicKey fromX509(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		return keyFactory.generatePublic(new X509EncodedKeySpec(data));
	}

}
