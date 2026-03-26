package client;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final List<Message> messages = new ArrayList<>();

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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add logo
        ImageIcon icon = new ImageIcon("resources/logo.png");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.add(logoLabel);
        panel.add(logoPanel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Layout settings for the login form
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel welcomeLabel = new JLabel("Welcome to Git Gabber! Please sign in to continue:", SwingConstants.CENTER);
        gbc.gridwidth = 2;
        formPanel.add(welcomeLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(loginStatusLabel, gbc);

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
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel);

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
        JButton saveHistoryBtn = new JButton("Save History");

        JPanel controls = new JPanel();
        controls.add(logoutBtn);
        controls.add(sendBtn);
        controls.add(saveHistoryBtn);

        bottom.add(controls, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> handleSend());
        logoutBtn.addActionListener(e -> handleLogout());
        saveHistoryBtn.addActionListener(e -> handleSaveHistory());

        messageField.addActionListener(e -> handleSend());

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
     * Handles saving the chat history to a file and writes it to a txt file.
     */
    private void handleSaveHistory() {
        JFileChooser fileChooser = new JFileChooser();

        // Generate a default filename with the current date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy HH-mm");
        String timestamp = LocalDateTime.now().format(formatter);
        fileChooser.setSelectedFile(new File("Git Gabber Chat History " + timestamp + ".txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                for (Message message : messages) { // Write message in format [timestamp] username: message
                    writer.write("[" + message.getTimeStamp() + "] " 
                        + message.getUserName() + ": " 
                        + message.getContent() + "\n");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            showLoginSuccess("Account registered successfully");
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
     * Shows a login success message.
     * @param message The success message to display
     */
    void showLoginSuccess(String message) {
        loginStatusLabel.setForeground(new Color(0, 128, 0)); // green
        loginStatusLabel.setText(message);
    }

    /**
     * Shows a chat message in the chat area.
     * @param message The message to display
     */
    public void showChatMessage(Message message) {
        messages.add(message);
        chatArea.append(message.getUserName() + ": " + message.getContent() + "\n");
    }

    public void clearExceptions() {
        loginStatusLabel.setText(" ");
    }
}
