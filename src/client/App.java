package client;

import java.io.IOException;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

/**
 * This class is responsible for starting the client application.
 *
 * @author Peter Hajj
 */
public class App {

    public static void main(String[] args) {
            try { // Start the GUI
                new GUI();
            } catch (InvalidLoginException | IOException | RegistrationException e) {
                e.printStackTrace();
            }

    }
}