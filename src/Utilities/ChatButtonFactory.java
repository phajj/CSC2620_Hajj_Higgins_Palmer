package Utilities;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public class ChatButtonFactory extends ButtonFactory {
  private static final Color BACKGROUND = new Color(55, 65, 81);   // dark gray
  private static final Color FOREGROUND = new Color(229, 231, 235); // light gray text
  private static final Color HOVER_BG   = new Color(75, 85, 99);   // lighter gray on hover

  @Override
  public JButton getButton(String text) {
    JButton button = new JButton(text);
    button.setFont(new Font("SansSerif", Font.PLAIN, 12));
    button.setBackground(BACKGROUND);
    button.setForeground(FOREGROUND);
    button.setOpaque(true);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
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
