package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Utilities.MessageHelper;

/**
 * This class is a thread that handles exactly one socket and its operations
 * 
 * @author Jackson Higgins
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private MessageHelper messageHelper;

    public ClientHandler(Socket client) throws IOException {
        this.socket = client;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()) );
        this.messageHelper = new MessageHelper();
    }

    @Override
    public void run() {
        try {
            String messageString;
            while ((messageString = reader.readLine()) != null) {
                Message message = messageHelper.toMessage(messageString);
                sendMessage(message);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will send a message over the connection back to the client
     * 
     * @param message Message to be transmitted
     */
    public void sendMessage(Message message) {
        Server.broadcast(message);
    }
}
