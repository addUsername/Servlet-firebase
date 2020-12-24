package dam.dii.p1.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import dam.dii.p1.MyServlet;

/**
 * Ok.. here we are gonna use AES encryption following these bc yes, persist as
 * hex.hex.. it works by now, should refactor everything
 *
 * https://stackoverflow.com/a/9537017/13771772
 * https://stackoverflow.com/questions/11667480/why-pbe-generates-same-key-with-different-salt-and-iteration-count/11684345
 * https://stackoverflow.com/questions/35907877/aes-encryption-ivs
 * https://security.stackexchange.com/questions/48000/why-would-you-need-a-salt-for-aes-cbs-when-iv-is-already-randomly-generated-and
 *
 * @author SERGI
 *
 */
public class Crypt {

	private SecretKey secret;
	private String keyPath;
	// ok, salt should be random so "PBKDF2WithHmacSHA1" generates a new secret each
	// time BUT then data wont be available.. really? YES
	// with "PBEWithSHA1andDESede" algorithm salt is not used (new
	// PBEKeySpec(secret),
	// so key dont changes which is good (persistence)..
	// and probably bad for the same reason¿? YES. So we use good algorithm and a
	// constant salt knowing that it doesnt make any sense, pls bring MD5 back

	/**
	 * TODO see what happens when running a jar If file.exists() read it.. it
	 * contains {@link SecretKey} sc
	 *
	 * @param keyPath Where key.dat file is, see {@link MyServlet#MyServlet()}
	 */
	public Crypt(String keyPath) {
		this.keyPath = keyPath;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyPath));
			secret = ((SecretKey) ois.readObject());
			ois.close();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If file.exists()=false the static method {@link Crypt#generateKey(String)}
	 * creates a new {@link SecretKey} and a "key.dat" file
	 *
	 * @param sc SecretKey
	 */
	public Crypt(SecretKey sc) {
		secret = sc;
	}

	/**
	 * This method stores SecretKey as "key.dat" file. Key changes everytime it is
	 * generated so we just call it once and store that key file carefully.
	 *
	 * TODO Should add salt and secrets as parameters see 4 link.
	 *
	 * @param key
	 * @return The {@link SecretKey} object 'inyected' in {@link Crypt} constructor
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */

	public static SecretKey generateKey(String path, byte[] salt, char[] key) {

		String FILENAME = path;// "key.dat";
		String ALGORITHM = "PBKDF2WithHmacSHA1";
		SecretKey mySecret = null;

		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(ALGORITHM);
			KeySpec spec = new PBEKeySpec(key, salt, 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			mySecret = new SecretKeySpec(tmp.getEncoded(), "AES");

			ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream(FILENAME));
			ois.writeObject(mySecret);
			ois.close();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			// TODO test IOEx writting inside .jar
			e.printStackTrace();
		}

		return mySecret;
	}

	public String encode(String rawPass) {
		// whomever calls this method should do encode("..".getBytes("UTF-8"));
		// Not now beach

		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParams = createIV(cipher.getBlockSize());
			cipher.init(Cipher.ENCRYPT_MODE, secret, ivParams);
			return byteArraysAsString(cipher.doFinal(rawPass.getBytes(StandardCharsets.ISO_8859_1)), ivParams.getIV());

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			// seems like there are not correct answers, YES!!
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ok, again, this pretty little mf (aka decoder, aka "from String representing
	 * an hexadecimal number written")
	 *
	 * @param encryptedPass
	 * @return
	 */
	public byte[] decode(String encryptedPass) {

		byte[][] pair = stringToByteArray(encryptedPass);
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParams = new IvParameterSpec(pair[1]);
			cipher.init(Cipher.DECRYPT_MODE, secret, ivParams);
			return cipher.doFinal(pair[0]);

			// lel
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This parameters are random generated each time something is encoded.. and
	 * that means: - same pass generate different encoded byte[] - also both decode
	 * to the same pass if key remains equals and {@link IvParameterSpec#getIv()} is
	 * passed to {@link Cipher} every time it is initiated - somehow it is safe to
	 * store this info raw near to the encodedPass - To reuse this parameters is a
	 * bad thing. just don't
	 *
	 * @param ivSizeBytes
	 * @return
	 */
	private IvParameterSpec createIV(final int ivSizeBytes) {

		byte[] iv = new byte[ivSizeBytes];
		SecureRandom theRNG = new SecureRandom();
		theRNG.nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	/**
	 * This byte[] is the return from cipher.doFinal(), and we want persist it in
	 * firebase.. but new String(ciphertext,StandardCharsets.ISO_8859_1) generates
	 * some characters that have the need of been escaped.. so we convert them
	 * (byte[]) to Hexadecimal where there are no chars to escape.
	 *
	 * Left two comments inside for documentation purposes, first method shows how
	 * to convert byte[] to Long, but doesn't work bc {@link BadPaddingException} is
	 * raised. The second one shows how to convert to hexadecimal manually
	 *
	 * @param bytes result from encoding
	 * @return Hex representation from bytes[] which is the result from encoding.
	 */
	private String byteArraysAsString(byte[] bytes, byte[] iv) {

		/*-
		 * First try ByteBuffer wrapped = ByteBuffer.wrap(bytes);
		 * Long pass = wrapped.getLong(0);
		 */

		/*-
		 * Second try (works) "char[] keyHexChars = new char[bytes.length*2];"
		 * int i = -1;
		 * for(byte b: bytes) {
		 *
		 * keyHexChars[++i] = Character.forDigit((b >> 4) & 0xF, 16);
		 * keyHexChars[++i] = Character.forDigit((b & 0xF), 16);
		 *
		 * }
		 * char[] keyHexIv = new char[iv.length*2];
		 * i = -1;
		 * for(byte b: iv) {
		 *
		 * keyHexIv[++i] = Character.forDigit((b >> 4) & 0xF, 16); keyHexIv[++i] =
		 * Character.forDigit((b & 0xF), 16);
		 *
		 * }
		 * return new String(keyHexChars)+"."+new String(keyHexIv);
		 */
		return new String(DatatypeConverter.printHexBinary(bytes)) + "."
				+ new String(DatatypeConverter.printHexBinary(iv));

	}

	/**
	 * @param String encoded persisted in firebase, format:
	 *               (encodedPasswordAsString) (dot) (IvParameters) looks like:
	 *               "d4b98e02ff5985c3c37a236728007ed2.7135861063528b30f15ec45a404e0c90"
	 * @return byte[2][16*n] {n = 1, 2, ... 1024} (i think) "byte[0] = pass",
	 *         "byte[1] = IvParams".
	 */
	private byte[][] stringToByteArray(String string) {

		String[] pair = string.split("\\.");

		byte[][] toReturn = { DatatypeConverter.parseHexBinary(pair[0]), DatatypeConverter.parseHexBinary(pair[1]) };
		return toReturn;
	}
}
