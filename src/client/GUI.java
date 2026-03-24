package client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
/**
 *  This class is responsible for creating the GUI for the client application.
 *  
 * @author Matthew Palmer & Peter Hajj
 */
public class GUI extends JFrame{
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    // Login UI components:
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JLabel loginStatusLabel = new JLabel(" ");
    // Chat UI components:
    private final JTextArea chatArea = new JTextArea(); 
    private final JTextField messageField = new JTextField();
    private final JLabel connectionStatusLabel = new JLabel("Offline");

    
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

        loginBtn.addActionListener(e -> cardLayout.show(cardPanel, "chat"));
        exitBtn.addActionListener(e -> cardLayout.show(cardPanel, "login"));

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
