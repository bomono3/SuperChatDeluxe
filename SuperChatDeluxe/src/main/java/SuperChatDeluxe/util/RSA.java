package SuperChatDeluxe.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA {
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	private static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAMdCNSlCsZINudTHuknc4CdTL7cN9KirfInUyL5TNI6fsr+qWe3ipQGBDtELk9HIbkR1uxe3nIsbCM9efInxCVx4qPU+0SRIPnM2egVqIKdMAbAXxAe+lUNQRhTmTDzDIrPcykrUmXq/p49Le2Nj8DbubWn1JkwZZqgwHAmkhAwIDAQAB";
	private static final String PRIVATE_KEY_STRING = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIAx0I1KUKxkg251Me6SdzgJ1Mvtw30qKt8idTIvlM0jp+yv6pZ7eKlAYEO0QuT0chuRHW7F7ecixsIz158ifEJXHio9T7RJEg+czZ6BWogp0wBsBfEB76VQ1BGFOZMPMMis9zKStSZer+nj0t7Y2PwNu5tafUmTBlmqDAcCaSEDAgMBAAECgYADAj2iVfhZwMwtkgDs2AbbS65xENtckIT2vWYhxOh0U3NpWeGizDK99N2tiR65epvuVglqTv6b76mEPQJBC7v91sYk+cLw9pXb1KymqnhFS24uvZ5ExNV50bJXdbR71GclFh9+hPj1Uuq0cDGgqG6OuKGf0+s6lyZsPfh6VUTU3QJBALLZ67LzX3M4PnmVhjm6zeF3yIN7NhyklKcsrVMzoRwljj0imaXF9dZiW9IZoxPb3m1QhMEIVFfntKpmUBEgON8CQQC3fgKVzYXtpwqbRmgloHzyXsvCwqRpCe/f6WIijfEi139ZvLDQZRzb2NO840g98ukAsnd0o2QRJh/JoZ0+QohdAkBBJwWnwOSqknifzgqBiT1qvzn6O64wrPvCEpJpVst/92ZjevFB5GVz3YzZcqFnoJbcd5KEx0YCZ9P2pyLPuk89AkEAp0/hL5M9oZuE0lz6rgSOqbnCdg5hN931uefmsbdXvvtchgjF+N+Z1uT4/O0JfGM4Dois38/0eKaYqZQR+ylTiQJAO6PzzNPActDGWCec8iDBGhbiS2sUxE/T8C61GpDRvZnLetHBF2/UKKXE7hiFr5TnomI4RSOtFlnRYIi+GVv8Og==";
	
	
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
	
	public void initFromStrings() {
		try {
			X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(PUBLIC_KEY_STRING));
			PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(PRIVATE_KEY_STRING));
		
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpecPublic);
			privateKey = keyFactory.generatePrivate(keySpecPrivate);
		}
		catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	public String encrypt(String message) throws Exception {
		byte[] messageToBytes = message.getBytes();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(messageToBytes);
		return encode(encryptedBytes);
	}
	
	private String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	
	public String decrypt(String encryptedMessage) throws Exception {
		byte[] encryptedBytes = decode(encryptedMessage);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
		return new String(decryptedMessage, "UTF8");
	}
	
	private byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}
	
	public static void main(String[] args) {
		RSA rsa = new RSA();
		rsa.initFromStrings();
		
		try {
			String encryptedMessage = rsa.encrypt("Hello World");
			String deruptedMessage = rsa.decrypt(encryptedMessage);
			
			System.out.println("Encryped: " + encryptedMessage);
			System.out.println("Decryped: " + deruptedMessage);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printKeys() {
		System.out.println("Public key: " + encode(publicKey.getEncoded()));
		System.out.println("Public key: " + encode(privateKey.getEncoded()));

		
	}
	
}
