package dam.dii.p1.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.SecretKey;

import dam.dii.p1.HelloServlet;
import dam.dii.p1.services.Secrets;

public class FileUtils {

	public static File loadFileAsResource(String string, boolean empty) {

		System.out.println("leyendo file " + string);

		InputStream is = HelloServlet.class.getResourceAsStream(string);
		if (is == null)
			return null;
		try {
			File toReturn = File.createTempFile(String.valueOf(is.hashCode()), ".tmp");
			FileOutputStream fos = new FileOutputStream(toReturn);

			if (empty) {
				byte[] buffer = new byte[0];
				int isEnd = 0;
				isEnd = is.read(buffer);
				fos.write(buffer, 0, isEnd);
				fos.close();
				return toReturn;
			}
			byte[] buffer = new byte[1024];
			int isEnd = 0;
			while (isEnd != -1) {
				isEnd = is.read(buffer);
				fos.write(buffer, 0, isEnd);
				fos.close();
				return toReturn;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void createFileKey(File file, File file2, SecretKey sc, byte[] jwtSecret, String firebaseSecret,
			String firebaseUrl, byte[] salt, char[] cryptoKey, byte[] magicKey) {

		Secrets.createKeyDat(file, sc, jwtSecret, firebaseSecret, firebaseUrl, salt, cryptoKey);
		Secrets.encrypt(magicKey, file, file2);
	}
}
