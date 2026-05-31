package smartmart.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIHelper {

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.FONT_BUTTON);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UIConstants.BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 80, 160));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        return button;
    }

    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.FONT_BUTTON);
        button.setBackground(new Color(204, 0, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UIConstants.BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(170, 0, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(204, 0, 0));
            }
        });
        return button;
    }

    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.FONT_BUTTON);
        button.setBackground(new Color(0, 153, 76));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UIConstants.BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 120, 60));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 153, 76));
            }
        });
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.FONT_BUTTON);
        button.setBackground(new Color(108, 117, 125));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UIConstants.BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 90, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(108, 117, 125));
            }
        });
        return button;
    }

    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(UIConstants.FONT_BODY);
        textField.setForeground(UIConstants.TEXT_PRIMARY);
        textField.setBackground(Color.WHITE);
        
        Border lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 205), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        textField.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));
        
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        return textField;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(UIConstants.FONT_BODY);
        passwordField.setForeground(UIConstants.TEXT_PRIMARY);
        passwordField.setBackground(Color.WHITE);
        
        Border lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 205), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        passwordField.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));
        
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        return passwordField;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_TITLE);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_SUBTITLE);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        return label;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(UIConstants.CARD_COLOR);
        
        Border lineBorder = BorderFactory.createLineBorder(new Color(220, 220, 225), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        card.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));
        return card;
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            "<html><body style='font-family: Segoe UI; font-size: 11pt; color: #006600;'><b>Success:</b> " + message + "</body></html>",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            "<html><body style='font-family: Segoe UI; font-size: 11pt; color: #990000;'><b>Error:</b> " + message + "</body></html>",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            "<html><body style='font-family: Segoe UI; font-size: 11pt; color: #cc6600;'><b>Warning:</b> " + message + "</body></html>",
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static JTable createStyledTable() {
        JTable table = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Alternating background colors
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                return c;
            }
        };

        table.setRowHeight(30);
        table.setFont(UIConstants.FONT_BODY);
        table.setGridColor(new Color(220, 220, 225));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        // Header customization
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(UIConstants.FONT_BUTTON);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));

        // Use custom renderer for header to keep the background colored nicely across Look & Feels
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(UIConstants.PRIMARY_COLOR);
                setForeground(Color.WHITE);
                setFont(UIConstants.FONT_BUTTON);
                setHorizontalAlignment(JLabel.LEFT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(200, 200, 205)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);

        return table;
    }
}
