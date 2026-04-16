package Utilities;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public class LoginButtonFactory extends ButtonFactory {

  // Matches ChatButtonFactory.BUTTON_BG for a consistent button color.
  private static final Color BACKGROUND = new Color(0, 120, 215);
  private static final Color FOREGROUND = Color.WHITE;

  @Override
  public JButton getButton(String text) {
    JButton button = new JButton(text);
    button.setFont(new Font("SansSerif", Font.BOLD, 14));
    button.setBackground(BACKGROUND);
    button.setForeground(FOREGROUND);

    button.setOpaque(true);
    button.setContentAreaFilled(true);

    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setRolloverEnabled(false);

    button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return button;
  }
}
