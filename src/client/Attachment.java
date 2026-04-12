package client;

/**
 * This class represents an attachment to a message in Git Gaber.
 * It contains the file type of the attachment, and what message it is assiciated with.
 *
 * @author Peter Hajj
 */

public class Attachment {
    private String fileType;
    private Message msg;

    public Attachment(String fileType, Message msg){
        this.fileType = fileType;
        this.msg = msg;
    }

    public String getFileType(){
        return fileType;
    }

    public void setFileType(String fileType){
        this.fileType = fileType;
    }

    public Message getMessage(){
        return msg;
    }

    public void setMessage(Message msg){
        this.msg = msg;
    }
}
