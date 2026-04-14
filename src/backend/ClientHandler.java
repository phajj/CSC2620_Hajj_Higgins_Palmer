package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.Encrypter;
import Utilities.MessageHelper;

/**
 * This class is a thread that handles exactly one socket and its operations
 * 
 * @author Jackson Higgins
 */
public class ClientHandler extends Thread {
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter sender;
  private Encrypter encrypter;
  private MessageHelper messageHelper;
  private ArrayList<String> groups;
  private String username = "";

  public ClientHandler(Socket client) throws IOException {
    this.socket = client;
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.sender = new PrintWriter(socket.getOutputStream(), true);
    this.encrypter = new Encrypter();
    this.messageHelper = new MessageHelper();
    this.groups = new ArrayList<>();
  }

  public ArrayList<String> getGroups() {
    return this.groups;
  }

  public String getUsername() {
    return this.username;
  }

  public void addGroup(String group) {
    this.groups.add(group);
  }

  public void removeGroup(String group) {
    this.groups.remove(group);
  }

  @Override
  public void run() {
    try {
      String messageString;
      // Get incoming messages from socket
      while ((messageString = reader.readLine()) != null) {
        String decryptedMessage = encrypter.decryptMessage(messageString);
        if (decryptedMessage.equals(":ping")) {
          sender.println("true"); // Alerts server is still online
        } else if (decryptedMessage.startsWith(":leave,")) {
          String[] parts = decryptedMessage.split(",");
          String group = parts[1];
          String user = parts[2];
          String content = ":leave," + user + " has left the chat.," + group;
          String encryptedContent = encrypter.encryptMessage(content);
          Server.broadcast(encryptedContent, group);
          Server.leaveGroup(group, this);
          removeGroup(group);
        } else if (decryptedMessage.startsWith(":invite,")) {
          Server.broadcast(messageString, "default");
        } else if (decryptedMessage.startsWith(":enter,")) {
          String[] parts = decryptedMessage.split(",");
          String enteredUsername = parts[1];
          String group = parts[2];
          this.username = enteredUsername;
          Server.ensureGroupAndAddClient(group, this);
          String message = ":enter," + enteredUsername + " has entered the chat.," + group;
          String encryptedMessage = encrypter.encryptMessage(message);
          Server.broadcast(encryptedMessage, group);
          // Send the current member list only to this client so they can populate their user panel
          List<String> members = Server.getGroupUsernames(group);
          String membersMsg = ":members," + group + "," + String.join(",", members);
          sender.println(encrypter.encryptMessage(membersMsg));
        } else {
          String group = messageHelper.toMessage(encrypter.decryptMessage(messageString)).getGroup();
          System.out.println("Received message from " + socket.getRemoteSocketAddress());
          Server.ensureGroupAndAddClient(group, this);
          Server.broadcast(messageString, group);
        }

      }
    } catch (IOException e) {
      System.out.println("Connection lost.");
    } finally {
      // Broadcast a leave notification for each group the client is still in
      if (!username.isEmpty()) {
        for (String group : new ArrayList<>(groups)) {
          String content = ":leave," + username + " has left the chat.," + group;
          Server.broadcast(encrypter.encryptMessage(content), group);
        }
      }
      Server.disconnectClient(this); // Remove this client from list of clients to be broatcast to
      try {
        socket.close(); // Close socket
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * This method will send a message over the connection back to the client
   * 
   * @param message Message to be transmitted
   */
  public void sendMessage(String messageString) {
    sender.println(messageString); // Send message to client
  }
}
