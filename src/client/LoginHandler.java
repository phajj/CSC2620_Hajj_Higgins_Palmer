package client;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles logic for logging in to the client as well as registering new users
 * 
 * @author Jackson Higgins
 */
public class LoginHandler {
    private final Pattern usernamePatern; // Pattern used for matching email strings
    private final Pattern passwordPattern; // Patter used for matching password strings
    private final String credentialsFileName = "credentials.txt";
    private static CredentialHandler credentialHandler;
    private static LoginHandler instance;

    private LoginHandler() throws IOException {
        credentialHandler = CredentialHandler.getInstance(credentialsFileName);
        usernamePatern = Pattern.compile("^[\\w\\-]{1,20}$"); // 1 to 20 word characters including "-"
        passwordPattern = Pattern.compile("^[\\w][\\x20-\\x2B\\x2D-\\x7E]{7,24}$"); //Starts with word character and ends with 7 to 24 ASCII characters excluding commas.
                                                                                           // Commas are used for seperating the username and password in the credentials.txt file
    }

    /**
     * This method ensures only one LoginHandler exists at a time
     * 
     * @return Instance of LoginHandler
     * @throws IOException If scanner error
     */
    public static LoginHandler getInstance() throws IOException {
        if (instance == null) {
            instance = new LoginHandler();
            return instance;
        }
        return instance;
    }

    /**
     * Validates username using Regex
     * 
     * @param username username to be checked
     * @return true if the username passes the regex check, false otherwise
     */
    boolean validateUsernameFormat(String username) {
        Matcher usernameMatcher = usernamePatern.matcher(username);

        return usernameMatcher.matches();
    }

    /**
     * Validates password using regex
     * 
     * @param password password to be checked
     * @return true if the password passes the regex check, false otherwise
     */
    boolean validatePasswordFormat(String password) {
        Matcher passwordMatcher = passwordPattern.matcher(password);

        return passwordMatcher.matches();
    }

    /**
     * Checks that the username and password are of a valid form and then checks them against stored credentials
     * 
     * @param username Username for login
     * @param password Password for login
     * @return True if the username and password are of a valid form and the password is the password for the username
     * @throws FileNotFoundException 
     */
    boolean login(String username, String password) throws InvalidLoginException, FileNotFoundException{
        
        if (!validateUsernameFormat(username)) {
            throw new InvalidLoginException("Invalid username format");
        }

        if (!validatePasswordFormat(password)) {
            throw new InvalidLoginException("Invalid password format");
        }

        if (!credentialHandler.hasCredentials()) {
            throw new InvalidLoginException("Please create a user before attempting to login");
        }

        String userPass = credentialHandler.lookup(username);
        
        if (!userPass.equals(password)) {
            throw new InvalidLoginException("Username or password incorrect");
        }

        return true;
    }

    /**
     * Register a new user and store there credentials in the credentials file
     * 
     * @param username Username for registering
     * @param password Password for username
     * @throws RegistrationException If username already exists
     * @throws IOException For file writing errors
     */
    void register(String username, String password) throws RegistrationException, IOException {
        credentialHandler.register(username, password);
    }
}