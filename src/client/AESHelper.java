package client;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;
/**
 * This class is responsible for encrypting and decrypting data using AES encryption.
 * Forked from Canvas\Module 7\Secure File transfer\AESHelper.java
 * @auther Peter Hajj
 */

public class AESHelper {
    private SecretKey secretKey;

    public void setKey(SecretKey key) {
        this.secretKey = key;
    }

    /**
     * Encrypts data using the AES algorithm.
     * @param data The data to encrypt.
     * @return The encrypted data as a Base64 encoded string.
     */
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts data using the AES algorithm.
     * @param encryptedData The data to decrypt.
     * @return The decrypted data.
     */
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
