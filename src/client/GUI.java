package client;

import javax.swing.*;
import java.awt.*;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 *  This class is responsible for creating the GUI for the client application.
 *  
 * @author Matthew Palmer & Peter Hajj & Jackson Higgins
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

    public GUI() throws UnknownHostException, InvalidLoginException, IOException, RegistrationException {
        setTitle("Git-Gabber");
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
     * @throws RegistrationException 
     * @throws IOException 
     * @throws InvalidLoginException 
     * @throws UnknownHostException 
     */
    public GUI(String username) throws UnknownHostException, InvalidLoginException, IOException, RegistrationException {
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

        loginBtn.addActionListener(e -> {
            clearExceptions();
            try {
                handleLogin();
            } catch (Exception error) {
                showLoginError(error.getMessage());
            }
        });
        registerBtn.addActionListener(e -> {            
            clearExceptions();            
            try {
                handleRegister();
            } catch (Exception error) {
                showLoginError(error.getMessage());
            }
        });
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
        JButton logoutBtn = new JButton("Logout");

        JPanel controls = new JPanel();
        controls.add(logoutBtn);
        controls.add(sendBtn);

        bottom.add(controls, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> handleSend());
        logoutBtn.addActionListener(e -> handleLogout());

        return panel;
    }

    /**
     * Sends the message in the message field using Client.send(), then clears the field.
     */
    private void handleSend() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            Client.send(text);
            messageField.setText("");
        }
    }

    /**
     * Handles logout: disconnects from the server and returns to the login screen.
     */
    private void handleLogout() {
        try {
            Client.disconnect();
        } catch (IOException ignored) {
            // ignore disconnect problems during logout
        }
        cardLayout.show(cardPanel, "login");
    }

    /**
     * Handles the login process when the login button is pressed.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            Client.connect(username, password, this);
            showChatScreen();
        } catch (InvalidLoginException | IOException e) {
            showLoginError(e.getMessage());
        }
    }

    /**
     * Handles the registration process when the register button is pressed.
     * @throws IOException 
     * @throws RegistrationException 
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            Client.register(username, password);
        } catch (RegistrationException | IOException e) {
            showLoginError(e.getMessage());
            e.printStackTrace();
        }
    }

    /** 
     * Shows the chat screen. 
     */ 
    private void showChatScreen() { 
        cardLayout.show(cardPanel, "chat");
        messageField.requestFocusInWindow(); 
    }

    /**
     * Shows a login error message.
     * @param message The error message to display
     */

    void showLoginError(String message) {
        loginStatusLabel.setForeground(new Color(160, 0, 0)); // dark red
        loginStatusLabel.setText(message);
    }

    /**
     * Shows a chat message in the chat area.
     * @param message The message to display
     */
    public void showChatMessage(Message message) {
        chatArea.append(message.getUserName() + ": " + message.getContent() + "\n");
    }

    public void clearExceptions() {
        loginStatusLabel.setText(" ");
    }
}
