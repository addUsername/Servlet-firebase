package dam.dii.p1.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.servlet.Servlet;

/**
 * Ok.. here we are gonna use AES encryption following these bc yes
 *  https://stackoverflow.com/a/9537017/13771772
 *  https://stackoverflow.com/questions/11667480/why-pbe-generates-same-key-with-different-salt-and-iteration-count/11684345
 * 
 * @author SERGI
 *
 */
public class Crypt {

	private SecretKey secret;
	// ok, salt should be random so "PBKDF2WithHmacSHA1" generates a new secret each time BUT then data wont be available.. really?
	// with "PBEWithSHA1andDESede" algorithm salt is not used ( new PBEKeySpec(secret); ), so key dont changes which is good (persistence)..
	// and probably bad for the same reason¿?. So we use good algorithm and a constant salt knowing that it doesnt make any sense, pls bring MD5 back  
		
	public Crypt(String keyPath) {
		//File mah = new File(keyPath);
		//System.out.println(mah.exists());
		//System.out.println(mah.getAbsolutePath());
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyPath));
			secret = ((SecretKey) ois.readObject());
			ois.close();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * This method is for documentation purposes, it store SecretKey as "key.dat" file. Key changes everytime it is generated so 
	 * we just call it once and store that key file carefully.
	 * @param key
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	/*
	public static void generateKey() throws InvalidKeySpecException, NoSuchAlgorithmException{
		
		byte[] salt = "salt".getBytes();
        char[] key = "moreSecrets".toCharArray();
        String FILENAME = "key.dat";
        String ALGORITHM = "PBKDF2WithHmacSHA1";
        SecretKey mySecret;
        
        SecretKeyFactory factory= SecretKeyFactory.getInstance(ALGORITHM);
	    KeySpec spec = new PBEKeySpec(key, salt, 65536, 256);
	    SecretKey tmp = factory.generateSecret(spec);
	    mySecret = new SecretKeySpec(tmp.getEncoded(), "AES");

        try {
        	ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(FILENAME));		
			ois.writeObject(mySecret);
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	*/
	public String encode(byte[] rawPass) {
		// whomever calls this method should do encode("..".getBytes("UTF-8"));		
		try {
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);			
			byte[] ciphertext = cipher.doFinal(rawPass);			
			return bitArrayToStringOfInts(ciphertext);
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// there are not correct answers
			e.printStackTrace();
		}		
		return null;
	}
	public byte[] decode(String encryptedPass) {
		// not used.. we compare encrypted passwords directly
		
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret);
			
			return cipher.doFinal(encryptedPass.getBytes());
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * This byte[] is the return from cipher.doFinal(), and we want to store it in firebase.. but 
	 * new String(ciphertext,StandardCharsets.ISO_8859_1) generates some characters that have the need of been escaped..
	 * The idea is persist these bytes as a String of int.. which is safe but longer
	 * @param bytes
	 * @return
	 */
	private String bitArrayToStringOfInts(byte[] bytes) {
		String toReturn = "";
		for(byte b: bytes ) {
			toReturn +=  ","+(int) b;
		}
		return toReturn;
	}

}
