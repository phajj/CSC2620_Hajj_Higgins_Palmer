package backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import Utilities.GroupManager;

/**
 * Handles user connection and broadcasting to connected users.
 * 
 * @author Jackson Higgins
 */
public class Server {
  private static final HashMap<String, ArrayList<ClientHandler>> broadcastGroups = new HashMap<>();
  private static final GroupManager groupManager = new GroupManager();
  private static final int PORT = 5001;
  private static final String DEFAULTGROUP = "default";

  /**
   * This method sends a message to all connected clients
   * 
   * @param message Message to be broadcast
   */
  public synchronized static void broadcast(String message, String group) {
    ArrayList<ClientHandler> clients = broadcastGroups.get(group);
    if (clients == null) {
      System.out.println("Broadcast failed: group '" + group + "' does not exist.");
      return;
    }
    for (ClientHandler client : clients) {
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
   * Create a new broadcast group
   */
  /**
   * Removes a client from a broadcast group. Deletes the group if it becomes empty.
   *
   * @param group  the group to leave
   * @param client the client handler leaving the group
   */
  public synchronized static void leaveGroup(String group, ClientHandler client) {
    ArrayList<ClientHandler> clients = broadcastGroups.get(group);
    if (clients != null) {
      clients.remove(client);
      if (clients.isEmpty()) {
        broadcastGroups.remove(group);
      }
    }
  }

  public static void createGroup(String groupName) {
    groupManager.ensureGroup(groupName);
    broadcastGroups.put(groupName, new ArrayList<>());
  }

  /**
   * Ensures a group exists (via GroupManager) and adds the client to it if not
   * already a member. Creates the group on demand when the first message arrives.
   *
   * @param group  the group name
   * @param client the client handler to add
   */
  public synchronized static void ensureGroupAndAddClient(String group, ClientHandler client) {
    groupManager.ensureGroup(group);
    if (!broadcastGroups.containsKey(group)) {
      broadcastGroups.put(group, new ArrayList<>());
    }
    ArrayList<ClientHandler> clients = broadcastGroups.get(group);
    if (!clients.contains(client)) {
      clients.add(client);
      client.addGroup(group);
    }
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
