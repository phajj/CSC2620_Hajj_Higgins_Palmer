package Utilities;

/**
 * This class is used to throw errors with detaiiled messages when registration errors occur
 * 
 * @author Jackson Higgins
 */
public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }
}
