package com.comerzzia.pos.util.cryptography;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class CriptoUtils {

	public static final String ALGORITHM_MD5 = "MD5";
	public static final String ALGORITHM_AES = "AES";
	public static final String ALGORITHM_ECB = "ECB";
	public static final String ALGORITHM_PKCS5 = "PKCS5Padding";
	public static final String ALGORITHM_SHA = "SHA";
	public static final String ALGORITHM_SHA_256 = "SHA-256";

    private static Cipher cipher;
    private static SecretKey key; 
    private static AlgorithmParameterSpec paramSpec;

    // 8-byte Salt
    private static byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };
    
    // Iteration count
    private static int iterationCount = 19;

	
    /** 
     * Encrypts the content of a string with the specified algorithm.
     * @param algorithm One of the supported algorithms (see public constants defined in this class)
     * @param clearText Byte array with clear text
     * @return Encrypted text
     * @throws CriptoException If the specified algorithm is not found
     */
	public static String encrypt(String algorithm, byte[] clearText) throws CriptoException {
		String output = "";
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(clearText);
			byte[] code = md.digest();
			for (int i = 0; i < code.length; i++) {
				output += Integer.toHexString((code[i] >> 4) & 0xf);
				output += Integer.toHexString(code[i] & 0xf);
			}
		}
		catch (NoSuchAlgorithmException ex) {
			throw new CriptoException("The specified algorithm was not found: " + algorithm + " : " + ex.getMessage());
		}
		return output;
   }

    
	/** 
	 * Encrypts a string using the specified key which will allow to decrypt it in the future.
	 * @param clearText String that will be encrypted
	 * @return String Encrypted text
	 * @throws CriptoException
	 */
	public String encrypt(String clearText) throws CriptoException {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

			// Encode the string into bytes using utf-8
			byte[] utf8 = clearText.getBytes("UTF8");

			// Encrypt
			byte[] enc = cipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			return Base64.getEncoder().encodeToString(enc);
		}
		catch (Exception e) {
			throw new CriptoException("Error encrypting string using key:" + e.getMessage());
		}
	}
    
	/** 
	 * Decrypts a string using the specified key, which should have been used for encrypting.
	 * @param cipherText String that will be decrypted
	 * @return String Decrypted text
	 * @throws CriptoException
	 */
    public static String decrypt(String cipherText) throws CriptoException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            // Decode base64 to get bytes
            byte[] dec = Base64.getDecoder().decode(cipherText);
    
            // Decrypt
            byte[] utf8 = cipher.doFinal(dec);
    
            // Decode using utf-8
            return new String(utf8, "UTF8");
        }
        catch (Exception e) {
            throw new CriptoException("Error decrypting string using key:"  + e.getMessage());
        }
    }

	public void init(String passPhrase) throws CriptoException {
		try {
			// Create the key
			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
			key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
			cipher = Cipher.getInstance(key.getAlgorithm());

			paramSpec = new PBEParameterSpec(salt, iterationCount);
		}
		catch (Exception e) {
			throw new CriptoException("Error initializing cipher for encrypting / decrypting :" + e.getMessage());
		}
	}

}
