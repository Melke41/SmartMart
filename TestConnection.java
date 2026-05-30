import smartmart.dao.ProductDAO;
import smartmart.dao.UserDAO;
import smartmart.model.Product;
import smartmart.model.User;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Starting Database Connection Integration Test...");
        Connection conn = null;
        try {
            // 1. Connect using DatabaseConnection.getInstance()
            conn = DatabaseConnection.getInstance();
            if (conn == null || conn.isClosed()) {
                System.err.println("Test Failed: Connection is null or closed.");
                System.exit(1);
            }
            System.out.println("Successfully connected to SQLite database.");

            // 2. Call ProductDAO.getAllProducts() and print each name and stock qty
            ProductDAO productDAO = new ProductDAO();
            List<Product> products = productDAO.getAllProducts();
            System.out.println("\n--- Product List from Database ---");
            for (Product p : products) {
                System.out.println("Product: " + p.getProductName() + " | Stock: " + p.getStockQty());
            }

            // 3. Call UserDAO to confirm 4 users exist
            UserDAO userDAO = new UserDAO();
            List<User> users = userDAO.getAllUsers();
            System.out.println("\n--- User Verification ---");
            System.out.println("Found " + users.size() + " users in the database.");
            if (users.size() == 4) {
                System.out.println("User count matches seed data (4 users).");
            } else {
                System.err.println("Test Failed: Expected 4 users, but found " + users.size());
                System.exit(1);
            }

            // 4. Print "DATABASE CONNECTION TEST PASSED"
            System.out.println("\nDATABASE CONNECTION TEST PASSED");

        } catch (Exception e) {
            System.err.println("Test Failed with Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            DatabaseConnection.closeConnection();
            System.out.println("Database connection closed.");
        }
    }
}
