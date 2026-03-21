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
    private PrintWriter sender;

    public ClientHandler(Socket client) throws IOException {
        this.socket = client;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()) );
        this.messageHelper = new MessageHelper();
        this.sender = new PrintWriter(socket.getOutputStream(), true);
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
            System.out.println("Connection lost.");
        } finally {
            Server.disconnectClient(this); // Remove this client from list of clients to be broatcast to
            socket.close(); // close socket
        }
    }

    /**
     * This method will send a message over the connection back to the client
     * 
     * @param message Message to be transmitted
     */
    public void sendMessage(Message message) {
        String messageString = messageHelper.fromMessage(message);
        sender.println(messageString); // Send message to client
    }
}
