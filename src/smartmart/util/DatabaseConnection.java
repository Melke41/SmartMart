package smartmart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static Connection getInstance() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    System.err.println("Driver Class not found: " + e.getMessage());
                }

                // Determine application directory (where the JAR/executable lives)
                String appDir = new java.io.File(DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

                java.io.File dbFile = new java.io.File(appDir, "database/smartmart.db");
                String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

                boolean initialize = !dbFile.exists();

                connection = DriverManager.getConnection(dbUrl);
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");

                if (initialize) {
                    try {
                        // Execute schema and seed SQL files shipped with the app
                        java.io.File schema = new java.io.File(appDir, "database/schema.sql");
                        java.io.File seed = new java.io.File(appDir, "database/seed.sql");
                        executeSqlScript(schema, connection);
                        executeSqlScript(seed, connection);
                        System.out.println("Database initialized from schema.sql and seed.sql");
                    } catch (Exception ex) {
                        System.err.println("Failed to initialize database schema: " + ex.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get connection: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while initializing DB: " + e.getMessage());
        }
        return connection;
    }

    private static void executeSqlScript(java.io.File sqlFile, Connection conn) {
        if (sqlFile == null || !sqlFile.exists()) return;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(sqlFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            String[] statements = sb.toString().split(";\s*\n");
            for (String stmt : statements) {
                String s = stmt.trim();
                if (s.isEmpty()) continue;
                try {
                    conn.createStatement().execute(s);
                } catch (SQLException ex) {
                    System.err.println("SQL statement failed: " + ex.getMessage());
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Failed to read SQL file: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        } finally {
            connection = null;
        }
    }
}
