package client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a message in Git Gabber.
 * It contains the content of the message, the timestamp of when it was sent, 
 * attachments and the username of the sender.
 *
 * @author Peter Hajj
 */

public class Message {
  private String content;
  private LocalDateTime timeStamp;
  private String userName;
  private String group;
  private List<Attachment> attachments; // null when no attachments

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

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  public void addAttachment(Attachment attachment) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    attachments.add(attachment);
  }

  public boolean hasAttachments() {
    return attachments != null && !attachments.isEmpty();
  }
}
