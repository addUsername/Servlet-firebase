package dam.dii.p1.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Servlet;
import javax.xml.bind.DatatypeConverter;

/**
 * Ok.. here we are gonna use AES encryption following these bc yes
 *  https://stackoverflow.com/a/9537017/13771772
 *  https://stackoverflow.com/questions/11667480/why-pbe-generates-same-key-with-different-salt-and-iteration-count/11684345
 *  https://stackoverflow.com/questions/35907877/aes-encryption-ivs 
 * @author SERGI
 *
 */
public class Crypt {

	private SecretKey secret;
	private String keyPath;
	// ok, salt should be random so "PBKDF2WithHmacSHA1" generates a new secret each time BUT then data wont be available.. really?
	// with "PBEWithSHA1andDESede" algorithm salt is not used ( new PBEKeySpec(secret); ), so key dont changes which is good (persistence)..
	// and probably bad for the same reason¿?. So we use good algorithm and a constant salt knowing that it doesnt make any sense, pls bring MD5 back  
	
	public Crypt(String keyPath) {
		this.keyPath = keyPath;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyPath));
			secret = ((SecretKey) ois.readObject());
			ois.close();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Crypt(SecretKey sc) {
		this.secret = sc;
	}
	/**
	 * This method is for documentation purposes, it store SecretKey as "key.dat" file. Key changes everytime it is generated so 
	 * we just call it once and store that key file carefully.
	 * @param key
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	
	public static SecretKey generateKey(String path) {
		System.out.println("FILE NO EXISTT!!");
		byte[] salt = "salt".getBytes();
        char[] key = "moreSecrets".toCharArray();
        String FILENAME = path;//"key.dat";
        String ALGORITHM = "PBKDF2WithHmacSHA1";
        SecretKey mySecret = null;
        try {
	        SecretKeyFactory factory= SecretKeyFactory.getInstance(ALGORITHM);
		    KeySpec spec = new PBEKeySpec(key, salt, 65536, 256);
		    SecretKey tmp = factory.generateSecret(spec);
		    mySecret = new SecretKeySpec(tmp.getEncoded(), "AES");
		    
        	ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(FILENAME));		
			ois.writeObject(mySecret);
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mySecret;
	}
	
	public String encode(String rawPass) {
		// whomever calls this method should do encode("..".getBytes("UTF-8")); Not now beach

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParams = createIV(cipher.getBlockSize());			
			cipher.init(Cipher.ENCRYPT_MODE, secret,ivParams);			
			/*
			baos.write(cipher.getIV());
			// byte[] ciphertext = cipher.doFinal(rawPass);
			
			CipherOutputStream cos = new CipherOutputStream(baos, cipher);
			cos.write(rawPass.getBytes());
			*/
			return byteArraysAsString(cipher.doFinal(rawPass.getBytes(StandardCharsets.ISO_8859_1)), ivParams.getIV());
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException  | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			// there are not correct answers
			e.printStackTrace();
		}		
		return null;
	}
	public byte[] decode(String encryptedPass) {
		// not used.. we compare encrypted passwords directly ¿?
		byte[][] pair = stringToByteArray(encryptedPass);
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParams = new IvParameterSpec(pair[1]);
			cipher.init(Cipher.DECRYPT_MODE, secret, ivParams);
			return cipher.doFinal(pair[0]);
			/*
			ByteArrayInputStream bais = new ByteArrayInputStream(stringToByteArray(encryptedPass));
			
			IvParameterSpec ivParams = readIV(cipher.getBlockSize(), bais);
			cipher.init(Cipher.DECRYPT_MODE, secret, ivParams);
			
			final byte[] buf = new byte[1_024];
			CipherInputStream cis = new CipherInputStream(bais, cipher);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			int read;
			while ((read = cis.read(buf)) != -1) {
			    baos.write(buf, 0, read);
			}
			return baos.toByteArray();
			//return cipher.doFinal(encryptedPass.getBytes());
			*/
			
		//lel	
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

	private IvParameterSpec createIV(final int ivSizeBytes) {
		
        byte[] iv = new byte[ivSizeBytes];
        //delete this?
        SecureRandom theRNG = new SecureRandom();
        theRNG.nextBytes(iv);        
        return new IvParameterSpec(iv);
    }	
	private IvParameterSpec readIV(int blockSize, InputStream is) throws IOException {
		// TODO Auto-generated method stub
		final byte[] iv = new byte[blockSize];
        int offset = 0;
        while (offset < blockSize) {
            int read = is.read(iv, offset, blockSize - offset);
            if (read == -1) {
                throw new IOException("Too few bytes for IV in input stream");
            }
            offset += read;
        }
        return new IvParameterSpec(iv);
	}
	/**
	 * This byte[] is the return from cipher.doFinal(), and we want to store it in firebase.. but 
	 * new String(ciphertext,StandardCharsets.ISO_8859_1) generates some characters that have the need of been escaped..
	 * The idea is persist these bytes as a String of int.. which is safe but longer
	 * @param bytes
	 * @return
	 */
	private String byteArraysAsString(byte[] bytes, byte[] iv) {
		//return new BigInteger(1, bytes).toString();
		System.out.println(bytes.length);
		System.out.println("byte 2" + bytes[2]);
		System.out.println(iv.length);
		/*ByteBuffer wrapped = ByteBuffer.wrap(bytes);
		Long pass = wrapped.getLong(0);
		
		ByteBuffer wrapped2 = ByteBuffer.wrap(iv);
		Long ivParams = wrapped2.getLong(0);*/
		char[] keyHexChars = new char[bytes.length*2];
		int i = -1;
		for(byte b: bytes) {
			
			keyHexChars[++i] = Character.forDigit((b >> 4) & 0xF, 16);
		    keyHexChars[++i] = Character.forDigit((b & 0xF), 16);		    
		    
		}
		
		char[] keyHexIv = new char[iv.length*2];
		i = -1;
		for(byte b: iv) {
			
			keyHexIv[++i] = Character.forDigit((b >> 4) & 0xF, 16);
		    keyHexIv[++i] = Character.forDigit((b & 0xF), 16);		    
		    
		}
		return new String(keyHexChars)+"."+new String(keyHexIv);
	}
	private byte[][] stringToByteArray(String string) {

		String[] pair = string.split("\\.");

		System.out.println(string);
		System.out.println(pair.length);
		
		/*
		ByteBuffer dbuf = ByteBuffer.allocate(16);
		dbuf.putLong(Long.parseLong(pair[0]));
		System.out.println("byte 2" + dbuf.array()[2]);
		
		ByteBuffer dbuf2 = ByteBuffer.allocate(16);
		dbuf2.putLong(Long.parseLong(pair[1]));	
		byte[][] toReturn = {dbuf.array() , dbuf2.array()};
		*/
		byte[][] toReturn = {DatatypeConverter.parseHexBinary(pair[0]) , DatatypeConverter.parseHexBinary(pair[1])};
		return toReturn;
	}
}
