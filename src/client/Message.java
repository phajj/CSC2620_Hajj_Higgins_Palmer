package client;

import java.time.LocalDateTime;
public class Message {
    private String content;
    private LocalDateTime timeStamp;
    private String userName;

    public Message(String content, String userName) {
        this.content = content;
        this.timeStamp = LocalDateTime.now();
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
    
    //TODO: sendMesesage and receiveMessage
    public void sendMessage(){
        
    }

    public void receiveMessage(){
        
    }
}
