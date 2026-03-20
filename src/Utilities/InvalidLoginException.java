package Utilities;


/**
 * This class is used to throw errors with detaiiled messages when login errors occur
 * 
 * @author Jackson Higgins
 */
public class InvalidLoginException extends Exception{
    public InvalidLoginException(String message) {
        super(message);
    }
}
