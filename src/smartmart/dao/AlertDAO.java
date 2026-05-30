package smartmart.dao;

import smartmart.model.Alert;
import smartmart.model.Category;
import smartmart.model.Product;
import smartmart.model.Supplier;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    public boolean createAlert(int productId, String message) throws SQLException {
        String query = "INSERT INTO alerts (product_id, message) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, productId);
            ps.setString(2, message);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<Alert> getUnresolvedAlerts() throws SQLException {
        List<Alert> list = new ArrayList<>();
        String query = "SELECT a.*, " +
                       "p.product_name, p.price, p.stock_qty, p.low_stock_limit, p.category_id, p.supplier_id, " +
                       "c.category_name, " +
                       "s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address " +
                       "FROM alerts a " +
                       "JOIN products p ON a.product_id = p.product_id " +
                       "JOIN categories c ON p.category_id = c.category_id " +
                       "JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                       "WHERE a.is_resolved = 0";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAlert(rs));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }

    public boolean resolveAlert(int alertId) throws SQLException {
        String query = "UPDATE alerts SET is_resolved = 1 WHERE alert_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, alertId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public void checkAndCreateLowStockAlerts() throws SQLException {
        ProductDAO productDAO = new ProductDAO();
        List<Product> lowStockProducts = productDAO.getLowStockProducts();
        Connection conn = DatabaseConnection.getInstance();

        String checkSql = "SELECT COUNT(*) FROM alerts WHERE product_id = ? AND is_resolved = 0";
        String insertSql = "INSERT INTO alerts (product_id, message) VALUES (?, ?)";

        PreparedStatement psCheck = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;
        try {
            psCheck = conn.prepareStatement(checkSql);
            psInsert = conn.prepareStatement(insertSql);

            for (Product p : lowStockProducts) {
                psCheck.setInt(1, p.getProductId());
                rs = psCheck.executeQuery();
                boolean exists = false;
                if (rs.next()) {
                    exists = (rs.getInt(1) > 0);
                }
                rs.close(); // Close rs immediately

                if (!exists) {
                    psInsert.setInt(1, p.getProductId());
                    psInsert.setString(2, "Low stock warning: " + p.getProductName() + " is down to " + p.getStockQty() + " (limit: " + p.getLowStockLimit() + ")");
                    psInsert.executeUpdate();
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (psCheck != null) {
                psCheck.close();
            }
            if (psInsert != null) {
                psInsert.close();
            }
        }
    }

    private Alert mapAlert(ResultSet rs) throws SQLException {
        Category cat = new Category(
                rs.getInt("category_id"),
                rs.getString("category_name")
        );
        Supplier sup = new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("supplier_phone"),
                rs.getString("supplier_email"),
                rs.getString("supplier_address")
        );
        Product prod = new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                cat,
                sup,
                rs.getDouble("price"),
                rs.getInt("stock_qty"),
                rs.getInt("low_stock_limit")
        );
        return new Alert(
                rs.getInt("alert_id"),
                prod,
                rs.getString("message"),
                rs.getInt("is_resolved") == 1,
                rs.getString("created_at")
        );
    }
}
