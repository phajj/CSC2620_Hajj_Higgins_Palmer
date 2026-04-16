package Utilities;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * Factory for chat-screen buttons. All buttons share a consistent blue style.
 */
public class ChatButtonFactory extends ButtonFactory {

  // Blue background used for every chat button in light/dark themes.
  static final Color BUTTON_BG = new Color(0, 120, 215);
  static final Color BUTTON_FG = Color.WHITE;

  @Override
  public JButton getButton(String text) {
    JButton button = new JButton(text);
    button.setFont(new Font("SansSerif", Font.PLAIN, 12));
    button.setBackground(BUTTON_BG);
    button.setForeground(BUTTON_FG);

    button.setOpaque(true);
    button.setContentAreaFilled(true);

    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setRolloverEnabled(false);

    button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return button;
  }
}
