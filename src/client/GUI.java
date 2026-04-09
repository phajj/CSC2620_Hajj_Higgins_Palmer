package client;

import Utilities.ChatButtonFactory;
import Utilities.GroupManager;
import Utilities.InvalidLoginException;
import Utilities.LoginButtonFactory;
import Utilities.RegistrationException;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.UnknownHostException;

/**
 * This class is responsible for creating the GUI for the client application.
 * 
 * @author Matthew Palmer & Peter Hajj & Jackson Higgins
 */
public class GUI extends JFrame {

  private final LoginButtonFactory loginBtnFactory = new LoginButtonFactory();
  private final ChatButtonFactory chatBtnFactory = new ChatButtonFactory();

  private final CardLayout cardLayout = new CardLayout();
  private final JPanel cardPanel = new JPanel(cardLayout);
  // Login UI components:
  private final JTextField usernameField = new JTextField(15);
  private final JPasswordField passwordField = new JPasswordField(15);
  private final JLabel loginStatusLabel = new JLabel(" ");
  // Chat UI components:
  private final JTextArea chatArea = new JTextArea();
  private final JTextField messageField = new JTextField();
  private final JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
  private final GroupManager groupManager = new GroupManager();
  // Sidebar components (populate these when backend is ready):
  private final JPanel chatListPanel = new JPanel(); // holds chat buttons
  private final DefaultListModel<String> userListModel = new DefaultListModel<>();
  private String currentChat = "default";
  // Connection indicator:
  private final JLabel connectionIndicator = new JLabel("\u25CF Disconnected");

  private String clientUser;

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
   * 
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
    gbc.insets = new Insets(5, 5, 25, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    JLabel welcomeLabel = new JLabel("Welcome to Git Gabber! Please sign in to continue:", SwingConstants.CENTER);
    gbc.gridwidth = 2;
    formPanel.add(welcomeLabel, gbc);

    // Reset insets for the rest of the components
    gbc.insets = new Insets(5, 5, 5, 5);

    // Add username and password fields with labels
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

    JButton loginBtn = loginBtnFactory.getButton("Login");
    JButton registerBtn = loginBtnFactory.getButton("Register");
    JButton exitBtn = loginBtnFactory.getButton("Exit");

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
   * 
   * @return JPanel containing the chat interface
   */
  private JPanel buildChatPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JButton addEmoji = new JButton("");
    JButton addAttachment = new JButton("");

    // Add icons to emoji and attachment buttons
    ImageIcon emojiIcon = new ImageIcon(
        new ImageIcon("resources/happy-face.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    addEmoji.setIcon(emojiIcon);
    addEmoji.setBackground(Color.WHITE);
    addEmoji.setBorder(BorderFactory.createEmptyBorder());
    addEmoji.setContentAreaFilled(false);
    addEmoji.setOpaque(true);
    addEmoji.setRolloverEnabled(false);

    ImageIcon attachmentIcon = new ImageIcon(
        new ImageIcon("resources/attach-file.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    addAttachment.setIcon(attachmentIcon);
    addAttachment.setBackground(Color.WHITE);
    addAttachment.setBorder(BorderFactory.createEmptyBorder());
    addAttachment.setContentAreaFilled(false);
    addAttachment.setOpaque(true);
    addAttachment.setRolloverEnabled(false);

    chatArea.setEditable(false);
    panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

    // Left sidebar — scrollable chat list with buttons
    chatListPanel.setLayout(new BoxLayout(chatListPanel, BoxLayout.Y_AXIS));
    JScrollPane chatListScroll = new JScrollPane(chatListPanel,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    chatListScroll.setPreferredSize(new Dimension(160, 0));

    JButton newChatBtn = chatBtnFactory.getButton("+ New Chat");
    newChatBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, newChatBtn.getPreferredSize().height));
    newChatBtn.addActionListener(e -> handleNewChat());

    JPanel leftSidebar = new JPanel(new BorderLayout());
    leftSidebar.add(chatListScroll, BorderLayout.CENTER);
    leftSidebar.add(newChatBtn, BorderLayout.SOUTH);
    panel.add(leftSidebar, BorderLayout.WEST);

    // Right sidebar — connected users list
    JList<String> userList = new JList<>(userListModel);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane userListScroll = new JScrollPane(userList);
    userListScroll.setPreferredSize(new Dimension(160, 0));

    JPanel rightSidebar = new JPanel(new BorderLayout());
    rightSidebar.add(new JLabel("Connected Users", SwingConstants.CENTER), BorderLayout.NORTH);
    rightSidebar.add(userListScroll, BorderLayout.CENTER);
    panel.add(rightSidebar, BorderLayout.EAST);

    JPanel bottom = new JPanel(new BorderLayout());

    JPanel messageRow = new JPanel(new BorderLayout());
    messageRow.add(messageField, BorderLayout.CENTER);
    JPanel messageButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
    messageButtons.add(addAttachment);
    messageButtons.add(addEmoji);
    messageRow.add(messageButtons, BorderLayout.EAST);
    bottom.add(messageRow, BorderLayout.CENTER);

    JButton sendBtn = chatBtnFactory.getButton("Send");
    JButton logoutBtn = chatBtnFactory.getButton("Logout");
    JButton saveHistoryBtn = chatBtnFactory.getButton("Save History");

    JPanel buttons = new JPanel();
    buttons.add(logoutBtn);
    buttons.add(sendBtn);
    buttons.add(saveHistoryBtn);

    connectionIndicator.setForeground(new Color(160, 0, 0));
    connectionIndicator.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

    JPanel controls = new JPanel(new BorderLayout());
    controls.add(connectionIndicator, BorderLayout.WEST);
    controls.add(buttons, BorderLayout.CENTER);

    bottom.add(controls, BorderLayout.SOUTH);
    panel.add(bottom, BorderLayout.SOUTH);

    sendBtn.addActionListener(e -> handleSend());
    logoutBtn.addActionListener(e -> handleLogout());
    saveHistoryBtn.addActionListener(e -> handleSaveHistory());

    messageField.addActionListener(e -> handleSend());

    // Notification panel
    JLabel invitationTextLabel = new JLabel();
    JButton acceptButton = chatBtnFactory.getButton("Accept");
    JButton declineButton = chatBtnFactory.getButton("Decline");

    notificationPanel.setVisible(false); // Change to true when an notificatoin in received
    notificationPanel.add(invitationTextLabel);
    notificationPanel.add(acceptButton);
    notificationPanel.add(declineButton);
    panel.add(notificationPanel, BorderLayout.NORTH);

    return panel;
  }

  /**
   * Sends the message in the message field using Client.send(), then clears the
   * field.
   */
  private void handleSend() {
    String text = messageField.getText().trim();
    if (!text.isEmpty()) {
      Client.send(text, currentChat);
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
        for (Message message : groupManager.getGroupMessages(currentChat)) { // Write message in format [timestamp]
                                                                             // username: message
          writer.write("[" + message.getTimeStamp() + "] "
              + message.getUserName() + ": "
              + message.getContent() + "\n");
        }
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Save Error",
            JOptionPane.ERROR_MESSAGE);
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
      setConnected(true);
      this.clientUser = username;
      groupManager.addGroup("default");
      groupManager.addUser("default", username);
      addChat("default");
      showChatScreen();
    } catch (InvalidLoginException | IOException e) {
      showLoginError(e.getMessage());
    }
  }

  /**
   * Handles the registration process when the register button is pressed.
   * 
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
   * 
   * @param message The error message to display
   */

  void showLoginError(String message) {
    loginStatusLabel.setForeground(new Color(160, 0, 0)); // dark red
    loginStatusLabel.setText(message);
  }

  /**
   * Shows a login success message.
   * 
   * @param message The success message to display
   */
  void showLoginSuccess(String message) {
    loginStatusLabel.setForeground(new Color(0, 128, 0)); // green
    loginStatusLabel.setText(message);
  }

  /**
   * Shows a chat message in the chat area.
   * 
   * @param message The message to display
   */
  public void showChatMessage(Message message) {
    groupManager.addMessage(message.getGroup(), message);
    if (message.getGroup().equals(currentChat)) {
      chatArea.append(message.getUserName() + ": " + message.getContent() + "\n");
    }
  }

  public void clearExceptions() {
    loginStatusLabel.setText(" ");
  }

  /**
   * Updates the connection indicator in the bottom-left of the chat screen.
   *
   * @param connected true if connected to the server, false otherwise
   */
  public void setConnected(boolean connected) {
    if (connected) {
      connectionIndicator.setText("\u25CF Connected");
      connectionIndicator.setForeground(new Color(0, 128, 0));
    } else {
      connectionIndicator.setText("\u25CF Disconnected");
      connectionIndicator.setForeground(new Color(160, 0, 0));
    }
  }

  /**
   * Adds a user to a group in the GroupManager and updates the right sidebar if
   * the group is the current chat.
   *
   * @param group    the group to add the user to
   * @param username the user to add
   */
  public void addUserToChat(String group, String username) {
    groupManager.addUser(group, username);
    if (group.equals(currentChat)) {
      userListModel.addElement(username);
    }
  }

  /**
   * Adds a chat button to the left sidebar. Call this when a new chat is received
   * from the server.
   *
   * @param chatName the display name for the chat entry
   */
  public void addChat(String chatName) {
    JButton chatBtn = chatBtnFactory.getButton(chatName);
    chatBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, chatBtn.getPreferredSize().height));
    chatBtn.addActionListener(e -> openChat(chatName));
    chatListPanel.add(chatBtn);
    chatListPanel.revalidate();
    chatListPanel.repaint();
  }

  /**
   * Switches the chat area to the selected chat and reloads its message history
   * and member list.
   *
   * @param chatName the chat to open
   */
  private void openChat(String chatName) {
    currentChat = chatName;

    chatArea.setText("");
    List<Message> messages = groupManager.getGroupMessages(chatName);
    if (messages != null) {
      for (Message msg : messages) {
        chatArea.append(msg.getUserName() + ": " + msg.getContent() + "\n");
      }
    }

    userListModel.clear();
    List<String> members = groupManager.getGroupMembers(chatName);
    if (members != null) {
      for (String member : members) {
        userListModel.addElement(member);
      }
    }
  }

  /**
   * Handles creating a new chat when the "+ New Chat" button is pressed.
   * Implement when backend is ready.
   */
  private void handleNewChat() {
    JPanel newChatPrompt = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel("Chat Name: ");
    JTextField chatName = new JTextField(15);
    newChatPrompt.add(label);
    newChatPrompt.add(chatName);

    int result = JOptionPane.showConfirmDialog(this, newChatPrompt,
        "New Chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = chatName.getText().trim();
      if (!name.isEmpty() && !groupManager.hasGroup(name)) {
        groupManager.addGroup(name);
        groupManager.addUser(name, clientUser);
        addChat(name);
      }
    }
  }
}
