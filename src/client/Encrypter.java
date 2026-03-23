package client;

import java.io.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
/**
 *  This class is responsible for encrypting and decrypting messages.
 *  
 * @author Peter Hajj
 */

public class Encrypter {
    private KeyGenerator keyGen;
    private SecretKey secretKey;

    // Generate AES key
    public Encrypter() {
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            secretKey = keyGen.generateKey();
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
            
            // Read file content of message 
            // BufferedReader fileReader = new BufferedReader(new FileReader(message));
            // StringBuilder fileContent = new StringBuilder();
            // String line;
            // while ((line = fileReader.readLine()) != null) {
            //     fileContent.append(line).append("\n");
            // }
            // fileReader.close();

            // // Encrypt Message file content and return it
            // return aesHelper.encrypt(fileContent.toString());

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
