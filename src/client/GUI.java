package client;

import javax.swing.*;
import java.awt.*;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 *  This class is responsible for creating the GUI for the client application.
 *  
 * @author Matthew Palmer & Peter Hajj
 */
public class GUI extends JFrame {
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    // Login UI components:
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JLabel loginStatusLabel = new JLabel(" ");
    // Chat UI components:
    private final JTextArea chatArea = new JTextArea(); 
    private final JTextField messageField = new JTextField();
    private final JLabel connectedUserLabel = new JLabel("Not connected");
    private final JLabel connectionStatusLabel = new JLabel("Offline");
    
    private String currentUser;

    public GUI() {
        setTitle("Git-Gabber”");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen

        cardPanel.add(buildLoginPanel(), "login");
        cardPanel.add(buildChatPanel(), "chat");

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "login");
        
        setVisible(true);
    }

    /**
     * Creates the GUI and prefills the username field.
     *
     * @param username the username to place in the login field
     */
    public GUI(String username) {
        this(); // call the main constructor first

        if (username != null && !username.isBlank()) {
            usernameField.setText(username);
        }
    }

    /**
     * Handles the exit process when the exit button is pressed.
     */
    private void handleExit() {
        try {
            Client.disconnect();
        } catch (IOException ignored) {
            // ignore disconnect problems during exit
        }
        dispose();
        System.exit(0);
    }

    /**
     * Builds the login panel with the necessary UI components.
     * @return JPanel containing the login form
     */
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Layout settings for the login form
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(loginStatusLabel, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton exitBtn = new JButton("Exit");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        buttonPanel.add(exitBtn);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());
        exitBtn.addActionListener(e -> handleExit());   

        gbc.gridy++;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    /**
     * Builds the chat panel UI components and layout.
     * @return JPanel containing the chat interface
     */
    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        chatArea.setEditable(false);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(messageField, BorderLayout.CENTER);

        JButton sendBtn = new JButton("Send");
        JButton clearBtn = new JButton("Clear");
        JButton logoutBtn = new JButton("Logout");

        JPanel controls = new JPanel();
        controls.add(connectionStatusLabel);
        controls.add(clearBtn);
        controls.add(logoutBtn);
        controls.add(sendBtn);

        bottom.add(controls, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Handles the login process when the login button is pressed.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter both username and password.");
            return;
        }

        try {
            loginStatusLabel.setText("Connecting...");
            Client.connect(username, password);

            currentUser = username;
            connectedUserLabel.setText("Logged in as: " + currentUser);
            connectionStatusLabel.setText("Connected");
            loginStatusLabel.setText("Login successful.");
            passwordField.setText("");
            showChatScreen();
        } catch (InvalidLoginException ex) {
            showLoginError("Invalid username or password.");
        } catch (UnknownHostException ex) {
            showLoginError("Cannot reach server.");
        } catch (IOException ex) {
            showLoginError("Connection error: " + ex.getMessage());
        }
    }

    /**
     * Handles the registration process when the register button is pressed.
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter both username and password.");
            return;
        }

        loginStatusLabel.setForeground(new Color(90, 90, 90));
        loginStatusLabel.setText("Registering...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Client.register(username, password);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    showLoginMessage("Registration successful. Please log in.");
                    passwordField.setText("");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showLoginError("Registration interrupted.");
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof RegistrationException) {
                        showLoginError("Registration failed: " + cause.getMessage());
                    } else if (cause instanceof IOException) {
                        showLoginError("Network error: " + cause.getMessage());
                    } else {
                        showLoginError("Registration failed.");
                    }
                }
            }
        }.execute();
    }

    /** 
     * Shows the chat screen. 
     */ 
    private void showChatScreen() { 
        cardLayout.show(cardPanel, "chat");
        messageField.requestFocusInWindow(); 
    }

    /**
     * Shows a login message.
     * @param message The message to display
     */
    private void showLoginMessage(String message) {
        loginStatusLabel.setForeground(new Color(0, 110, 0)); // dark green
        loginStatusLabel.setText(message);
    }

    /**
     * Shows a login error message.
     * @param message The error message to display
     */

    private void showLoginError(String message) {
        loginStatusLabel.setForeground(new Color(160, 0, 0)); // dark red
        loginStatusLabel.setText(message);
    }

    // For testing the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
