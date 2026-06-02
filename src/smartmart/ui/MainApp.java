package smartmart.ui;

import smartmart.util.DatabaseConnection;
import smartmart.util.DatabaseInitializer;

import javax.swing.*;
import java.sql.Connection;

public class MainApp {

    public static void main(String[] args) {
        // Initialize database on first launch if needed
        DatabaseInitializer.initializeIfNeeded();

        // Set cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Test database connection
        DatabaseConnection.getInstance();

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
