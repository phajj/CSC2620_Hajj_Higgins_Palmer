package client;

import java.util.Base64;

/**
 * Represents a file or image attachment in Git Gabber.
 * It contains the file type, name, and raw data.
 * Serialize/deserialize attachment for socket sending.
 *
 * Wire segment format: {base64(fileName)}:{fileType}:{base64(data)}
 *
 * @author Peter Hajj
 */
public class Attachment {

    public static final String PNG = "png";
    public static final String JPG = "jpg";

    private String fileName; 
    private String fileType;  // "png" or "jpg"
    private byte[] data; 

    /**
     * @param fileName original file name
     * @param fileType "png" or "jpg" 
     * @param data raw file data
     */
    public Attachment(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Serialize the attachment into a colon seperated string.
     *
     * Format: {base64(fileName)}:{fileType}:{base64(data)}
     */
    public String serialize() {
        String encodedName = Base64.getEncoder().encodeToString(fileName.getBytes());
        String encodedData = Base64.getEncoder().encodeToString(data);
        return encodedName + ":" + fileType + ":" + encodedData;
    }

    /**
     * Reconstruct an Attachment from a segment produced by serialize().
     */
    public static Attachment deserialize(String segment) {
        String[] parts = segment.split(":", 3);
        String fileName = new String(Base64.getDecoder().decode(parts[0]));
        String fileType = parts[1];
        byte[] data = Base64.getDecoder().decode(parts[2]);
        return new Attachment(fileName, fileType, data);
    }
}
