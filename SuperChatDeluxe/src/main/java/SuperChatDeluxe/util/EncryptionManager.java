package SuperChatDeluxe.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class EncryptionManager {
	private PublicKey publicKey;
	
	private static final String PUBLIC_KEY_STRING = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAMdCNSlCsZINudTHuknc4CdTL7cN9KirfInUyL5TNI6fsr+qWe3ipQGBDtELk9HIbkR1uxe3nIsbCM9efInxCVx4qPU+0SRIPnM2egVqIKdMAbAXxAe+lUNQRhTmTDzDIrPcykrUmXq/p49Le2Nj8DbubWn1JkwZZqgwHAmkhAwIDAQAB";
	
	public void initFromStrings() {
		try {
			X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(PUBLIC_KEY_STRING));
		
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpecPublic);
		}
		catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	private String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	
	private byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}
	
	public String encrypt(String message) throws Exception {
		byte[] messageToBytes = message.getBytes();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(messageToBytes);
		return encode(encryptedBytes);
	}
}
