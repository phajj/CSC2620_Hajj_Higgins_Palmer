package client;

import java.time.LocalDateTime;

/**
 * This class represents a message in Git Gabber.
 * It contains the content of the message, the timestamp of when it was sent,
 * and the username of the sender.
 * 
 * @author Peter Hajj
 */

public class Message {
  private String content;
  private LocalDateTime timeStamp;
  private String userName;
  private String group;

  // Constructor for creating a new message (used when sending messages)
  public Message(String content, String userName, String group) {
    this.content = content;
    this.timeStamp = LocalDateTime.now();
    this.userName = userName;
    this.group = group;
  }

  // Constructor for creating a message with a specific timestamp (used when
  // receiving messages)
  public Message(String content, LocalDateTime timeStamp, String userName, String group) {
    this.content = content;
    this.timeStamp = timeStamp;
    this.userName = userName;
    this.group = group;
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

  public String getGroup() {
    return this.group;
  }

  public void setGroup(String group) {
    this.group = group;
  }
}
