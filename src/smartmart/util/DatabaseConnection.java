package smartmart.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {
    }

    /**
     * The single, canonical location of the database file.
     *
     * It lives under the user's home directory ({@code ~/.smartmart/smartmart.db})
     * so it is ALWAYS writable and ALWAYS the same file, regardless of how the
     * application is launched (IDE, run.bat, {@code java -jar}, or the installed
     * shortcut). Resolving the path from the code-source location used to break
     * because that path changes between launch methods, which is why saved data
     * appeared to "disappear".
     */
    public static File getDatabaseFile() {
        String home = System.getProperty("user.home");
        File dir = new File(home, ".smartmart");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "smartmart.db");
    }

    public static Connection getInstance() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    System.err.println("Driver Class not found: " + e.getMessage());
                }

                File dbFile = getDatabaseFile();
                boolean needsInit = !dbFile.exists() || dbFile.length() == 0;

                // Before creating an empty database, try to carry over an existing
                // one shipped with the app (preserves seeded users/products and any
                // data the user already entered).
                if (needsInit) {
                    File legacy = findLegacyDatabase();
                    if (legacy != null) {
                        try {
                            Files.copy(legacy.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            needsInit = false;
                            System.out.println("Migrated existing database from " + legacy.getAbsolutePath());
                        } catch (Exception ex) {
                            System.err.println("Could not migrate existing database: " + ex.getMessage());
                        }
                    }
                }

                String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
                connection = DriverManager.getConnection(dbUrl);
                try (Statement st = connection.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON;");
                }

                if (needsInit) {
                    initializeSchema(connection);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get connection: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while initializing DB: " + e.getMessage());
        }
        return connection;
    }

    /** Run schema.sql then seed.sql against a fresh database. */
    private static void initializeSchema(Connection conn) {
        try {
            executeSqlScript(loadSql("schema.sql"), conn);
            executeSqlScript(loadSql("seed.sql"), conn);
            System.out.println("Database initialized from schema.sql and seed.sql");
        } catch (Exception ex) {
            System.err.println("Failed to initialize database schema: " + ex.getMessage());
        }
    }

    /**
     * Load a bundled SQL script. Tries the classpath first (works inside the JAR),
     * then falls back to the {@code database/} folder in several candidate
     * locations so it also works when running from source or an install dir.
     */
    private static String loadSql(String name) throws Exception {
        // 1. Classpath resource (e.g. bundled in the JAR under /database/)
        InputStream in = DatabaseConnection.class.getResourceAsStream("/database/" + name);
        if (in == null) {
            in = DatabaseConnection.class.getResourceAsStream("/" + name);
        }
        if (in != null) {
            try (InputStream stream = in) {
                return new String(stream.readAllBytes());
            }
        }
        // 2. Filesystem fallback
        for (File base : candidateBaseDirs()) {
            File f = new File(base, "database/" + name);
            if (f.exists()) {
                return new String(Files.readAllBytes(f.toPath()));
            }
        }
        throw new java.io.FileNotFoundException("Could not locate " + name);
    }

    /** Look for a pre-existing smartmart.db shipped alongside the application. */
    private static File findLegacyDatabase() {
        for (File base : candidateBaseDirs()) {
            File f = new File(base, "database/smartmart.db");
            if (f.exists() && f.length() > 0) {
                return f;
            }
        }
        return null;
    }

    /** Directories that might contain the {@code database/} folder, in priority order. */
    private static java.util.List<File> candidateBaseDirs() {
        java.util.List<File> dirs = new java.util.ArrayList<>();
        // Current working directory (typical when launched from the project root)
        dirs.add(new File(System.getProperty("user.dir")));
        try {
            File codeSource = new File(DatabaseConnection.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            // The code source itself (classpath root or the JAR file)...
            File dir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;
            if (dir != null) {
                dirs.add(dir);
                if (dir.getParentFile() != null) {
                    dirs.add(dir.getParentFile()); // ...and one level up (e.g. project/out -> project)
                }
            }
        } catch (Exception ignored) {
            // code source not available (rare) — fall back to working directory only
        }
        return dirs;
    }

    private static void executeSqlScript(String script, Connection conn) {
        if (script == null || script.isBlank()) {
            return;
        }
        String[] statements = script.split(";\\s*\\n");
        for (String stmt : statements) {
            String s = stmt.trim();
            if (s.isEmpty()) {
                continue;
            }
            try (Statement st = conn.createStatement()) {
                st.execute(s);
            } catch (SQLException ex) {
                System.err.println("SQL statement failed: " + ex.getMessage());
            }
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
