package Utilities;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public class LoginButtonFactory extends ButtonFactory {
  private static final Color BACKGROUND = new Color(59, 130, 246);  // blue
  private static final Color FOREGROUND = Color.WHITE;
  private static final Color HOVER_BG   = new Color(37, 99, 235);   // darker blue on hover

  @Override
  public JButton getButton(String text) {
    JButton button = new JButton(text);
    button.setFont(new Font("SansSerif", Font.BOLD, 14));
    button.setBackground(BACKGROUND);
    button.setForeground(FOREGROUND);
    button.setOpaque(true);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    button.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
        button.setBackground(HOVER_BG);
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent e) {
        button.setBackground(BACKGROUND);
      }
    });

    return button;
  }
}
