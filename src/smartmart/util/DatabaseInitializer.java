package smartmart.util;

/**
 * Ensures the database exists and is seeded before the UI starts.
 *
 * <p>The actual location, migration and schema/seed logic now lives in a single
 * place — {@link DatabaseConnection} — so the file that gets seeded is always the
 * exact same file the app reads from and writes to. This class simply triggers
 * that initialization on startup.
 */
public class DatabaseInitializer {

    public static void initializeIfNeeded() {
        // Opening the shared connection creates and seeds the database on first
        // launch (and migrates any existing one). Nothing else to do here.
        DatabaseConnection.getInstance();
    }
}
