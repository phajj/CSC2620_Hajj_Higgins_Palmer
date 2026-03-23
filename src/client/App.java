package client;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
/**
 *  This class is responsible for starting the client application.
 *  
 * @author Peter Hajj
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                String username = JOptionPane.showInputDialog("Enter your username:");

                // Validate username input
                if (username == null || username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                        "Username cannot be empty.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (username.length() > 20) {
                    JOptionPane.showMessageDialog(null, 
                        "Username cannot be longer than 20 characters.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create client GUI and start client application
                GUI gui;
                try{
                    gui = new GUI(username);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
