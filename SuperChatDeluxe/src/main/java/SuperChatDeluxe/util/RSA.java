package SuperChatDeluxe.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.stereotype.Component;

@Component
public class RSA {
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	public RSA() {
		init();
	}
	
	public RSA(PublicKey pubKey, PrivateKey privKey) {
		this.publicKey = pubKey;
		this.privateKey = privKey;
	}
	
	public void init() {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			KeyPair pair = generator.generateKeyPair();
			privateKey = pair.getPrivate();
			publicKey = pair.getPublic();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initFromStrings(String privKey, String pubKey) {
		try {
			PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(privKey));
		
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			privateKey = keyFactory.generatePrivate(keySpecPrivate);
			
			X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(pubKey));
			
			publicKey = keyFactory.generatePublic(keySpecPublic);
		}
		catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public PublicKey createPublicKeyFromString(String pubKey) {
		KeyFactory keyFactory = null;
		PublicKey publicKeyLocal = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(pubKey));
		
		try {
			publicKeyLocal = keyFactory.generatePublic(keySpecPublic);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		return publicKeyLocal;
	}
	
	public String encrypt(String message, PublicKey key) throws Exception {
		byte[] messageToBytes = message.getBytes();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedBytes = cipher.doFinal(messageToBytes);
		return encode(encryptedBytes);
	}
	
	public String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	
	public String decrypt(String encryptedMessage) throws Exception {
		byte[] encryptedBytes = decode(encryptedMessage);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
		return new String(decryptedMessage, "UTF8");
	}
	
	public byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}
	
//	public static void main(String[] args) throws Exception {
//		RSA newRSA = new RSA();
//		newRSA.printKeys();
//		String privKey = newRSA.encode(newRSA.getPrivateKey().getEncoded());
//		String pubKey = newRSA.encode(newRSA.getPublicKey().getEncoded());
//		newRSA.init();
//		newRSA.printKeys();
//		newRSA.initFromStrings(privKey, pubKey);
//		newRSA.printKeys();
//		String encrypted = newRSA.encrypt("Hello its me!");
//		String decoded = newRSA.decrypt(encrypted);
//		System.out.println(decoded);
//		
//	}
	
	public void printKeys() {
		System.out.println("Public key: " + encode(publicKey.getEncoded()));
		System.out.println("Private key: " + encode(privateKey.getEncoded()));

		
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	
	
}
