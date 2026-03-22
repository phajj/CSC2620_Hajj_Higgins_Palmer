package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Utilities.MessageHelper;

/**
 * This class is a thread that is specifically for receiving messages from the connection and updating the GUI with the messages
 * 
 * @author Jackson Higgins
 */
public class Receiver extends Thread{
    BufferedReader receiver;
    Socket socket;
    String encryptedMessage;
    MessageHelper messageHelper;
    Encrypter encrypter;

    public Receiver(Socket socket, Encrypter encrypter) throws IOException {
        this.socket = socket;
        this.receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.messageHelper = new MessageHelper();
        this.encrypter = encrypter; 
    }

    @Override
    public void run() {
        try {
            while ((encryptedMessage = receiver.readLine()) != null) {
                String messageString = encrypter.decryptMessage(encryptedMessage); // Decrypt message to string
                Message message = messageHelper.toMessage(messageString); // Convert message string to a Message object
                //Update gui and log file. e.g. gui.putMessage(), log.logMessage()
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
