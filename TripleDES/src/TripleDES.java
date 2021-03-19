import java.security.Key;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TripleDES {
	
	public static void main(String[] args) {
		
		//Set String byte key and byte data as the input for encryption
		byte[] key = "876543218765432184142444".getBytes();
		byte[] data = "This is a message".getBytes();
		
		//Encryption type for key set to null
		Key deskey = null;
		//Set 3DES key using the previously set key
		DESedeKeySpec spec;
		try {
			spec = new DESedeKeySpec(key);
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
			//key for encrypt
			deskey = keyfactory.generateSecret(spec);
			
			//desede (encrypt decrypt encrypt)
			Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			
			byte[] CipherText = cipher.doFinal(data);
			
			//Convert byte cipher text to hex
			StringBuffer hexCiphertext = new StringBuffer();
			for (int i=0;i<CipherText.length;i++)
				hexCiphertext.append(Integer.toString((CipherText[i]&0xff)+0x100,16).substring(1));
			
			System.out.println("Ciphertext is:	"+hexCiphertext);
			
			//decryption
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			byte[] plaintext = cipher.doFinal(CipherText);
			
			System.out.println("Plaintext is:	"+new String(plaintext));
			
		} catch (InvalidKeyException ex) {
			Logger.getLogger(TripleDES.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(TripleDES.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidKeySpecException ex) {
			Logger.getLogger(TripleDES.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchPaddingException ex) {
			Logger.getLogger(TripleDES.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalBlockSizeException ex) {
			Logger.getLogger(TripleDES.class.getName()).log(Level.SEVERE, null, ex);
		} catch (BadPaddingException ex) {
			Logger.getLogger(TripleDES.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
