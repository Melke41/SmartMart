package smartmart.util;

import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class DatabaseInitializer {

    public static void initializeIfNeeded() {
        String dbPath = "database/smartmart.db";
        File dbFile = new File(dbPath);

        if (dbFile.exists()) {
            return; // database already exists, skip
        }

        System.out.println("First time setup — initializing database...");

        // create database folder if not exists
        new File("database").mkdirs();

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // read and execute schema.sql
            executeSQL(conn, "database/schema.sql");

            // read and execute seed.sql
            executeSQL(conn, "database/seed.sql");

            conn.close();
            System.out.println("Database initialized successfully.");

        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeSQL(Connection conn, String filePath) throws Exception {
        File sqlFile = new File(filePath);
        if (!sqlFile.exists()) {
            System.err.println("SQL file not found: " + filePath);
            return;
        }

        String content = new String(Files.readAllBytes(sqlFile.toPath()));

        // split by semicolon to get individual statements
        String[] statements = content.split(";");

        Statement stmt = conn.createStatement();
        for (String sql : statements) {
            String trimmed = sql.trim();
            if (!trimmed.isEmpty()) {
                try {
                    stmt.execute(trimmed);
                } catch (SQLException e) {
                    // ignore "table already exists" errors
                    if (!e.getMessage().contains("already exists")) {
                        System.err.println("SQL error: " + e.getMessage());
                    }
                }
            }
        }
        stmt.close();
    }
}
