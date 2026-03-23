package client;

import javax.swing.JFrame;
import java.awt.*;
/**
 *  This class is responsible for creating the GUI for the client application.
 *  
 * @author Matthew Palmer & Peter Hajj
 */
public class GUI extends JFrame{
    private String username;
    private Message message;

    // Constructor for user login GUI
    public GUI(String username) throws InterruptedException, java.util.concurrent.ExecutionException {
        this.username = username;
        
        // Set up GUI components
        setTitle("Secure Chat - " + username);
        setVisible(true);

        // Set window properties
        setSize(600,800);
        setLocationRelativeTo(null); // Centers the window on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screens the window
        setResizable(true);

        // Username Label & TextField
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(new Label("Username:"), gbc);

        gbc.gridx = 1;
        add(new TextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new Label("Password:"), gbc);

        gbc.gridx = 1;
        TextField pass = new TextField(15);
        pass.setEchoChar('*');
        add(pass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new Button("Login"), gbc);

        gbc.gridx = 1;
        add(new Button("Exit"), gbc);
    }
    public GUI(String username, Message message) throws InterruptedException, java.util.concurrent.ExecutionException {
        this.username = username;
        this.message = message;
    }

      public static void main(String[] args) {
        try {
            new GUI("defaultUser");
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            e.printStackTrace();
        }
    }
}
