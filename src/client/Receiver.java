package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Utilities.MessageHelper;

/**
 * This class is a thread that is specifically for receiving messages from the
 * connection and updating the GUI with the messages
 * 
 * @author Jackson Higgins
 */
public class Receiver extends Thread {
  BufferedReader receiver;
  Socket socket;
  String encryptedMessage;
  MessageHelper messageHelper;
  Encrypter encrypter;
  GUI gui;

  public Receiver(Socket socket, Encrypter encrypter, GUI gui) throws IOException {
    this.socket = socket;
    this.receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.messageHelper = new MessageHelper();
    this.encrypter = encrypter;
    this.gui = gui;
  }

  @Override
  public void run() {
    try {
      while ((encryptedMessage = receiver.readLine()) != null) {
        if (encryptedMessage.equals("true")) { // If we received a response to our ping
          ConnectionChecker.setConnectionStatus(true);
          continue;
        }

        String messageString = encrypter.decryptMessage(encryptedMessage); // Decrypt message to string

        if (messageString.startsWith(":enter,")) {
          String[] parts = messageString.split(",");
          String content = parts[1];
          String group = parts[2];
          Message message = new Message(content, "Server Alert", group);
          gui.showChatMessage(message);
          String username = content.split(" ")[0];
          gui.addUserToChat(group, username);
          continue;
        }

        if (messageString.startsWith(":members,")) {
          String[] parts = messageString.split(",");
          String group = parts[1];
          List<String> members = new ArrayList<>();
          for (int i = 2; i < parts.length; i++) {
            members.add(parts[i]);
          }
          gui.setGroupMembers(group, members);
          continue;
        }

        if (messageString.startsWith(":invite,")) {
          String[] parts = messageString.split(",");
          String invitedUser = parts[1];

          if (invitedUser.equals(gui.getUsername())) {
            String group = parts[2];
            String inviter = parts[3];
            gui.receiveInvite(group, inviter);
          }
          continue;
        }

        if (messageString.startsWith(":leave,")) {
          String[] parts = messageString.split(",");
          String content = parts[1];
          String group = parts[2];
          String user = content.split(" ")[0];
          Message message = new Message(content, "Server Alert", group);
          gui.removeUserFromChat(group, user);
          gui.showChatMessage(message);
          continue;
        }

        Message message = messageHelper.toMessage(messageString); // Convert message string to a Message object
        System.out.println("Received message from : " + socket.getInetAddress());
        System.out.println("Message: " + messageString);
        // Update gui and log file. e.g. gui.putMessage(), log.logMessage()

        gui.showChatMessage(message); // Show the message in the chat area of the GUI

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
