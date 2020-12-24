package dam.dii.p1.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * This will read and parse the encoded config file secrets.dat which contains
 * 1) JwtSecret, 2) SecretKey and salt from cry, 3)firebase uri and token
 *
 * @author SERGI
 *
 */
public class Secrets {

	private static final String ALGORITHM = "AES";

	public static void encrypt(byte[] magicKey, File rawFile, File outputFile) {
		doMagic(Cipher.ENCRYPT_MODE, magicKey, rawFile, outputFile);
	}

	public static void decrypt(byte[] magicKey, File encodedFile, File outputFile) {
		doMagic(Cipher.DECRYPT_MODE, magicKey, encodedFile, outputFile);
	}

	private static void doMagic(int cipherMode, byte[] magicKey, File file, File outputFile) {

		System.out.println(magicKey.length);
		try {
			Key secretKey = new SecretKeySpec(magicKey, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(cipherMode, secretKey);

			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fis.read(bytes);
			fis.close();

			byte[] outputBytes = cipher.doFinal(bytes);
			FileOutputStream os = new FileOutputStream(outputFile);
			os.write(outputBytes);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createKeyDat(File rawFile, SecretKey sc, byte[] jwtSecret, String firebaseSecret,

			String firebaseUrl, byte[] salt, char[] cryptoKey) {

		System.out.println("Secrets create key data");
		HashMap<String, Object> obj = new HashMap<String, Object>();
		obj.put("secretkey", sc);
		obj.put("jwtSecrety", jwtSecret);
		obj.put("firebaseSecret", firebaseSecret);
		obj.put("firebaseUrl", firebaseUrl);
		obj.put("cryptoKey", cryptoKey);
		obj.put("salt", salt);
		try {
			ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(rawFile));
			ois.writeObject(obj);
			ois.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public static HashMap<String, Object> readKeyDat(File rawFile) throws IOException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rawFile));
		try {
			return (HashMap<String, Object>) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
