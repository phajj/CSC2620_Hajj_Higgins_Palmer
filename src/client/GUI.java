package client;

import Utilities.ChatButtonFactory;
import Utilities.GroupManager;
import Utilities.InvalidLoginException;
import Utilities.LoginButtonFactory;
import Utilities.RegistrationException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;


/**
 * This class is responsible for creating the GUI for the client application.
 * 
 * @author Peter Hajj 
 * @author Jackson Higgins
 * @author Matthew Palmer
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
  private final JTextPane chatArea = new JTextPane();
  private final JTextField messageField = new JTextField();
  private final List<Attachment> pendingAttachments = new ArrayList<>();
  private final JLabel attachmentStatusLabel = new JLabel(" ");
  private final JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
  private final GroupManager groupManager = new GroupManager();
  // emoji name -> original image
  private final HashMap<String, BufferedImage> emojiImages = new HashMap<>();
  // pattern to match :emoji-name: tokens (allows letters, numbers, underscores and hyphens)
  private final Pattern emojiPattern = Pattern.compile(":(\\w[\\w-]*):");
  // Sidebar components (populate these when backend is ready):
  private final JPanel chatListPanel = new JPanel(); // holds chat buttons
  private final HashMap<String, JButton> chatButtons = new HashMap<>();

  // Borders for sidebar chat buttons.
  private static final javax.swing.border.Border CHAT_BTN_BORDER = BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(0, 90, 170), 1, true),
      BorderFactory.createEmptyBorder(5, 10, 5, 10));
  private static final javax.swing.border.Border CHAT_BTN_BORDER_SELECTED = BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(Color.WHITE, 2, true),
      BorderFactory.createEmptyBorder(4, 9, 4, 9));
  private final DefaultListModel<String> userListModel = new DefaultListModel<>();
  private JList<String> userList;
  private boolean darkTheme = false;
  private JToggleButton themeToggle;
  private String currentChat = "default";
  // Connection indicator:
  private final JLabel connectionIndicator = new JLabel("\u25CF Disconnected");
  // Tracks actual connection state so applyTheme() can restore the correct color.
  private boolean isConnected = false;
  private JButton addEmoji;
  private JButton addAttachment;

  // Notification panel components:
  private final JLabel invitationTextLabel = new JLabel();
  private final JButton acceptButton = chatBtnFactory.getButton("Accept");
  private final JButton declineButton = chatBtnFactory.getButton("Decline");
  // Leave chat button:
  private final JButton leaveChatBtn = chatBtnFactory.getButton("Leave Chat");
  // Invite user button:
  private final JButton inviteUserBtn = chatBtnFactory.getButton("Invite User");

  private String clientUser;
  private File lastAttachmentDirectory;

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

    // load emoji images for inline rendering
    loadEmojis();

    // load saved theme preference and apply
    Preferences prefs = Preferences.userNodeForPackage(GUI.class);
    darkTheme = prefs.getBoolean("darkTheme", false);
    applyTheme();

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

  public String getUsername() {
    return clientUser;
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
    // Assign to fields so applyTheme() can re-apply background after colorize().
    addEmoji = new JButton("");
    addAttachment = new JButton("");

    // Add icons to emoji and attachment buttons.
    ImageIcon emojiIcon = new ImageIcon(
        new ImageIcon("resources/happy-face.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    addEmoji.setIcon(emojiIcon);
    addEmoji.setBackground(Color.WHITE);
    addEmoji.setBorder(BorderFactory.createEmptyBorder());
    addEmoji.setContentAreaFilled(false);
    addEmoji.setBorderPainted(false);
    addEmoji.setFocusPainted(false);
    addEmoji.setOpaque(true);
    addEmoji.setRolloverEnabled(false);

    ImageIcon attachmentIcon = new ImageIcon(
        new ImageIcon("resources/attach-file.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    addAttachment.setIcon(attachmentIcon);
    addAttachment.setBackground(Color.WHITE);
    addAttachment.setBorder(BorderFactory.createEmptyBorder());
    addAttachment.setContentAreaFilled(false);
    addAttachment.setBorderPainted(false);
    addAttachment.setFocusPainted(false);
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
    userList = new JList<>(userListModel);
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
    attachmentStatusLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 0));
    bottom.add(attachmentStatusLabel, BorderLayout.NORTH);

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
    addAttachment.addActionListener(e -> handleAttachFile());
    addEmoji.addActionListener(e -> showEmojiPicker(addEmoji));

    messageField.addActionListener(e -> handleSend());

    // Notification panel

    notificationPanel.setVisible(false);
    notificationPanel.add(invitationTextLabel);
    notificationPanel.add(acceptButton);
    notificationPanel.add(declineButton);

    leaveChatBtn.setVisible(false);
    leaveChatBtn.addActionListener(e -> handleLeaveChat());

    inviteUserBtn.setVisible(false);
    inviteUserBtn.addActionListener(e -> handleInviteUser());

    themeToggle = new JToggleButton("Dark");
    themeToggle.setFocusable(false);
    themeToggle.addActionListener(e -> {
      darkTheme = themeToggle.isSelected();
      Preferences.userNodeForPackage(GUI.class).putBoolean("darkTheme", darkTheme);
      applyTheme();
    });

    JPanel topRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
    topRightButtons.add(themeToggle);
    topRightButtons.add(inviteUserBtn);
    topRightButtons.add(leaveChatBtn);

    JPanel topBar = new JPanel(new BorderLayout());
    topBar.add(notificationPanel, BorderLayout.CENTER);
    topBar.add(topRightButtons, BorderLayout.EAST);
    panel.add(topBar, BorderLayout.NORTH);

    return panel;
  }

  /**
   * Sends the message in the message field using Client.send(), then clears the
   * field.
   */
  private void handleSend() {
    String text = messageField.getText().trim();
    boolean hasText = !text.isEmpty();
    boolean hasAttachments = !pendingAttachments.isEmpty();

    if (!hasText && !hasAttachments) return;

    // Build a Message directly so attachments can be included
    Message message = new Message(hasText ? text : "", clientUser, currentChat);
    for (Attachment att : pendingAttachments) {
      message.addAttachment(att);
    }

    Client.send(message);
    messageField.setText("");
    pendingAttachments.clear();
    attachmentStatusLabel.setText("");
  }

  private void handleInvite(String invitedUser, String group) {
    Client.sendInvite(invitedUser, group);
  }

  /**
   * Prompts the user for a username and invites them to the current chat.
   */
  private void handleInviteUser() {
    JPanel prompt = new JPanel(new FlowLayout(FlowLayout.LEFT));
    prompt.add(new JLabel("Username: "));
    JTextField usernameInput = new JTextField(15);
    prompt.add(usernameInput);

    int result = JOptionPane.showConfirmDialog(this, prompt,
        "Invite to \"" + currentChat + "\"", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String invitedUser = usernameInput.getText().trim();
      if (!invitedUser.isEmpty()) {
        handleInvite(invitedUser, currentChat);
      }
    }
  }

  /**
   * handles creating GUI invite element
   */
  public void receiveInvite(String group, String inviter) {
    if (groupManager.hasGroup(group)) {
      return;
    }
    SwingUtilities.invokeLater(() -> {
      invitationTextLabel.setText(inviter + " invited you to join \"" + group + "\"");

      // Remove any previously registered listeners before adding new ones
      for (var l : acceptButton.getActionListeners())
        acceptButton.removeActionListener(l);
      for (var l : declineButton.getActionListeners())
        declineButton.removeActionListener(l);

      acceptButton.addActionListener(e -> {
        if (!groupManager.hasGroup(group)) {
          groupManager.addGroup(group);
        }
        addChat(group);
        openChat(group); // switch to the new chat so messages are sent to the right place
        Client.enterGroup(group); // notifies server; :members reply will populate the user list
        notificationPanel.setVisible(false);
      });

      declineButton.addActionListener(e -> notificationPanel.setVisible(false));

      notificationPanel.setVisible(true);
    });
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
              + message.getContent());
          if (message.hasAttachments()) {
            for (Attachment attachment : message.getAttachments()) {
              writer.write(" [file: " + attachment.getFileName() + "]");
            }
          }
          writer.write("\n");
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
      SwingUtilities.invokeLater(() -> {
        appendToChat(message.getUserName() + ": " + message.getContent() + "\n");
        if (message.hasAttachments()) {
          for (Attachment att : message.getAttachments()) {
            appendImageToChat(att.getData());
          }
        }
      });
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
    // Store state so applyTheme() can restore the correct color after a theme switch.
    isConnected = connected;
    if (connected) {
      connectionIndicator.setText("\u25CF Connected");
      // Force green regardless of theme or L&F defaults.
      connectionIndicator.setForeground(new Color(0, 160, 0));
    } else {
      connectionIndicator.setText("\u25CF Disconnected");
      // Force red regardless of theme or L&F defaults.
      connectionIndicator.setForeground(new Color(180, 0, 0));
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
      SwingUtilities.invokeLater(() -> userListModel.addElement(username));
    }
  }

  /**
   * Remove a user from a group. This updates the gui to not display their name in
   * the list of users in the chat
   *
   * @param username user being removed
   * @param group    group to remove the user from
   */
  public void removeUserFromChat(String group, String username) {
    groupManager.removeUser(group, username);
    if (group.equals(currentChat)) {
      SwingUtilities.invokeLater(() -> userListModel.removeElement(username));
    }
  }

  /**
   * Replaces the member list for a group with an authoritative list from the
   * server. Updates the right sidebar if the group is currently open.
   *
   * @param group   the group whose member list should be replaced
   * @param members the authoritative member list
   */
  public void setGroupMembers(String group, List<String> members) {
    groupManager.ensureGroup(group);
    List<String> current = groupManager.getGroupMembers(group);
    current.clear();
    for (String member : members) {
      current.add(member);
    }
    if (group.equals(currentChat)) {
      SwingUtilities.invokeLater(() -> {
        userListModel.clear();
        for (String member : members) {
          userListModel.addElement(member);
        }
      });
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
    // Apply the selected border if this chat is already active (e.g. "default" on login),
    // otherwise use the standard idle border. Both borders occupy the same total space so
    // toggling between them never causes the button to resize.
    chatBtn.setBorder(chatName.equals(currentChat) ? CHAT_BTN_BORDER_SELECTED : CHAT_BTN_BORDER);
    chatBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, chatBtn.getPreferredSize().height));
    chatBtn.addActionListener(e -> openChat(chatName));
    chatButtons.put(chatName, chatBtn);
    chatListPanel.add(chatBtn);
    // Small gap between chat entries for visual separation.
    chatListPanel.add(Box.createRigidArea(new Dimension(0, 4)));
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
    // Reset all chat buttons to the idle border, then highlight the active one.
    for (JButton b : chatButtons.values()) b.setBorder(CHAT_BTN_BORDER);
    JButton active = chatButtons.get(chatName);
    if (active != null) active.setBorder(CHAT_BTN_BORDER_SELECTED);

    boolean nonDefault = !chatName.equals("default");
    leaveChatBtn.setVisible(nonDefault);
    inviteUserBtn.setVisible(nonDefault);

    chatArea.setText("");
    List<Message> messages = groupManager.getGroupMessages(chatName);
    if (messages != null) {
      for (Message msg : messages) {
        appendToChat(msg.getUserName() + ": " + msg.getContent() + "\n");
        if (msg.hasAttachments()) {
          for (Attachment att : msg.getAttachments()) {
            appendImageToChat(att.getData());
          }
        }
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
   * Handles leaving the current chat: notifies the server, removes the group
   * locally, removes the sidebar button, and returns to the default chat.
   */
  private void handleLeaveChat() {
    Client.leaveGroup(currentChat);
    groupManager.removeGroup(currentChat);
    chatListPanel.remove(chatButtons.get(currentChat));
    chatListPanel.revalidate();
    chatListPanel.repaint();
    openChat("default");
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
      if (!name.isEmpty()) {
        groupManager.addGroup(name);
        Client.createGroup(name);     
        addChat(name);              
        openChat(name);               
        Client.enterGroup(name);
      }
    }
  }

  /**
   * Opens file select window and filters to PNG/JPG.
   * Reads the selected file and queues it as an attachment.
   */
  private void handleAttachFile() {
    JFileChooser chooser = new JFileChooser(lastAttachmentDirectory);
    // Filter file type
    chooser.setFileFilter(new FileNameExtensionFilter("Images (PNG, JPG)", "png", "jpg", "jpeg"));
    if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

    File file = chooser.getSelectedFile();
    lastAttachmentDirectory = file.getParentFile();
    try {
      byte[] data = Files.readAllBytes(file.toPath());
      String name = file.getName();
      String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
      String type = ext.equals("png") ? Attachment.PNG : Attachment.JPG;
      pendingAttachments.add(new Attachment(name, type, data));

      // Show all queued file names above the message input
      StringBuilder names = new StringBuilder();
      for (Attachment att : pendingAttachments) {
        if (names.length() > 0) names.append(", ");
        names.append(att.getFileName());
      }
      attachmentStatusLabel.setText("Attached: " + names);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Could not read file: " + e.getMessage(),
          "Attachment Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Appends plain text to the chat pane and scrolls to the bottom.
   */
  private void appendToChat(String text) {
    StyledDocument doc = chatArea.getStyledDocument();
    try {
      // parse text for :emojiName: tokens and render images inline
      Matcher matcher = emojiPattern.matcher(text);
      int last = 0;
      while (matcher.find()) {
        String before = text.substring(last, matcher.start());
        if (!before.isEmpty()) {
          doc.insertString(doc.getLength(), before, null);
        }

        String emojiName = matcher.group(1);
        // compute target height using font metrics so emoji lines up with baseline
        FontMetrics fm = chatArea.getFontMetrics(chatArea.getFont());
        int emojiHeight = Math.max(8, fm.getAscent() + 2);
        ImageIcon icon = getEmojiIcon(emojiName, emojiHeight);
        if (icon != null) {
          SimpleAttributeSet attrs = new SimpleAttributeSet();
          StyleConstants.setIcon(attrs, icon);
          // insert object-replacement char with icon attributes so it aligns with text
          doc.insertString(doc.getLength(), "\uFFFC", attrs);
        } else {
          // unknown emoji token - leave text unchanged
          doc.insertString(doc.getLength(), matcher.group(), null);
        }

        last = matcher.end();
      }

      // append remaining text after last emoji
      if (last < text.length()) {
        doc.insertString(doc.getLength(), text.substring(last), null);
      }
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    chatArea.setCaretPosition(doc.getLength());
  }

  /**
   * Loads available emoji images from resources/emojis into memory.
   */
  private void loadEmojis() {
    File dir = new File("resources/emojis");
    if (!dir.exists() || !dir.isDirectory()) return;
    File[] files = dir.listFiles((d, n) -> {
      String lower = n.toLowerCase();
      return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif");
    });
    if (files == null) return;
    for (File f : files) {
      try {
        BufferedImage img = ImageIO.read(f);
        if (img != null) {
          String name = f.getName();
          int idx = name.lastIndexOf('.');
          if (idx > 0) name = name.substring(0, idx);
          emojiImages.put(name, img);
        }
      } catch (IOException ignored) {
      }
    }
  }

  /**
   * Returns an ImageIcon scaled to the requested height maintaining aspect ratio.
   */
  private ImageIcon getEmojiIcon(String name, int targetHeight) {
    BufferedImage orig = emojiImages.get(name);
    if (orig == null) return null;
    int h = Math.max(8, targetHeight);
    int w = (int) Math.round(orig.getWidth() * (h / (double) orig.getHeight()));
    Image scaled = orig.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    return new ImageIcon(scaled);
  }

  /**
   * Applies the current theme (dark/light) across visible components.
   */
  private void applyTheme() {
    Color panelBg = darkTheme ? new Color(60, 63, 65) : Color.WHITE;
    Color bg = darkTheme ? new Color(43, 43, 43) : Color.WHITE;
    Color fg = darkTheme ? new Color(220, 220, 220) : Color.BLACK;
    Color btnBg = darkTheme ? new Color(75, 78, 80) : new Color(238, 238, 238);

    SwingUtilities.invokeLater(() -> {
      // apply general coloring recursively to the content pane
      colorize(this.getContentPane(), panelBg, fg);

      // key components
      chatArea.setBackground(bg);
      chatArea.setForeground(fg);
      chatArea.setCaretColor(fg);

      messageField.setBackground(darkTheme ? new Color(55, 58, 60) : Color.WHITE);
      messageField.setForeground(fg);

      attachmentStatusLabel.setForeground(fg);
      notificationPanel.setBackground(panelBg);

      chatListPanel.setBackground(panelBg);
      if (userList != null) {
        userList.setBackground(panelBg);
        userList.setForeground(fg);
      }

      // Re-apply the correct indicator color after colorize() has swept every component.
      // setConnected() is the single source of truth — never derive the color from the theme.
      setConnected(isConnected);

      // Re-apply icon button backgrounds after colorize() overwrites them.
      // contentAreaFilled=false means the component's own background color is what shows;
      // we match it to panelBg so the buttons blend with the toolbar in both themes.
      if (addEmoji != null)      { addEmoji.setBackground(panelBg);      addEmoji.setOpaque(true); }
      if (addAttachment != null) { addAttachment.setBackground(panelBg); addAttachment.setOpaque(true); }
      // Use the opposite theme's colors so the button previews what clicking it will do.
      Color toggleBg = darkTheme ? new Color(238, 238, 238) : new Color(75, 78, 80);
      Color toggleFg = darkTheme ? Color.BLACK           : new Color(220, 220, 220);
      if (themeToggle != null) { themeToggle.setSelected(darkTheme); themeToggle.setText(darkTheme ? "Dark" : "Light"); themeToggle.setBackground(toggleBg); themeToggle.setForeground(toggleFg); themeToggle.setOpaque(true); }
    });
  }

  private void colorize(Component comp, Color bg, Color fg) {
    if (comp instanceof JButton) return;
    if (comp instanceof JComponent) {
      JComponent jc = (JComponent) comp;
      jc.setBackground(bg);
      jc.setForeground(fg);
      if (jc instanceof JScrollPane) {
        ((JScrollPane) jc).getViewport().setBackground(bg);
      }
    }
    if (comp instanceof Container) {
      for (Component child : ((Container) comp).getComponents()) {
        colorize(child, bg, fg);
      }
    }
  }

  /**
   * Shows a popup emoji picker anchored to the given component.
   */
  private void showEmojiPicker(Component invoker) {
    JPopupMenu popup = new JPopupMenu();
    JPanel grid = new JPanel();
    int cols = 8;
    grid.setLayout(new GridLayout(0, cols, 4, 4));

    int iconSize = 28;
    if (!emojiImages.isEmpty()) {
      for (String name : emojiImages.keySet()) {
        ImageIcon icon = getEmojiIcon(name, iconSize);
        JButton btn = new JButton(icon);
        btn.setPreferredSize(new Dimension(iconSize + 6, iconSize + 6));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.addActionListener(e -> {
          String token = ":" + name + ":";
          int pos = messageField.getCaretPosition();
          String text = messageField.getText();
          if (pos < 0) pos = text.length();
          String newText = text.substring(0, Math.max(0, pos)) + token + text.substring(Math.max(0, pos));
          messageField.setText(newText);
          messageField.requestFocusInWindow();
          messageField.setCaretPosition(pos + token.length());
          popup.setVisible(false);
        });
        grid.add(btn);
      }
    } else {
      String[] fallback = new String[] { "\uD83D\uDE00", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE01", "\uD83D\uDE06", "\uD83D\uDE0A", "\uD83D\uDE42", "\uD83D\uDE09", "\uD83D\uDE0D", "\uD83D\uDE18" };
      for (String emoji : fallback) {
        JButton btn = new JButton(emoji);
        btn.setFont(btn.getFont().deriveFont(18f));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.addActionListener(e -> {
          int pos = messageField.getCaretPosition();
          String text = messageField.getText();
          if (pos < 0) pos = text.length();
          String newText = text.substring(0, Math.max(0, pos)) + emoji + text.substring(Math.max(0, pos));
          messageField.setText(newText);
          messageField.requestFocusInWindow();
          messageField.setCaretPosition(pos + emoji.length());
          popup.setVisible(false);
        });
        grid.add(btn);
      }
    }

    JScrollPane scroller = new JScrollPane(grid);
    scroller.setBorder(BorderFactory.createEmptyBorder());
    scroller.setPreferredSize(new Dimension(320, 200));
    popup.add(scroller);
    popup.show(invoker, 0, invoker.getHeight());
  }

  /**
   * Decodes raw image data, scales the image to a >= width of 200px, and places it in the chat pane.
   */
  private void appendImageToChat(byte[] data) {
    try {
      BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
      if (img == null) {
        appendToChat("[Image]\n");
        return;
      }
      int maxWidth = 200;
      int w = Math.min(img.getWidth(), maxWidth);
      int h = (int) (img.getHeight() * ((double) w / img.getWidth()));
      Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
      JLabel imgLabel = new JLabel(new ImageIcon(scaled));
      chatArea.setCaretPosition(chatArea.getDocument().getLength());
      chatArea.insertComponent(imgLabel);
      appendToChat("\n");
    } catch (IOException e) {
      appendToChat("[Image could not be displayed]\n");
    }
  }

}
