package client;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/**
 *  This class is responsible for encrypting and decrypting messages.
 *  
 * @author Peter Hajj
 */

public class Encrypter {
    private SecretKey secretKey;
    private String keyString;

    // Generate AES key
    public Encrypter() {
        try {
            this.keyString = "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU="; // Never do this in prod. Hard coded 256 bit string for key generation
            byte[] keyBytes = Base64.getDecoder().decode(keyString);
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a message using the AES algorithm.
     *
     * @param message The message to encrypt.
     * @return The encrypted message.
     */
    public String encryptMessage(String message){
        try {
            AESHelper aesHelper = new AESHelper();
            aesHelper.setKey(secretKey);

            return aesHelper.encrypt(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Decrypts a message using the AES algorithm.
     *
     * @param encryptedMessage The message to decrypt.
     * @return The decrypted message.
     */
    public String decryptMessage(String encryptedMessage){
        try {
            AESHelper aesHelper = new AESHelper();
            aesHelper.setKey(secretKey);
            return aesHelper.decrypt(encryptedMessage);
        } catch (Exception e) {
            return "Error decrypting message: " + e.getMessage();
        }
    }
}
