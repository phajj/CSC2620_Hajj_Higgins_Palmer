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
    String messageString;
    MessageHelper messageHelper;

    public Receiver(Socket socket) throws IOException {
        this.socket = socket;
        this.receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.messageHelper = new MessageHelper();
    }

    @Override
    public void run() {
        try {
            while ((messageString = receiver.readLine()) != null) {
                Message message = messageHelper.toMessage(messageString);
                //Update gui and log file. e.g. gui.putMessage(), log.logMessage()
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
