package smartmart.ui;

import smartmart.util.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;

public class MainApp {

    public static void main(String[] args) {
        // 1. Initialize SQLite database connection first
        System.out.println("Initializing database connection...");
        Connection conn = DatabaseConnection.getInstance();
        if (conn == null) {
            System.err.println("CRITICAL ERROR: Failed to establish database connection. Exiting application.");
            System.exit(1);
        }
        System.out.println("Database connection verified successfully.");

        // 2. Set Cross-Platform Look and Feel (Metal)
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Warning: Failed to set cross-platform Look & Feel. Falling back to default.");
            e.printStackTrace();
        }

        // 3. Launch Login Screen on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("CRITICAL ERROR: Failed to launch application UI.");
                    e.printStackTrace();
                }
            }
        });
    }
}
