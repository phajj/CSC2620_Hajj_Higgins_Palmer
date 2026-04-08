package backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles user connection and broadcasting to connected users.
 * 
 * @author Jackson Higgins
 */
public class Server {
  private static final HashMap<String, ArrayList<ClientHandler>> broadcastGroups = new HashMap<>(); // Each entry in
  private static final int PORT = 5001;
  private static final String DEFAULTGROUP = "default";

  /**
   * This method sends a message to all connected clients
   * 
   * @param message Message to be broadcast
   */
  public synchronized static void broadcast(String message, String group) {
    for (ClientHandler client : broadcastGroups.get(group)) {
      client.sendMessage(message);
    }
  }

  /**
   * Gracefully handles client disconnects.
   * 
   * @param client Client to be disconnected.
   */
  public static void disconnectClient(ClientHandler client) {
    for (String group : client.getGroups()) {
      broadcastGroups.get(group).remove(client); // Remove the client from all of its broadcast groups
      if (broadcastGroups.get(group).size() == 0) {
        broadcastGroups.remove(group); // Remove the group if it has no members
      }
    }
  }

  /**
   * Create and new broadcast group
   */
  public static void createGroup(String groupName) {
    ArrayList<ClientHandler> group = new ArrayList<>();
    broadcastGroups.put(groupName, group);
  }

  public static void main(String[] args) throws IOException {
    ArrayList<ClientHandler> defaultList = new ArrayList<>();
    broadcastGroups.put(DEFAULTGROUP, defaultList);
    ServerSocket serverSocket = new ServerSocket(PORT); // Start server
    System.out.println("Server starting on port " + PORT);

    // Loop accepts client connections and spins up a new thread for each as they
    // connect
    while (true) {
      Socket client = serverSocket.accept();
      System.out.println(client.getInetAddress() + " connected.");
      ClientHandler handler = new ClientHandler(client);
      handler.addGroup(DEFAULTGROUP);
      broadcastGroups.get(DEFAULTGROUP).add(handler); // Add newly connected client to default broadcast group
      handler.start(); // Creates new thread with the socket freeing this main thread to accept another
                       // connection.
    }
  }
}
