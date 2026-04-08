package client;

/**
 * The purpose of this class is to ping the server to check if it is online
 *
 * @author Jackson Higgins
 */

public class ConnectionChecker extends Thread {
  private Sender sender;
  private GUI gui;
  private static volatile boolean isConnected = true;
  private volatile boolean running = true;

  public ConnectionChecker(Sender sender, GUI gui) {
    this.sender = sender;
    this.gui = gui;
  }

  /**
   * Set the connection status of the server
   *
   * @param status indicates if we are connected to the server
   */
  public static void setConnectionStatus(Boolean status) {
    isConnected = status;
  }

  /**
   * Stops this thread from running
   */
  public void shutdown() {
    running = false;
    interrupt();
  }

  @Override
  public void run() {
    while (running) {
      try {
        isConnected = false;
        sender.ping();
        sleep(5000);
        gui.setConnected(isConnected);
      } catch (InterruptedException e) {
        break;
      } catch (Exception e) {
        e.printStackTrace();
        gui.setConnected(false);
      }
    }
  }
}
