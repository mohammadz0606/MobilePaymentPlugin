/**
 *
 */
package com.edesign.paymentsdk.Sdk.Security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 */
public class SecurityUtil {

    private Cipher aesCipher;
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;

    private static String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static String CIPHER_ALGORITHM = "AES";

    /**
     *
     */
    private SecurityUtil(String merchantSecretKey) {
        byte[] encryptedSecretKey = generateEZConnectSHA2Hash(merchantSecretKey);
        String encodedSecretKey = new String(Base64.encodeBase64(encryptedSecretKey));

        try {
            aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace(System.out);
        }

        byte[] initializationVector = Arrays.copyOfRange(encryptedSecretKey, 0, 16);
        byte[] passwordKey128 = Arrays.copyOfRange(encodedSecretKey.getBytes(), 0, 16);

        secretKey = new SecretKeySpec(passwordKey128, CIPHER_ALGORITHM);
        ivParameterSpec = new IvParameterSpec(initializationVector);
    }

    /**
     * @param message
     * @return
     */
    private byte[] generateEZConnectSHA2Hash(String message) {
        byte[] hash = DigestUtils.sha256(message);
        return hash;
    }

    /**
     * @param data
     * @return
     */
    private byte[] convertFromBase64(String data) {
        return Base64.decodeBase64(data.getBytes());
    }

    /**
     * @param data
     * @return
     */
    private byte[] decrypt(byte[] data) {
        byte[] encryptedData = null;

        try {
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            encryptedData = aesCipher.doFinal(data);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return encryptedData;
    }

    /**
     * @param secrtKey
     * @param data
     * @return
     */
    public static String encrypt(String secrtKey, String data) {
        try {
            SecurityUtil c = new SecurityUtil(secrtKey);

            byte[] encBytes = c.encrypt(data);
            String encString = c.convertToBase64(encBytes);

            return encString;
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return null;
    }

    public static String decrypt(String secrtKey, String data) {
        try {
            SecurityUtil c = new SecurityUtil(secrtKey);

            return new String(c.decrypt(c.convertFromBase64(data)));

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return null;
    }

    /**
     * @param clearData
     * @return
     */
    private byte[] encrypt(String clearData) {
        byte[] encryptedData = null;

        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            encryptedData = aesCipher.doFinal(clearData.getBytes("UTF-8"));

        } catch (InvalidKeyException e) {
            e.printStackTrace(System.out);
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace(System.out);
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace(System.out);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(System.out);
        }

        return encryptedData;
    }

    /**
     * @param clearData
     * @return
     * @throws UnsupportedEncodingException
     */
    private String convertToBase64(byte[] clearData) throws UnsupportedEncodingException {
        return new String(Base64.encodeBase64(clearData));
    }
}
