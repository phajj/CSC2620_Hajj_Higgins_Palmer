package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

import Utilities.RegistrationException;

/**
 * This class handles reading and writing to the file that holds local user credentilas
 * 
 * @author Jackson Higgins
 */
public class CredentialHandler {
    private final File credentialsFile;
    private final BufferedWriter writer;
    private static CredentialHandler instance;
    private HashMap<String, String> creds; 

    private CredentialHandler(String filename) throws IOException{

        Path credentialsPath = Path.of("./" + filename);

        if (!Files.exists(credentialsPath)) {
            Files.createFile(credentialsPath);
        }

        this.credentialsFile = new File(filename);
        writer = new BufferedWriter(new FileWriter(credentialsFile.getName(), true));
        creds = new HashMap<>();  
    }

    /**
     * This method makes sure there is only one CredentialHandler at a time
     * 
     * @param filename If there is not instance yet, a new credentials file will be created with this name.
     * @return Instance of CredentialHandler
     * @throws IOException For scanner errors
     */
    public static CredentialHandler getInstance(String filename) throws IOException {
        if (instance == null) {
            instance = new CredentialHandler(filename);
            return instance;
        }
        return instance;
    }

    /**
     * loads credentials into creds hashmap
     * @throws FileNotFoundException 
     */
    private void loadCredentials() throws FileNotFoundException {
        creds.clear();
        try (Scanner reader = new Scanner(credentialsFile)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] credentials = line.split(","); // lines are formatted username,password
                creds.put(credentials[0], credentials[1]);
            }
        }
    }

    /**
     * This method checks if there are user credentials that can be checked.
     
     * @return True if there are user credentials, false otherwise.
     * @throws FileNotFoundException 
     */
    public boolean hasCredentials() throws FileNotFoundException {
        loadCredentials();
        return !creds.isEmpty();
    }

    /**
     * Registers a new set of user credentials in the credentials file
     * 
     * @param username Username for user
     * @param password Password for user
     * @throws RegistrationException If username already registered
     * @throws IOException For file writing errors
     */
    void register(String username, String password) throws RegistrationException, IOException{
        loadCredentials();
        if (hasCredentials() && creds.containsKey(username)) {
            throw new RegistrationException("Username " + username + " already exists");
        }

        writer.write(username + "," + password);
        writer.newLine();
        writer.flush();
    }

    /**
     * Lookup the password for a username in the credentials file
     * 
     * @param username Username belonging to the password you want to lookup
     * @return password that belongs to the username
     * @throws FileNotFoundException 
     */
    String lookup(String username) throws FileNotFoundException {
        loadCredentials();
        return creds.get(username); 
    }
}
