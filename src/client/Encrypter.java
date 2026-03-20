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
    public String encryptMessage(String message){
        try {
            // Generate AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            AESHelper aesHelper = new AESHelper();
            aesHelper.setKey(secretKey);
            
            // Read file content of message 
            BufferedReader fileReader = new BufferedReader(new FileReader(message));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            fileReader.close();

            // Encrypt Message file content and return it
            return aesHelper.encrypt(fileContent.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
     // TODO: decryptMessage
    public String decryptMessage(String encryptedMessage){
        try {
            
        } catch (Exception e) {
           
        }
        return new String(encryptedMessage);
    }
}
