package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

/**
 * This is the main client. Handles user login and connection to the server. Methods in the class should be called from the GUI when buttons in the GUI are pressed.
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
    private static String user;
    private static Encrypter encrypter;


    /**
     * Establishes if the user has entered a valid set of credentials
     * 
     * @param username Username for login
     * @param password Password for login
     * @return True if the username exists and the password is the correct password for the username
     * @throws InvalidLoginException If invalid credentialas
     */
    static boolean login(String username, String password) throws InvalidLoginException {
        return loginHandler.login(username, password);
    }

    /**
     * Register a new user for the application
     * 
     * @param username Username to be registered
     * @param password Password to be registered
     * @throws RegistrationException If username already exists
     * @throws IOException File read/write errors
     */
    static void register(String username, String password) throws RegistrationException, IOException {
        loginHandler.register(username, password);
    }

    /**
     * This method should be called when the login button is pressed in the GUI. It will validate user credentials and form a connection to the server as well as create new threads for sending and receiving messages

     * @param username Username for login
     * @param password Password for login
     * @throws InvalidLoginException If invalid credentials
     * @throws UnknownHostException Incorrect server address
     * @throws IOException 
     */
    static void connect(String username, String password) throws InvalidLoginException, UnknownHostException, IOException {
        if (login(username, password)) {
            user = username;
            socket = new Socket(serverIP, serverPort);
            sender = new Sender(socket, encrypter);
            receiver = new Receiver(socket, encrypter);
            encrypter = new Encrypter();
            sender.start();
            receiver.start();
        }
    }

    /**
     * Send a message to the broadcast server
     * 
     * @param messageString Message to be sent
     */
    static void send(String messageString) {
        Message message = new Message(messageString, user);
        sender.send(message);
    }

    /**
     * Closes connection between client and server.
     * 
     * @throws IOException
     */
    static void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}
