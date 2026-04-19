# Git Gabber – Secure Chat

Git Gabber is a secure, multi-user real-time chat application built in Java, developed as a course project for **CSC2620 Object Oriented Design** at Merrimack College. It demonstrates core OOP principles including multithreading, TCP socket networking, AES encryption, Swing GUI development, and design patterns such as the Abstract Factory pattern.

## Team Members
- Peter Hajj
- Jackson Higgins
- Matthew Palmer

## Features
- **User Authentication** – Register and log in with a username and password; credentials are stored locally
- **Real-Time Messaging** – Send and receive messages instantly over TCP sockets
- **Group Chat** – Create chat groups, invite other connected users, and switch between groups
- **AES Encryption** – All messages are encrypted before transmission using AES
- **File Attachments** – Share image attachments (PNG/JPG) in chat
- **Emoji Support** – Insert emojis directly into messages
- **Connection Monitoring** – A background heartbeat thread detects server disconnections and notifies the user
- **Themed UI** – Swing-based GUI with multiple visual themes and dynamic button styling

## Technologies Used
- **Java** – Core application language
- **Java Swing** – Desktop GUI framework
- **TCP Sockets** – Client-server communication over port 5001
- **AES Encryption** – `javax.crypto.Cipher` for message encryption/decryption
- **Multithreading** – Separate threads for sending, receiving, and connection monitoring
- **Git & GitHub** – Version control and collaboration

## Project Structure
```
SecureChat/
├── src/
│   ├── backend/
│   │   ├── Server.java           # Server entry point; manages connected clients and groups
│   │   └── ClientHandler.java    # Per-connection thread; routes and broadcasts messages
│   ├── client/
│   │   ├── App.java              # Application entry point
│   │   ├── Client.java           # Orchestrates login, connection, and messaging
│   │   ├── GUI.java              # Swing interface (chat panels, login screen, notifications)
│   │   ├── Sender.java           # Outgoing message thread
│   │   ├── Receiver.java         # Incoming message thread
│   │   ├── Encrypter.java        # Message encryption wrapper
│   │   ├── AESHelper.java        # Low-level AES cipher operations
│   │   ├── LoginHandler.java     # Credential validation logic
│   │   ├── CredentialHandler.java# Local credential file management
│   │   ├── Message.java          # Message data model
│   │   ├── Attachment.java       # File attachment serialization
│   │   └── ConnectionChecker.java# Server heartbeat / connection monitor
│   └── Utilities/
│       ├── GroupManager.java     # Client-side group membership and history
│       ├── MessageHelper.java    # Message serialization/deserialization
│       ├── ButtonFactory.java    # Abstract Factory base for UI buttons
│       ├── ChatButtonFactory.java
│       ├── LoginButtonFactory.java
│       ├── InvalidLoginException.java
│       └── RegistrationException.java
```

## How to Run

**Prerequisites:** Java (JDK 17+) and Visual Studio Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) installed.

### Start the Server
Open `src/backend/Server.java` in VS Code and run it using the **Run** button or `F5`. The server listens on port **5001**.

### Start the Client
Open `src/client/App.java` in VS Code and run it using the **Run** button or `F5`. A Swing window will open — register a new account or log in, then connect to the server to start chatting.
