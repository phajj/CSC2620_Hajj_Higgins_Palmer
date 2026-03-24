package client;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

/**
 * This class is responsible for starting the client application.
 *
 * @author Peter Hajj
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog(
                    null,
                    "Enter your username:",
                    "SecureChat Login",
                    JOptionPane.QUESTION_MESSAGE
            );

            // User pressed Cancel
            if (username == null) {
                return;
            }

            username = username.trim();

            // Validate username input
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Username cannot be empty.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (username.length() > 20) {
                JOptionPane.showMessageDialog(
                        null,
                        "Username cannot be longer than 20 characters.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Create client GUI and start client application
            try {
                new GUI(username);
            } catch (InvalidLoginException | IOException | RegistrationException e) {
                e.printStackTrace();
            }
        });
    }
}