package client;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;
/*
 * This class is responsible for encrypting and decrypting data using AES encryption.
 * Forked from Canvas\Module 7\Secure File transfer\AESHelper.java
 */

public class AESHelper {
    private SecretKey secretKey;

    // Set key received from client
    public void setKey(SecretKey key) {
        this.secretKey = key;
    }

    // Encrypt data using current key
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt data using current key
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
