package client;

import java.time.LocalDateTime;

/**
 * This class represents a message in the secure chat application.
 * It contains the content of the message, the timestamp of when it was sent, and the username of the sender.
 * 
 * @author Peter Hajj
 */

public class Message {
    private String content;
    private LocalDateTime timeStamp;
    private String userName;

    // Constructor for creating a new message (used when sending messages)
    public Message(String content, String userName) {
        this.content = content;
        this.timeStamp = LocalDateTime.now();
        this.userName = userName;
    }
    
    // Constructor for creating a message with a specific timestamp (used when receiving messages)
    public Message(String content, LocalDateTime timeStamp, String userName) {
        this.content = content;
        this.timeStamp = timeStamp;
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
