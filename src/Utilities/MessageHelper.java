package Utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import client.Attachment;
import client.Message;

/**
 * This is a helper class for the Message class. It has methods for converting
 * the Message object to and from a string.
 * 
 * @author Jackson Higgins
 */
public class MessageHelper {
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  /**
   * Converts a Message object to an easily parsable string
   * 
   * @param message Message object to be turned into a string
   * @return String representation of the Message object
   */
  public String fromMessage(Message message) {
    String content = message.getContent();
    String username = message.getUserName();
    LocalDateTime timestamp = message.getTimeStamp();
    String timestampString = formatter.format(timestamp);
    String group = message.getGroup();
    StringBuilder sb = new StringBuilder();

    // {timestamp},{username},{content},{group}| {att1}|{att2} etc
    sb.append(timestampString).append(",").append(username).append(",").append(content).append(",").append(group);
    
    //for parsing:
    if (message.hasAttachments()) { 
      for (Attachment att : message.getAttachments()) {
        sb.append("|").append(att.serialize()); 
      }
    }

    return sb.toString();
  }

  /**
   * Converts a string representation of a message back into a string
   * 
   * @param messageString Message representation of a string
   * @return Message object
   */
  public Message toMessage(String messageString) {
    // Split on | to separate text messages from attachments
    String[] segments = messageString.split("\\|");

    String[] parts = segments[0].split(",", 4);
    String timeStampString = parts[0];
    String username = parts[1];
    String content = parts[2];
    String group = parts[3];
    LocalDateTime timeStamp = LocalDateTime.parse(timeStampString);

    Message message = new Message(content, timeStamp, username, group);

    // Deserialize any attachment segments
    for (int i = 1; i < segments.length; i++) {
      message.addAttachment(Attachment.deserialize(segments[i]));
    }

    return message;
  }

  public String convertEmojiCodes(String messageString) {
    // Simple implementation: replace ":smile:" with the corresponding unicode character
    // In a real implementation, you would want a more robust solution, possibly using regex and a map of codes to unicode
    return messageString.replace(":smile:", "\uD83D\uDE04")
                        .replace(":sad:", "\uD83D\uDE1E")
                        .replace(":heart:", "\u2764\uFE0F");
  }
}
