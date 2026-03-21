package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import Utilities.MessageHelper;

/**
 * This class is a thread that handles sending messages to the server.
 * 
 * @author Jackson Higgins
 */
public class Sender extends Thread{
    private Socket socket;
    private MessageHelper messageHelper;
    private PrintWriter sender;

    public Sender(Socket socket) throws IOException {
        this.socket = socket;
        this.messageHelper = new MessageHelper();
        this.sender = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Send a message to the broadcast server.
     * 
     * @param message message to be sent with username and timestamp metadata
     */
    public synchronized void send(Message message) {
        String messageString = messageHelper.fromMessage(message);
        sender.println(messageString); // Send message to server
    }
}
