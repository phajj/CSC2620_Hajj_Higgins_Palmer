package Utilities;

import javax.swing.JButton;

abstract class ButtonFactory extends JButton {
  public abstract JButton getButton(String text);
}
