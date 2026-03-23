package Test;

import java.io.IOException;

import Utilities.InvalidLoginException;
import Utilities.RegistrationException;
import client.Client;

public class SocketCommunicationTest {
    public static void main(String[] args) throws RegistrationException, IOException, InvalidLoginException {
        Client.connect("jackson", "higgins1234");

        Client.send("Hello World");
        
    }
}
