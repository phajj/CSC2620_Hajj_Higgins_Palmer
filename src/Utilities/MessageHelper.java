package Utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import client.Message;

/**
 * This is a helper class for the Message class. It has methods for converting the Message object to and from a string.
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
        StringBuilder sb = new StringBuilder();

        sb.append(timestampString).append(",").append(username).append(",").append(content); // format of {timestamp},{username},{content} for easy parsing
        
        return sb.toString();
    }

    /**
     * Converts a string representation of a message back into a string
     * 
     * @param messageString Message representation of a string
     * @return Message object
     */
    public Message toMessage(String messageString) {
        String[] parts = messageString.split(",", 3);
        String timeStampString = parts[0];
        String username = parts[1];
        String content = parts[2];
        LocalDateTime timeStamp = LocalDateTime.parse(timeStampString);

        Message message = new Message(content, timeStamp, username);

        return message;
    }
}
