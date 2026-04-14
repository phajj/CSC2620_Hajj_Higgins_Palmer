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
public class Sender extends Thread {
  private MessageHelper messageHelper;
  private PrintWriter sender;
  private Encrypter encrypter;

  public Sender(Socket socket, Encrypter encrypter) throws IOException {
    this.encrypter = encrypter;
    messageHelper = new MessageHelper();
    sender = new PrintWriter(socket.getOutputStream(), true);
  }

  /**
   * Send a message to the broadcast server.
   * 
   * @param message message to be sent with username and timestamp metadata
   */
  public synchronized void send(Message message) {

    String messageString = messageHelper.fromMessage(message);
    String encryptedString = encrypter.encryptMessage(messageString);
    sender.println(encryptedString); // Send message to server
  }

  /**
   * Send a ping to the server to check connection
   */
  public void ping() {
    String ping = ":ping";
    String encryptedPing = encrypter.encryptMessage(ping);
    sender.println(encryptedPing);
  }

  /**
   * Notify the chat group that someone has joined the chat
   *
   * @param username user who joined the chat
   * @param group    group that was joined
   */
  public void enter(String username, String group) {
    String enter = ":enter," + username + "," + group;
    String encryptedEnter = encrypter.encryptMessage(enter);
    sender.println(encryptedEnter);
  }

  // Used for initial conneciton to default group only
  public void enter(String username) {
    String enter = ":enter," + username + ",default";
    String encryptedEnter = encrypter.encryptMessage(enter);
    sender.println(encryptedEnter);
  }

  /**
   * Notify that a user has left a chat
   *
   * @param group    group that the user has left
   * @param username user who has left
   */
  public void leave(String group, String username) {
    String leave = ":leave," + group + "," + username;
    sender.println(encrypter.encryptMessage(leave));
  }

  /**
   * Invite a user to a chat
   *
   * @param invitedUser user who has been invited to the chat
   * @param group       group the user was invited to
   * @param inviter     the person who invited the user to the group
   */
  public void invite(String invitedUser, String group, String inviter) {
    String invite = ":invite," + invitedUser + "," + group + "," + inviter;
    sender.println(encrypter.encryptMessage(invite));
  }

  public void createGroup(String name) {
    String createGroup = ":createGroup," + name;
    sender.println(encrypter.encryptMessage(createGroup));
  }
}
