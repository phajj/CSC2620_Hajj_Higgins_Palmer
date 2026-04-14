package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

/**
 * This is the main client. Handles user login and connection to the server.
 * Methods in the class should be called from the GUI when buttons in the GUI
 * are pressed.
 * 
 * @author Jackson Higgins
 */
public class Client {
  private final static String serverIP = null; // Needs to be set for use
  private final static int serverPort = 5001;
  private static Sender sender;
  private static Receiver receiver;
  private static Socket socket;
  private static LoginHandler loginHandler;
  private static ConnectionChecker connectionChecker;
  private static String user;
  private static Encrypter encrypter;

  /**
   * Establishes if the user has entered a valid set of credentials
   * 
   * @param username Username for login
   * @param password Password for login
   * @return True if the username exists and the password is the correct password
   *         for the username
   * @throws InvalidLoginException If invalid credentialas
   * @throws IOException
   */
  private static boolean login(String username, String password) throws InvalidLoginException, IOException {
    loginHandler = LoginHandler.getInstance();
    return loginHandler.login(username, password);
  }

  /**
   * Register a new user for the application
   * 
   * @param username Username to be registered
   * @param password Password to be registered
   * @throws RegistrationException If username and/or password already exists
   * @throws IOException           File read/write errors
   */
  public static void register(String username, String password) throws RegistrationException, IOException {
    loginHandler = LoginHandler.getInstance();
    loginHandler.register(username, password);
  }

  /**
   * This method should be called when the login button is pressed in the GUI. It
   * will validate user credentials and form a connection to the server as well as
   * create new threads for sending and receiving messages
   * 
   * @param username Username for login
   * @param password Password for login
   * @throws InvalidLoginException If invalid credentials
   * @throws UnknownHostException  Incorrect server address
   * @throws IOException
   */
  public static void connect(String username, String password, GUI gui)
      throws InvalidLoginException, UnknownHostException, IOException {
    if (login(username, password)) {
      if (connectionChecker != null) {
        connectionChecker.shutdown();
      }
      user = username;
      encrypter = new Encrypter();
      socket = new Socket(serverIP, serverPort);
      sender = new Sender(socket, encrypter);
      receiver = new Receiver(socket, encrypter, gui);
      sender.start();
      receiver.start();
      sender.enter(username);
      connectionChecker = new ConnectionChecker(sender, gui);
      connectionChecker.start();
    }
  }

  /**
   * Send a message to the broadcast server
   *
   * @param messageString Message to be sent
   */
  public static void send(String messageString, String group) {
    Message message = new Message(messageString, user, group);
    sender.send(message);
  }

  /**
   * Send a pre-built Message to the server. Use this when the message
   * already has attachments added to it.
   *
   * @param message the Message to send
   */
  public static void send(Message message) {
    sender.send(message);
  }

  /**
   * Notifies the server that this client is leaving a group.
   *
   * @param group the group to leave
   */
  public static void leaveGroup(String group) {
    sender.leave(group, user);
  }

  /**
   * Sends an invite for another user to join a group.
   *
   * @param invitedUser the username to invite
   * @param group       the group to invite them to
   */
  public static void sendInvite(String invitedUser, String group) {
    sender.invite(invitedUser, group, user);
  }

  /**
   * Notifies the server that this client has entered a group.
   * The server will broadcast the join notification and reply with the current member list.
   *
   * @param group the group to enter
   */
  public static void enterGroup(String group) {
    sender.enter(user, group);
  }

  /**
   * Closes connection between client and server.
   * 
   * @throws IOException
   */
  public static void disconnect() throws IOException {
    if (connectionChecker != null) {
      connectionChecker.shutdown();
    }
    if (socket != null) {
      socket.close();
    }
  }

  public static void createGroup(String name) {
    sender.createGroup(name);
  }
}
