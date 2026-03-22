package backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import client.Message;

/**
 * Handles user connection and broadcasting to connected users.
 * 
 * @author Jackson Higgins
 */
public class Server {
    private static final ArrayList<ClientHandler> connectedClients = new ArrayList<>();
    private static final int PORT = 5001;
    
    /**
     * This method sends a message to all connected clients
     * 
     * @param message Message to be broadcast
     */
    public synchronized static void broadcast(Message message) {
        for (ClientHandler client : connectedClients) {
            client.sendMessage(message);
        }
    }

    /**
     * Gracefully handles client disconnects.
     * 
     * @param client Client to be disconnected.
     */
    public static void disconnectClient(ClientHandler client) {
        connectedClients.remove(client);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT); // Start server
        System.out.println("Server starting on port " + PORT);

        // Loop accepts client connections and spins up a new thread for each as they connect
        while (true) {
            Socket client = serverSocket.accept();
            connectedClients.add(new ClientHandler(client));
            System.out.println(client.getInetAddress() + " connected.");
            ClientHandler handler = new ClientHandler(client);
            handler.start(); // Creates new thread with the socket freeing this main thread to accept another connection.
        }
    }
}
